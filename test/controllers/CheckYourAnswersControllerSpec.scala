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
import identifiers.UpdateInterestId
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.libs.json.{JsNull, JsString, Json}
import service.BBSIService
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, JourneyConstants}
import viewmodels.{AnswerSection, BankAccountViewModel, UpdateInterestViewModelCheckAnswers}
import views.html.check_your_answers

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase  with JourneyConstants{

  private val id = 1
  private val id1 = 2
  private val interestAmount = "3000"
  private val bankName = "Test Name"

  val bankAccountViewModel = BankAccountViewModel(id, bankName)
  val bankAccountViewModel1 = BankAccountViewModel(id1, bankName)

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
    val validData = Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel), UpdateInterestId.toString -> JsString("3000"))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

    "return 200 and the correct view for a GET" in {
      val result = controller(bbsiService = bbsiService, getRelevantData).onPageLoadClose()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer).toString
    }

    "return Not Found when there is no Bank Account details present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(UpdateInterestId.toString -> JsString("3000")))))
      val result = controller(bbsiService = bbsiService, getNoData).onPageLoadClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return Not found when there is no interest amount present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel)))))
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
    val validData = Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel), UpdateInterestId.toString -> JsString("3000"))
    val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

    "redirect to the next page when valid data is submitted" in {
      when(bbsiService.updateBankAccountInterest(any(), any(), any())(any())).thenReturn(Future.successful(EnvelopeIdKey))
      when(mockDataCacheConnector.flush(any())).thenReturn(Future.successful(true))
      val result = controller(bbsiService = bbsiService, getRelevantData, mockDataCacheConnector).onSubmitClose()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.ConfirmationController.onPageLoad().url)
    }

    "return bad request on submit when no bank details is present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(UpdateInterestId.toString -> JsString("3000")))))
      val result = controller(bbsiService = bbsiService, getNoData).onSubmitClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "return bad request on submit when no interest amount is present in cache" in {
      val getNoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(bankAccountViewModel)))))
      val result = controller(bbsiService = bbsiService, getNoData).onSubmitClose()(fakeRequest)
      status(result) mustBe NOT_FOUND
    }
  }
}
