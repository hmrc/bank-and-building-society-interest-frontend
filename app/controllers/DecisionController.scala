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
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.DecisionFormProvider
import identifiers.DecisionId
import models.Mode
import models.Decision
import models.domain.BankAccount
import service.BBSIService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.{Enumerable, Navigator, UserAnswers}
import viewmodels.BankAccountViewModel
import views.html.decision

import scala.concurrent.Future

class DecisionController @Inject()(
                                    appConfig: FrontendAppConfig,
                                    override val messagesApi: MessagesApi,
                                    dataCacheConnector: DataCacheConnector,
                                    navigator: Navigator,
                                    authenticate: AuthAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    formProvider: DecisionFormProvider,
                                    bbsiService: BBSIService)
                                    (implicit templateRenderer: TemplateRenderer) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode, id: Int) = (authenticate andThen getData).async {
    implicit request =>

      val nino = request.externalId

      bbsiService.bankAccount(Nino(nino), id) flatMap {
        case Some(BankAccount(_, Some(_), Some(_), Some(bankName), _, _)) =>
          val viewModel = BankAccountViewModel(id, bankName)

          dataCacheConnector.save("cacheId","BankAccount", viewModel) map { _ =>
            val preparedForm = request.userAnswers.flatMap(_.decision) match {
              case None => form
              case Some(value) => form.fill(value)
            }
            Ok(decision(appConfig, preparedForm, mode, viewModel))
          }

        case Some(_) => throw new RuntimeException(s"Bank account does not contain name, number or sortcode for nino: [${nino}] and id: [$id]")
        case _ => Future.successful(NotFound)
      }
  }

  def onSubmit(mode: Mode, id: Int) = (authenticate andThen getData).async {
    implicit request =>
      val nino = request.externalId
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          bbsiService.bankAccount(Nino(nino), id) flatMap {
            case Some(BankAccount(_, Some(_), Some(_), Some(bankName), _, _)) =>
              val viewModel = BankAccountViewModel(id, bankName)
              Future.successful(BadRequest(decision(appConfig, formWithErrors, mode, viewModel)))
            case Some(_) => throw new RuntimeException(s"Bank account does not contain name, number or sortcode for nino: [${nino}] and id: [$id]")
            case _ => Future.successful(NotFound)
          }
        },
        (value) =>
          dataCacheConnector.save[Decision](request.externalId, DecisionId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(DecisionId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
