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

import forms.CloseAccountFormProvider
import models.{CloseAccount, NormalMode}
import play.api.data.Form
import viewmodels.BankAccountViewModel
import views.behaviours.QuestionViewBehaviours
import views.html.closeAccount

class CloseAccountViewSpec extends QuestionViewBehaviours[CloseAccount] {

  val bankName = "testName"
  val messageKeyPrefix = "closeAccount"
  val viewModel = BankAccountViewModel(1, bankName)

  override val form = new CloseAccountFormProvider()()

  def createView = () => closeAccount(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  def createViewUsingForm = (form: Form[_]) => closeAccount(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)


  "CloseAccount view" must {
    behave like normalPage(createView, messageKeyPrefix, Some(messages("closeAccount.title",bankName)), Some(messages("closeAccount.title",bankName)))
    behave like pageWithPreHeading(createView, messages("closeAccount.preHeading"), Some(messages("This section is")))
    behave like pageWithBackLink(createView)
    behave like pageWithCancelLink(createView)

  }
}
