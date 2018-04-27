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

import connectors.DataCacheConnector
import controllers.actions._
import models.NormalMode
import models.domain.BankAccount
import models.requests.{DataRequest, OptionalDataRequest}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.test.Helpers._
import service.BBSIService
import uk.gov.hmrc.http.cache.client.CacheMap
import viewmodels.BankAccountViewModel
import views.html.removeAccount

import scala.concurrent.Future

class RemoveAccountControllerSpec extends ControllerSpecBase {

  private val bankName = "TestName"
  private val id = 1
  val viewModel = BankAccountViewModel(id, bankName)
  val bankAccount = BankAccount(id,
    Some("accountnumber"),
    Some("sortcode"),
    Some(bankName),
    0,
    Some("source"))

  def controller(
                  dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                  bbsiService: BBSIService) =
    new RemoveAccountController(
      frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      bbsiService)

  def viewAsString() = removeAccount(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer).toString

  "RemoveAccount Controller" must {

    "return OK and the correct view for a GET" in {
      val bbsiService = mock[BBSIService]
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map("BankAccount" -> Json.toJson(viewModel)))))
      val result = controller(dataRetrievalAction = dataRetrievalAction, bbsiService = bbsiService).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return Not Found for a GET" in {
      val bbsiService = mock[BBSIService]
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map())))
      val result = controller(dataRetrievalAction = dataRetrievalAction, bbsiService = bbsiService).onPageLoad(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

    "redirect to the next page when valid data is submitted" in {
      val bbsiService = mock[BBSIService]
      val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map("BankAccount" -> Json.toJson(viewModel)))))
      when(bbsiService.removeBankAccount(any(), any())(any())).thenReturn(Future.successful("envelopeId"))
      val result = controller(dataRetrievalAction = dataRetrievalAction, bbsiService = bbsiService).onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.ConfirmationController.onPageLoad().url)
    }

  }
}




