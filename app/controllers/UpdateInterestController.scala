/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.UpdateInterestFormProvider
import identifiers.UpdateInterestId
import models.Mode
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import service.BBSIService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.{JourneyConstants, Navigator, UserAnswers}
import viewmodels.{BankAccountViewModel, UpdateInterestViewModel}
import views.html.updateInterest

import scala.concurrent.Future

class UpdateInterestController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        bbsiService: BBSIService,
                                        formProvider: UpdateInterestFormProvider)(implicit templateRenderer: TemplateRenderer) extends FrontendController with JourneyConstants with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId
      val preparedForm = request.userAnswers.updateInterest match {
        case None => form
        case Some(value) => form.fill(value)
      }

      bbsiService.untaxedInterest(Nino(nino)) flatMap { untaxedInterest =>
        val bankAccountViewModel = dataCacheConnector.getEntry[BankAccountViewModel](nino, BankAccountDetailsKey)
        bankAccountViewModel map {
          case Some(BankAccountViewModel(id , _))=> {
            untaxedInterest.bankAccounts.find(_.id == id) match {
              case Some(bankAccount) =>
                val viewModel = UpdateInterestViewModel(id, untaxedInterest.amount, bankAccount.bankName.getOrElse(""))
                Ok(updateInterest(appConfig, preparedForm, mode, viewModel))
              case _ => NotFound
            }
          }
          case None => NotFound
        }
      }
  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          bbsiService.untaxedInterest(Nino(nino)) flatMap { untaxedInterest =>
            val bankAccountViewModel = dataCacheConnector.getEntry[BankAccountViewModel](nino, BankAccountDetailsKey)
            bankAccountViewModel flatMap {
              case Some(BankAccountViewModel(id, _)) => {
                untaxedInterest.bankAccounts.find(_.id == id) match {
                  case Some(bankAccount) =>
                    val viewModel = UpdateInterestViewModel(id, untaxedInterest.amount, bankAccount.bankName.getOrElse(""))
                    Future.successful(BadRequest(updateInterest(appConfig, formWithErrors, mode, viewModel)))
                  case _ => Future.successful(NotFound)
                }
              }
              case Some(_) => throw new RuntimeException(s"Bank account does not contain ID for nino: [${nino}]")
              case _ => Future.successful(NotFound)
            }
          }
        },
        (value) =>
          dataCacheConnector.save[String](nino, UpdateInterestId.toString, value) map (cacheMap =>
            Redirect(navigator.nextPage(UpdateInterestId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
