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

import base.SpecBase
import controllers.routes
import forms.BankAccountClosingInterestForm
import identifiers._
import models.Decision.{Close, Remove}
import models._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.time.TaxYearResolver

class NavigatorSpec extends SpecBase with MockitoSugar with JourneyConstants {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.OverviewController.onPageLoad()
      }

      "go to RemoveAccount for the identifier remove in the route map" in {
        val mockUserAnswers = mock[UserAnswers]
        when(mockUserAnswers.decision).thenReturn(Some(Remove))
        navigator.nextPage(DecisionId,NormalMode)(mockUserAnswers) mustBe routes.RemoveAccountController.onPageLoad()
      }

      "go to CloseAccount for the identifier close in the route map" in {
        val mockUserAnswers = mock[UserAnswers]
        val mode = NormalMode
        when(mockUserAnswers.decision).thenReturn(Some(Close))
        navigator.nextPage(DecisionId,mode)(mockUserAnswers) mustBe routes.CloseAccountController.onPageLoad(mode)
      }

      "go to CheckYourAnswers for an account close date which is previous to the current tax year" in {
        val mockUserAnswers = mock[UserAnswers]
        when(mockUserAnswers.closeAccount).thenReturn(Some(CloseAccount("01","01",TaxYearResolver.currentTaxYear.toString)))
        navigator.nextPage(CloseAccountId,NormalMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoadClose()
      }

      "go to ClosingInterest for an account close date which is in the current tax year" in {
        val mockUserAnswers = mock[UserAnswers]
        val mode = NormalMode
        when(mockUserAnswers.closeAccount).thenReturn(Some(CloseAccount("07","04",TaxYearResolver.currentTaxYear.toString)))
        navigator.nextPage(CloseAccountId,mode)(mockUserAnswers) mustBe routes.ClosingInterestController.onPageLoad(mode)
      }

      "go to CheckYourAnswers for the identifier yes" in {
        val mockUserAnswers = mock[UserAnswers]
        when(mockUserAnswers.closingInterest).thenReturn(Some(BankAccountClosingInterestForm(Some("Yes"),Some("100"))))
        navigator.nextPage(ClosingInterestId,NormalMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoadClose()
      }
    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
