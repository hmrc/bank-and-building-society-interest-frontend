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

package views

import viewmodels.BankAccountViewModel
import views.behaviours.ViewBehaviours
import views.html.removeAccount

class RemoveAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "removeAccount"
  private val id = 1
  private val bankName = "testName"
  val viewModel = BankAccountViewModel(id, bankName)

  def createView = () => removeAccount(frontendAppConfig, viewModel)(fakeRequest, messages, templateRenderer)

  "RemoveAccount view" must {
    behave like normalPage(createView, messageKeyPrefix,
      title = Some(messages("removeAccount.title",bankName)),
      heading = Some(messages("removeAccount.heading",bankName))
    )
    behave like pageWithBackLink(createView)
    behave like pageWithPreHeading(createView, messages("removeAccount.preHeading"), Some(messages("This section is")))
    behave like pageWithCancelLink(createView)
    behave like pageWithText(createView, messages("removeAccount.description", viewModel.bankName))
    behave like pageWithSubmitButton(createView, controllers.routes.RemoveAccountController.onSubmit().url, messages("confirmAndSend"))
  }
}
