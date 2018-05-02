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
import play.api.libs.json.JsString
import service.BBSIService
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import viewmodels.{AnswerSection, BankAccountViewModel, UpdateInterestViewModelCheckAnswers}
import views.html.check_your_answers

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  private val id = 1
  private val interestAmount = "3000"
  private val bankName = "Test Name"

  val bankAccountViewModel = BankAccountViewModel(id, bankName)

  def onwardRoute = routes.ConfirmationController.onPageLoad()

  private val viewModel = UpdateInterestViewModelCheckAnswers(id, interestAmount, bankName)

  def controller(bbsiService: BBSIService, dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap, dataCacheConnector: DataCacheConnector = FakeDataCacheConnector) =
    new CheckYourAnswersController(frontendAppConfig, messagesApi, dataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction, dataRetrievalAction, new DataRequiredActionImpl, bbsiService)

  "Check Your Answers Controller" must {
    "return 200 and the correct view for a GET" in {
      val bbsiService = mock[BBSIService]
      val mockDataCacheConnector = mock[DataCacheConnector]
      val validData = Map(UpdateInterestId.toString -> JsString("3000"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      when(mockDataCacheConnector.getEntry[BankAccountViewModel](any(), any())(any())).thenReturn(Future.successful(Some(bankAccountViewModel)))
      val result = controller(bbsiService = bbsiService, getRelevantData, dataCacheConnector = mockDataCacheConnector).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe check_your_answers(frontendAppConfig, viewModel)(fakeRequest, messages,templateRenderer).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val bbsiService = mock[BBSIService]
      val result = controller(bbsiService = bbsiService, dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
