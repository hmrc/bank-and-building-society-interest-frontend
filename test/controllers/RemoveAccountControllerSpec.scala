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

import controllers.actions._
import models.NormalMode
import models.domain.BankAccount
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.test.Helpers._
import service.BBSIService
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

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new RemoveAccountController(frontendAppConfig, messagesApi, FakeAuthAction,
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString() = removeAccount(frontendAppConfig)(fakeRequest, messages, templateRenderer).toString

  "RemoveAccount Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "retrieve the bank name correctly" in {
      val bbsiService = mock[BBSIService]
      when(bbsiService.bankAccount(any(), any())(any())).thenReturn(Future.successful(Some(bankAccount)))
      val result = controller(bbsiService).onSubmit(NormalMode, id)
    }
  }
}




