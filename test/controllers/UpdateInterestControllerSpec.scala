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

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, JourneyConstants}
import connectors.{DataCacheConnector, FakeDataCacheConnector}
import controllers.actions._
import play.api.test.Helpers._
import forms.UpdateInterestFormProvider
import identifiers.UpdateInterestId
import models.{CheckMode, NormalMode}
import models.domain.{BankAccount, UntaxedInterest}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import service.BBSIService
import viewmodels.{BankAccountViewModel, UpdateInterestViewModel}
import views.html.updateInterest

import scala.concurrent.Future

class UpdateInterestControllerSpec extends ControllerSpecBase with JourneyConstants{

  def onwardRoute = routes.CheckYourAnswersController.onPageLoad()

  val formProvider = new UpdateInterestFormProvider()
  val form = formProvider()
  private val id = 1
  private val id1 = 3
  private val amount: BigDecimal = 20.3
  private val bankName = "Test Name"
  private val accountNumber = "Test Account Number"
  private val sortCode = "Test Sort Code"
  private val source = "Test Source"

  val bankAccountViewModel = BankAccountViewModel(id, bankName)
  val bankAccountViewModel1 = BankAccountViewModel(id1, bankName)
  val untaxedInterest = UntaxedInterest(amount, Seq(BankAccount(id, Some(accountNumber), Some(sortCode), Some(bankName), amount, Some(source))))
  val viewModel = UpdateInterestViewModel(id, amount, bankName)
  val bankAccount = BankAccount(id, Some(accountNumber), Some(sortCode), Some(bankName), amount, Some(source))

  def controller(bbsiService: BBSIService,
                 dataCacheConnector: DataCacheConnector = FakeDataCacheConnector,
                 dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new UpdateInterestController(frontendAppConfig, messagesApi, dataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction,
      dataRetrievalAction, new DataRequiredActionImpl, bbsiService, formProvider)

  def viewAsString(form: Form[_] = form) = updateInterest(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer).toString

  val testAnswer = "answer"

  "UpdateInterest Controller" must {

    "return OK and the correct view for a GET" in {
      val mockDataCacheConnector = mock[DataCacheConnector]
      val bbsiService = mock[BBSIService]
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(Some(bankAccountViewModel)))


      val result = controller(bbsiService = bbsiService, mockDataCacheConnector).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return Not Found when there is no Bank Account with the matching id from cache" in {
      val mockDataCacheConnector = mock[DataCacheConnector]
      val bbsiService = mock[BBSIService]
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(Some(bankAccountViewModel1)))


      val result = controller(bbsiService = bbsiService, mockDataCacheConnector).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

    "return Not found when there is no data present in cache" in {
      val mockDataCacheConnector = mock[DataCacheConnector]
      val bbsiService = mock[BBSIService]
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(None))


      val result = controller(bbsiService = bbsiService, mockDataCacheConnector).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val prePop = form.fill(testAnswer)
      val validData = Map(UpdateInterestId.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(Some(bankAccountViewModel)))
      val result = controller(bbsiService = bbsiService, mockDataCacheConnector, getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(prePop)
    }

    "redirect to the next page when valid data is submitted" in {
      val bbsiService = mock[BBSIService]
      val postRequest = fakeRequest.withFormUrlEncodedBody(("updateInterest", 120.toString))

      val result = controller(bbsiService = bbsiService).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val postRequest = fakeRequest.withFormUrlEncodedBody(("updateInterest", ""))
      val boundForm = form.bind(Map("updateInterest" -> ""))

      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))

      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(Some(bankAccountViewModel)))
      val result = controller(bbsiService = bbsiService, mockDataCacheConnector).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val result = controller(bbsiService = bbsiService, mockDataCacheConnector, dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val postRequest = fakeRequest.withFormUrlEncodedBody(("updateInterest", testAnswer))
      val result = controller(bbsiService = bbsiService, mockDataCacheConnector, dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
