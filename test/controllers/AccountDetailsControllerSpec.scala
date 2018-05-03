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
import controllers.actions.{DataRetrievalAction, FakeAuthAction}
import models.domain.UntaxedInterest
import org.jsoup.Jsoup
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import play.api.test.Helpers.{contentAsString, status, _}
import service.BBSIService
import views.html.account_details

import scala.concurrent.Future

class AccountDetailsControllerSpec extends ControllerSpecBase {

  def controller(
                  dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                  bbsiService: BBSIService = bbsiService,
                  dataCacheConnector: DataCacheConnector = FakeDataCacheConnector) =
    new AccountDetailsController(
      frontendAppConfig,
      messagesApi,
      dataCacheConnector,
      FakeAuthAction,
      bbsiService)

  "Account Details Controller" must {
    "return 200 for a GET" in {
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe account_details(untaxedInterest,frontendAppConfig)(fakeRequest, messages, templateRenderer).toString
      val doc = Jsoup.parse(contentAsString(result))
      doc.title() must include(messages("accountDetails.heading"))
    }

    "flush cache and redirect to account details page" in {
      when(dataCacheConnector.flush(any())).thenReturn(Future.successful(true))
      val result = controller(dataCacheConnector = dataCacheConnector).cancelJourney()(fakeRequest)
      status(result) mustBe SEE_OTHER
      verify(dataCacheConnector, times(1)).flush(any())
      redirectLocation(result).get mustBe controllers.routes.AccountDetailsController.onPageLoad().url
    }
  }

  private val untaxedInterest = UntaxedInterest(0, Seq.empty)
  private val bbsiService = mock[BBSIService]
  private val dataCacheConnector = mock[DataCacheConnector]
}
