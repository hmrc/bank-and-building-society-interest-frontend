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
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, JourneyConstants}
import connectors.{DataCacheConnector, FakeDataCacheConnector}
import controllers.actions._
import play.api.test.Helpers._
import forms.CloseAccountFormProvider
import identifiers.CloseAccountId
import models.{CheckMode, CloseAccount, NormalMode}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import service.BBSIService
import viewmodels.BankAccountViewModel
import views.html.closeAccount

import scala.concurrent.Future

class CloseAccountControllerSpec extends ControllerSpecBase with JourneyConstants {

  def onwardRoute = routes.IndexController.onPageLoad()

  val formProvider = new CloseAccountFormProvider()
  val form = formProvider()
  val viewModel = BankAccountViewModel(1, "testName")

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 dataCacheConnector: DataCacheConnector = FakeDataCacheConnector) =
    new CloseAccountController(frontendAppConfig, messagesApi, dataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction,
      dataRetrievalAction, new DataRequiredActionImpl, formProvider)

  def viewAsString(form: Form[_] = form) = closeAccount(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer).toString

  "CloseAccount Controller" must {

    "return OK and the correct view for a GET" in {
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(viewModel)))))
      val result = controller(dataRetrievalAction).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when a date has previously been entered" in {

      val validData = Map(CloseAccountId.toString -> Json.toJson(CloseAccount("01","10","2017")),BankAccountDetailsKey -> Json.toJson(viewModel))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
      contentAsString(result) mustBe viewAsString(form.fill(CloseAccount("01","10","2017")))
    }

    "return Not Found for a GET" in {
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map())))
      val result = controller(dataRetrievalAction).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe NOT_FOUND
    }

    "redirect to the next page when valid data is submitted" in {
      val mockDataCacheConnector = mock[DataCacheConnector]
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(viewModel)))))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("accountClosedDay", "01"), ("accountClosedMonth", "10"),("accountClosedYear","2017"))
      val validData = Map(closeAccountDateKey -> Json.toJson("2017-10-01"))
      when(mockDataCacheConnector.save(any(), any(), any())(any())).thenReturn(Future.successful(CacheMap(cacheMapId, validData)))

      val result = controller(dataRetrievalAction = dataRetrievalAction, dataCacheConnector = mockDataCacheConnector).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map(BankAccountDetailsKey -> Json.toJson(viewModel)))))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("accountClosedDay", "011"), ("accountClosedMonth", "123"),("accountClosedYear","20172"))
      val boundForm = form.bind(Map("accountClosedDay" -> "011", "accountClosedMonth" -> "123", "accountClosedYear" ->"20172"))

      val result = controller(dataRetrievalAction = dataRetrievalAction).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("accountClosedDay", "01"), ("accountClosedMonth", "10"),("accountClosedYear","2017"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
