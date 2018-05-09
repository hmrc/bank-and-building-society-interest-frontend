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

import models.{CheckMode, NormalMode}
import models.bbsi.TaxYear
import org.joda.time.LocalDate
import uk.gov.hmrc.play.views.formatting.Dates
import viewmodels.CloseBankAccountCheckAnswersViewModel
import views.behaviours.ViewBehaviours
import views.html.close_account_check_your_answers

class CloseCheckAnswersViewSpec extends ViewBehaviours {

  val messageKeyPrefix = ""

  val id = 1
  val currentTaxYear = TaxYear().year
  val closeBankAccountDate = new LocalDate(currentTaxYear, 5, 4)
  val closeBankAccountDate1 = new LocalDate(currentTaxYear - 1, 5, 4)
  val displayAmount = "4,000"
  val closeBankAccountName = "TEST NAME"
  val interestAmount = "4000"

  val viewModel = CloseBankAccountCheckAnswersViewModel(id, closeBankAccountDate.toString(), Some(closeBankAccountName), Some(interestAmount))
  val viewModel1 = CloseBankAccountCheckAnswersViewModel(id, closeBankAccountDate1.toString(), Some(closeBankAccountName), None)

  def createView = () => close_account_check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer)
  def createView1 = () => close_account_check_your_answers(frontendAppConfig, viewModel1)(fakeRequest, messages, templateRenderer)

  "CheckYourAnswers view when interest amount is provided" must {
    behave like normalPage(createView,
      messageKeyPrefix,
      Some(messages("checkYourAnswers")),
      Some(messages("checkYourAnswers")),
      Some(messages("closeAccount.preHeading")))
    behave like pageWithSubmitButton(createView, controllers.routes.CheckYourAnswersController.onSubmitClose().url, messages("confirmAndSend"))
    behave like pageWithBackLink(createView)
    behave like pageWithCancelLink(createView)
    behave like pageWithText(createView, messages("checkYourAnswers.confirmText"))
    behave like pageWithHeadingH2(createView, viewModel.closeBankAccountName.getOrElse(""))

    "display change links for close account journey" when {
      behave like pageWithChangeLink(createView, 1, controllers.routes.DecisionController.onPageLoad(NormalMode, id).url + "?edit=true")
      behave like pageWithChangeLink(createView, 2, controllers.routes.CloseAccountController.onPageLoad(CheckMode).url + "?edit=true")
      behave like pageWithChangeLink(createView, 3, controllers.routes.ClosingInterestController.onPageLoad(CheckMode).url + "?edit=true")
    }

    "display question lines for close account journey" when {
      behave like pageWithQuestionLine(createView, 1, messages("checkYourAnswers.whatYouToldUs"))
      behave like pageWithQuestionLine(createView, 2, messages("close.checkYourAnswers.rowTwo.question"))
      behave like pageWithQuestionLine(createView, 3, messages("close.checkYourAnswers.rowThree.question", currentTaxYear.toString))
    }

    "display Answer lines for close account journey" when {
      behave like pageWithAnswerLine(createView, 1, messages("close.checkYourAnswers.rowOne.answer"))
      behave like pageWithAnswerLine(createView, 2, Dates.formatDate(new LocalDate(viewModel.closeBankAccountDate)))
      behave like pageWithAnswerLine(createView, 3, "£" + displayAmount)
    }
  }

  "CheckYourAnswers view when interest amount is not provided" must {
    behave like normalPage(createView1,
      messageKeyPrefix,
      Some(messages("checkYourAnswers")),
      Some(messages("checkYourAnswers")),
      Some(messages("closeAccount.preHeading")))
    behave like pageWithSubmitButton(createView1, controllers.routes.CheckYourAnswersController.onSubmitClose().url, messages("confirmAndSend"))
    behave like pageWithBackLink(createView1)
    behave like pageWithCancelLink(createView1)
    behave like pageWithText(createView1, messages("checkYourAnswers.confirmText"))
    behave like pageWithHeadingH2(createView1, viewModel1.closeBankAccountName.getOrElse(""))

    "display change links for close account journey" when {
      behave like pageWithChangeLink(createView1, 1, controllers.routes.DecisionController.onPageLoad(NormalMode, id).url + "?edit=true")
      behave like pageWithChangeLink(createView1, 2, controllers.routes.CloseAccountController.onPageLoad(CheckMode).url + "?edit=true")
    }

    "display question lines for close account journey" when {
      behave like pageWithQuestionLine(createView1, 1, messages("checkYourAnswers.whatYouToldUs"))
      behave like pageWithQuestionLine(createView1, 2, messages("close.checkYourAnswers.rowTwo.question"))
    }

    "display Answer lines for close account journey" when {
      behave like pageWithAnswerLine(createView1, 1, messages("close.checkYourAnswers.rowOne.answer"))
      behave like pageWithAnswerLine(createView1, 2, Dates.formatDate(new LocalDate(viewModel1.closeBankAccountDate)))
    }
  }
}
