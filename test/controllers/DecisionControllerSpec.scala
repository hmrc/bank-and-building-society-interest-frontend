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
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import connectors.{DataCacheConnector, FakeDataCacheConnector}
import controllers.actions._
import play.api.test.Helpers._
import forms.DecisionFormProvider
import identifiers.DecisionId
import models.NormalMode
import models.Decision
import models.domain.BankAccount
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, times, when}
import service.BBSIService
import viewmodels.BankAccountViewModel
import views.html.decision

import scala.concurrent.Future

class DecisionControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.RemoveAccountController.onPageLoad()

  val formProvider = new DecisionFormProvider()
  val form = formProvider()
  private val id = 1
  private val bankName = "TestName"
  val viewModel = BankAccountViewModel(id, bankName)
  val bankAccount = BankAccount(id,
    Some("accountnumber"),
    Some("sortcode"),
    Some(bankName),
    0,
    Some("source"))

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 bbsiService: BBSIService,
                 fakeNavigator: FakeNavigator = new FakeNavigator(desiredRoute = onwardRoute),
                 dataCacheConnector: DataCacheConnector = FakeDataCacheConnector) =
    new DecisionController(
      frontendAppConfig,
      messagesApi,
      dataCacheConnector,
      fakeNavigator,
      FakeAuthAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      bbsiService)

  def viewAsString(form: Form[_] = form) = decision(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer).toString

  "Decision Controller" must {

    "return OK and the correct view for a GET" in {
      val bbsiService = mock[BBSIService]
      when(bbsiService.bankAccount(any(), any())(any())).thenReturn(Future.successful(Some(bankAccount)))
      val result = controller(bbsiService = bbsiService).onPageLoad(NormalMode, id)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "cache bank account details" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val bankAccountViewModel = BankAccountViewModel(id, bankName)
      val validData = Map("BankAccount" -> Json.toJson(bankAccountViewModel))
      when(bbsiService.bankAccount(any(), any())(any())).thenReturn(Future.successful(Some(bankAccount)))
      when(mockDataCacheConnector.save(any(), any(), any())(any())).thenReturn(Future.successful(CacheMap(cacheMapId, validData)))

      val result = controller(bbsiService = bbsiService, dataCacheConnector = mockDataCacheConnector).onPageLoad(NormalMode, id)(fakeRequest)
      status(result) mustBe OK
      verify(mockDataCacheConnector, times(1)).save(any(), any(), any())(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val bbsiService = mock[BBSIService]
      val validData = Map(DecisionId.toString -> JsString(Decision.values.head.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      when(bbsiService.bankAccount(any(), any())(any())).thenReturn(Future.successful(Some(bankAccount)))

      val result = controller(getRelevantData, bbsiService).onPageLoad(NormalMode, id)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(Decision.values.head))
    }

    "redirect to the next page when valid data is submitted" in {
      val bbsiService = mock[BBSIService]
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", Decision.options.head.value))

      val result = controller(bbsiService = bbsiService).onSubmit(NormalMode, id)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val bbsiService = mock[BBSIService]
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      when(bbsiService.bankAccount(any(), any())(any())).thenReturn(Future.successful(Some(bankAccount)))
      val result = controller(bbsiService = bbsiService).onSubmit(NormalMode, id)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}
