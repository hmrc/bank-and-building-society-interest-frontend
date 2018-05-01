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
import forms.CloseAccountFormProvider
import identifiers.CloseAccountId
import models.{CloseAccount, Mode}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import service.BBSIService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.{JourneyConstants, Navigator, UserAnswers}
import viewmodels.BankAccountViewModel
import views.html.closeAccount

import scala.concurrent.Future

class CloseAccountController @Inject()(appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  authenticate: AuthAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: CloseAccountFormProvider,
                                                  bbsiService: BBSIService)
                                                  (implicit templateRenderer: TemplateRenderer)
                                                  extends FrontendController with I18nSupport with JourneyConstants {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(bankAccountViewModel) => {
          val preparedForm = request.userAnswers.closeAccount match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Ok(closeAccount(appConfig, preparedForm, mode, bankAccountViewModel))
        }
        case _ => NotFound
      }

  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(closeAccount(appConfig, formWithErrors, mode, BankAccountViewModel(1,"")))),
        (value) =>
          dataCacheConnector.save[CloseAccount](request.externalId, CloseAccountId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CloseAccountId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
