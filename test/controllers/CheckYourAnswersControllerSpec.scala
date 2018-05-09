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

import connectors.{DataCacheConnector, FakeDataCacheConnector}
import play.api.test.Helpers._
import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeAuthAction, FakeDataRetrievalAction}
import forms.BankAccountClosingInterestForm
import identifiers.{CloseAccountId, ClosingInterestId, UpdateInterestId}
import models.CloseAccount
import models.bbsi.TaxYear
import org.joda.time.LocalDate
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.libs.json.{JsNull, JsString, Json}
import service.BBSIService
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.time.TaxYearResolver
import utils.{FakeNavigator, JourneyConstants}
import viewmodels.{AnswerSection, BankAccountViewModel, CloseBankAccountCheckAnswersViewModel, UpdateInterestViewModelCheckAnswers}
import views.html.{check_your_answers, close_account_check_your_answers}

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase  with JourneyConstants{

  private val id = 1
  private val id1 = 2
  private val interestAmount = "3000"
  private val bankName = "Test Name"
  private val currentTaxYear = TaxYear().year
  private val localDate = new LocalDate(currentTaxYear, 5, 3)
  private val previousTaxYear = currentTaxYear - 1
  private val localDate1 = new LocalDate(previousTaxYear, 5, 3)

  val closeAccount = CloseAccount("3", "5", currentTaxYear.toString)
  val closeAccount1 = CloseAccount("3", "5", previousTaxYear.toString)

  val bankAccountClosingInterestForm = BankAccountClosingInterestForm(Some("YesValue"), Some(interestAmount))

  val bankAccountViewModel = BankAccountViewModel(id, bankName)
  val bankAccountViewModel1 = BankAccountViewModel(id1, bankName)
  val closeAccountCheckAnswersViewModel = CloseBankAccountCheckAnswersViewModel(id, localDate.toString(), Some(bankName), Some(interestAmount))
  val closeAccountCheckAnswersViewModel1 = CloseBankAccountCheckAnswersViewModel(id, localDate1.toString(), Some(bankName), None)

  def onwardRoute = routes.ConfirmationController.onPageLoad()

  private val viewModel = UpdateInterestViewModelCheckAnswers(id, interestAmount, bankName)

  def controller(bbsiService: BBSIService, dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap, dataCacheConnector: DataCacheConnector = FakeDataCacheConnector) =
    new CheckYourAnswersController(frontendAppConfig, messagesApi, dataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction, dataRetrievalAction, new DataRequiredActionImpl, bbsiService)

  "Check Your Answers Controller onPageLoad function" must {
    val bbsiService = mock[BBSIService]
    val validData = Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel), UpdateInterestId.toString -> JsString("3000"))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

    "return 200 and the correct view for a GET" in {
      val result = controller(bbsiService = bbsiService, getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer).toString
    }

    "return Not Found when there is no Bank Account details present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(UpdateInterestId.toString -> JsString("3000")))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoad()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return Not found when there is no interest amount present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel)))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoad()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(bbsiService = bbsiService, dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
  "Check Your Answers Controller onSubmit function" must {
    val bbsiService = mock[BBSIService]
    val mockDataCacheConnector = mock[DataCacheConnector]
    val validData = Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel), UpdateInterestId.toString -> JsString("3000"))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

    "redirect to the next page when valid data is submitted" in {
      when(bbsiService.updateBankAccountInterest(any(), any(), any())(any())).thenReturn(Future.successful(EnvelopeIdKey))
      when(mockDataCacheConnector.flush(any())).thenReturn(Future.successful(true))
      val result = controller(bbsiService = bbsiService, getRelevantData, mockDataCacheConnector).onSubmit()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.ConfirmationController.onPageLoad().url)
    }

    "return bad request on submit when no bank details is present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(UpdateInterestId.toString -> JsString("3000")))))
      val result = controller(bbsiService = bbsiService, getNoData).onSubmit()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return bad request on submit when no interest amount is present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel)))))
      val result = controller(bbsiService = bbsiService, getNoData).onSubmit()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }
  }

  "Check Your Answers Controller onPageLoadClose function" must {
    val bbsiService = mock[BBSIService]
    val validDataWithInterestAmount = Map(
      BankAccountDetailsKey -> Json.toJson(bankAccountViewModel),
      CloseAccountId.toString -> Json.toJson(closeAccount),
      ClosingInterestId.toString -> Json.toJson(bankAccountClosingInterestForm))
    val validDataWithOutInterestAmount = Map(
      BankAccountDetailsKey -> Json.toJson(bankAccountViewModel),
      CloseAccountId.toString -> Json.toJson(closeAccount1))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validDataWithInterestAmount)))
    val getRelevantData1 = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validDataWithOutInterestAmount)))

    "return 200 and the correct view for a GET when interest amount is provided for current tax year" in {
        val result = controller(bbsiService = bbsiService, getRelevantData).onPageLoadClose()(fakeRequest)
        status(result) mustBe OK
        contentAsString(result) mustBe close_account_check_your_answers(frontendAppConfig, closeAccountCheckAnswersViewModel)(fakeRequest, messages, templateRenderer).toString
    }

    "return 200 and the correct view for a GET when interest amount is not provided for previous tax year" in {
        val result = controller(bbsiService = bbsiService, getRelevantData1).onPageLoadClose()(fakeRequest)
        status(result) mustBe OK
        contentAsString(result) mustBe close_account_check_your_answers(frontendAppConfig, closeAccountCheckAnswersViewModel1)(fakeRequest, messages, templateRenderer).toString
    }

    "return Not Found when there is no Bank Account details present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(CloseAccountId.toString -> Json.toJson(closeAccount),
        ClosingInterestId.toString -> Json.toJson(bankAccountClosingInterestForm)))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoadClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return Not found when there is no close account date in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel),
        ClosingInterestId.toString -> Json.toJson(bankAccountClosingInterestForm)))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoadClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return Not found when there is no interest amount and close account date falls with in the tax year in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel),
        CloseAccountId.toString -> Json.toJson(closeAccount)))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoadClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(bbsiService = bbsiService, dontGetAnyData).onPageLoadClose()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
  "Check Your Answers Controller onSubmitClose function" must {
    val bbsiService = mock[BBSIService]
    val mockDataCacheConnector = mock[DataCacheConnector]
    val validDataWithInterestAmount = Map(CloseAccountAnswersKey -> Json.toJson(closeAccountCheckAnswersViewModel))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validDataWithInterestAmount)))

    val validDataWithOutInterestAmount = Map(CloseAccountAnswersKey -> Json.toJson(closeAccountCheckAnswersViewModel1))
    val getRelevantData1 = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validDataWithOutInterestAmount)))
    "redirect to the next page when valid data with interest amount is submitted" in {
      when(bbsiService.closeBankAccount(any(), any(), any())(any())).thenReturn(Future.successful(EnvelopeIdKey))
      when(mockDataCacheConnector.flush(any())).thenReturn(Future.successful(true))
      val result = controller(bbsiService = bbsiService, getRelevantData, mockDataCacheConnector).onSubmitClose()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.ConfirmationController.onPageLoad().url)
    }

    "redirect to the next page when valid data with out interest amount is submitted" in {
      when(bbsiService.closeBankAccount(any(), any(), any())(any())).thenReturn(Future.successful(EnvelopeIdKey))
      when(mockDataCacheConnector.flush(any())).thenReturn(Future.successful(true))
      val result = controller(bbsiService = bbsiService, getRelevantData1, mockDataCacheConnector).onSubmitClose()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.ConfirmationController.onPageLoad().url)
    }

    "return Not Found on submit when no close account details is present in cache" in {
      val result = controller(bbsiService = bbsiService, getEmptyCacheMap).onSubmitClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }
  }
}
