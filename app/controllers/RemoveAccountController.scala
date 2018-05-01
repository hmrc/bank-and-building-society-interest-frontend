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

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import connectors.DataCacheConnector
import service.BBSIService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.renderer.TemplateRenderer
import viewmodels.BankAccountViewModel
import views.html.removeAccount
import utils.JourneyConstants


import scala.concurrent.Future

class RemoveAccountController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         dataCacheConnector: DataCacheConnector,
                                         authenticate: AuthAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         bbsiService: BBSIService)
                                         (implicit templateRenderer: TemplateRenderer) extends FrontendController with I18nSupport with JourneyConstants {

  def onPageLoad = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(bankAccountViewModel) => Future.successful(Ok(removeAccount(appConfig, bankAccountViewModel)))
        case _ => Future.successful(NotFound)
      }
  }

  def onSubmit = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId

      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(bankAccountViewModel) => {
          bbsiService.removeBankAccount(Nino(nino), bankAccountViewModel.id) flatMap { _ =>
            dataCacheConnector.flush(nino).map { _ =>
              Redirect(controllers.routes.ConfirmationController.onPageLoad)
            }
          }
        }
        case _ => Future.successful(NotFound)
      }
  }
}
