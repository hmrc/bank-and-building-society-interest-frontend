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

package utils

import controllers.routes
import models.CheckMode
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def updateInterest: Option[AnswerRow] = userAnswers.updateInterest map { interestAmount =>
    AnswerRow("updateInterest.checkYourAnswersLabel", s"$interestAmount", false, routes.UpdateInterestController.onPageLoad(CheckMode).url)
  }

  def closingInterest: Option[AnswerRow] = userAnswers.closingInterest map { bankAccountClosingInterestForm =>
    AnswerRow("closingInterest.checkYourAnswersLabel",
      s"${bankAccountClosingInterestForm.closingBankAccountInterestChoice} ${bankAccountClosingInterestForm.closingInterestEntry}",
      true, routes.ClosingInterestController.onPageLoad(CheckMode).url)
  }

  def closeAccount: Option[AnswerRow] = userAnswers.closeAccount map { accountClose =>
    AnswerRow("closeAccount.checkYourAnswersLabel", s"${accountClose.accountClosedDay} ${accountClose.accountClosedMonth} ${accountClose.accountClosedYear}",
      false, routes.CloseAccountController.onPageLoad(CheckMode).url)
  }

  def decision: Option[AnswerRow] = userAnswers.decision map { choice =>
    AnswerRow("decision.checkYourAnswersLabel", s"decision.$choice", true, routes.DecisionController.onPageLoad(CheckMode, 0).url)
  }
}
