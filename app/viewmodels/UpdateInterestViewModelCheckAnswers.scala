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
import play.api.i18n.Messages
import uk.gov.hmrc.play.views.formatting.Money

case class UpdateInterestViewModelCheckAnswers(id: Int, interestAmount: String, bankName: String) {

  def journeyConfirmationLines(implicit messages: Messages): Seq[CheckYourAnswersConfirmationLine] = {

    val updateLine = CheckYourAnswersConfirmationLine(
      Messages("checkYourAnswers.whatYouToldUs"),
      Messages("checkYourAnswers.rowOne.answer"),
      controllers.routes.DecisionController.onPageLoad(CheckMode, id).url)

    val interestLine = CheckYourAnswersConfirmationLine(
      Messages("checkYourAnswers.rowTwo"),
      Money.pounds(BigDecimal(interestAmount)).toString().replace("&pound;", "\u00A3"),
      controllers.routes.UpdateInterestController.onPageLoad(CheckMode).url)

    Seq(updateLine, interestLine)
  }
}
