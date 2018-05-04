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

package viewmodels

import models.CheckMode
import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.hmrc.play.views.formatting.{Dates, Money}
import uk.gov.hmrc.time.TaxYearResolver

case class CloseBankAccountCheckAnswersViewModel(id: Int, closeBankAccountDate: String, closeBankAccountName: Option[String], interestAmount: Option[String]) {

  def journeyConfirmationLines(implicit messages: Messages): Seq[CheckYourAnswersConfirmationLine] = {
    val confirmationLines = Seq(CheckYourAnswersConfirmationLine(
      Messages("checkYourAnswers.whatYouToldUs"),
      Messages("close.checkYourAnswers.rowOne.answer"),
      controllers.routes.DecisionController.onPageLoad(CheckMode, id).url),
      CheckYourAnswersConfirmationLine(
        Messages("close.checkYourAnswers.rowTwo.question"),
        Dates.formatDate(new LocalDate(closeBankAccountDate)),
        controllers.routes.DecisionController.onPageLoad(CheckMode, id).url))

    if (bankAccountClosedInCurrentTaxYear) {
      confirmationLines :+ CheckYourAnswersConfirmationLine(
        Messages("close.checkYourAnswers.rowThree.question", TaxYearResolver.currentTaxYear.toString),
        interestAmount
          .map(interest => Money.pounds(BigDecimal(interest)).toString().trim.replace("&pound;", "\u00A3"))
          .getOrElse(Messages("closeBankAccount.closingInterest.notKnown")),
        controllers.routes.DecisionController.onPageLoad(CheckMode, id).url)
    }
    else {
      confirmationLines
    }
  }

  val bankAccountClosedInCurrentTaxYear: Boolean = TaxYearResolver.fallsInThisTaxYear(LocalDate.parse(closeBankAccountDate))


}

object CloseBankAccountCheckAnswersViewModel {
  implicit val formats = Json.format[CloseBankAccountCheckAnswersViewModel]
}
