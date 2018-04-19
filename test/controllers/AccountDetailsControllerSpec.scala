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

import controllers.actions.FakeAuthAction
import models.domain.UntaxedInterest
import org.jsoup.Jsoup
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.test.Helpers.{contentAsString, status, _}
import service.BBSIService
import views.html.account_details

import scala.concurrent.Future

class AccountDetailsControllerSpec extends ControllerSpecBase {

  "Account Details Controller" must {
    "return 200 for a GET" in {

      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))

      val result = new AccountDetailsController(frontendAppConfig, messagesApi, FakeAuthAction, bbsiService).onPageLoad()(fakeRequest)
      status(result) mustBe OK


    }

    "return the correct view for a GET" in {
      when(bbsiService.untaxedInterest(any())(any())).thenReturn(Future.successful(untaxedInterest))
      val result = new AccountDetailsController(frontendAppConfig, messagesApi, FakeAuthAction, bbsiService).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe account_details(untaxedInterest,frontendAppConfig)(fakeRequest, messages, templateRenderer).toString

      val doc = Jsoup.parse(contentAsString(result))
      doc.title() must include(messages("accountDetails.heading"))
    }
  }

  private val untaxedInterest = UntaxedInterest(0, Seq.empty)
  private val bbsiService = mock[BBSIService]
}
