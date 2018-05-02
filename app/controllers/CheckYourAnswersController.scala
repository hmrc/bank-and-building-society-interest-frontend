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

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import utils.{CheckYourAnswersHelper, Enumerable, JourneyConstants, Navigator}
import viewmodels.{AnswerSection, BankAccountViewModel, UpdateInterestViewModelCheckAnswers}
import views.html.check_your_answers
import config.FrontendAppConfig
import connectors.DataCacheConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.renderer.TemplateRenderer
import models.Mode

import scala.concurrent.Future

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           navigator: Navigator,
                                           authenticate: AuthAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction)
                                          (implicit templateRenderer: TemplateRenderer) extends FrontendController with I18nSupport with JourneyConstants with Enumerable.Implicits{

  def onPageLoad() = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val updateInterest = request.userAnswers.updateInterest
      val decisionAnswer = request.userAnswers.decision
      val bankAccountViewModel = dataCacheConnector.getEntry[BankAccountViewModel](request.externalId, BankAccountDetailsKey)

      bankAccountViewModel map {
        case Some(BankAccountViewModel(id, _)) => {
          val viewModel = UpdateInterestViewModelCheckAnswers(id, updateInterest.getOrElse(""), decisionAnswer.get.toString)
          Ok(check_your_answers(appConfig, viewModel))
        }
        case _ => NotFound
      }
  }
}
