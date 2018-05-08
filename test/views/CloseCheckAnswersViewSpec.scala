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
  val closeBankAccountDate = new LocalDate(currentTaxYear, 4, 4)
  val closeBankAccountName = "TEST NAME"
  val interestAmount = "4000"

  val viewModel = CloseBankAccountCheckAnswersViewModel(id, closeBankAccountDate.toString(), Some(closeBankAccountName), Some(interestAmount))

  def createView = () => close_account_check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer)

  "CheckYourAnswers view when interest amount is not provided" must {
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
    }

    "display question lines for close account journey" when {
      behave like pageWithQuestionLine(createView, 1, messages("checkYourAnswers.whatYouToldUs"))
      behave like pageWithQuestionLine(createView, 2, messages("close.checkYourAnswers.rowTwo.question"))
    }

    "display Answer lines for close account journey" when {
      behave like pageWithAnswerLine(createView, 1, messages("close.checkYourAnswers.rowOne.answer"))
      behave like pageWithAnswerLine(createView, 2, Dates.formatDate(new LocalDate(viewModel.closeBankAccountDate)))
    }
  }
}
