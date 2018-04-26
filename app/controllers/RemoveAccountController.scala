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
import uk.gov.hmrc.renderer.TemplateRenderer
import viewmodels.BankAccountViewModel
import views.html.removeAccount

import scala.concurrent.Future

class RemoveAccountController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         authenticate: AuthAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction)
                                         (implicit templateRenderer: TemplateRenderer) extends FrontendController with I18nSupport {

  def onPageLoad = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val nino = request.externalId

      request.userAnswers.cacheMap.getEntry[BankAccountViewModel]("BankAccount") match {
        case Some(bankAccountViewModel) => Ok(removeAccount(appConfig, bankAccountViewModel))
        case _ => NotFound
      }
  }
}
