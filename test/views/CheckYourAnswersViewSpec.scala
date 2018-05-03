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

package views

import models.CheckMode
import play.api.data.Form
import viewmodels.UpdateInterestViewModelCheckAnswers
import views.behaviours.ViewBehaviours
import views.html.check_your_answers

class CheckYourAnswersViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "checkYourAnswers"

  private val id = 1
  private val interestAmount = "300"
  private val bankName = "Test Name"


  val viewModel = UpdateInterestViewModelCheckAnswers(id, interestAmount, bankName)

  def createView = () => check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer)

  def createViewUsingForm = (form: Form[String]) => check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer)


  "CheckYourAnswers view" must {
    behave like normalPage(createView,
      messageKeyPrefix,
      Some(messages("checkYourAnswers")),
      Some(messages("checkYourAnswers")),
      Some(messages("updateInterest.checkYourAnswers.preHeading")))
    behave like pageWithSubmitButton(createView, controllers.routes.CheckYourAnswersController.onSubmit().url, messages("confirmAndSend"))
    behave like pageWithBackLink(createView)
    behave like pageWithCancelLink(createView)
    behave like pageWithText(createView, messages("checkYourAnswers.confirmText"))
    behave like pageWithHeadingH2(createView, viewModel.bankName)

    "display change links for Update Interest journey" when {
      behave like pageWithChangeLink(createView, 1, controllers.routes.DecisionController.onPageLoad(CheckMode, id).url + "?edit=true")
      behave like pageWithChangeLink(createView, 2, controllers.routes.UpdateInterestController.onPageLoad(CheckMode).url + "?edit=true")
    }

    "display change links for Update Interest journey" when {
      behave like pageWithQuestionLine(createView, 1, messages("checkYourAnswers.whatYouToldUs"))
      behave like pageWithQuestionLine(createView, 2, messages("checkYourAnswers.rowTwo"))
    }

    "display change links for Update Interest journey" when {
      behave like pageWithAnswerLine(createView, 1, messages("checkYourAnswers.rowOne.answer"))
      behave like pageWithAnswerLine(createView, 2, "£" + viewModel.interestAmount)
    }
  }



}
