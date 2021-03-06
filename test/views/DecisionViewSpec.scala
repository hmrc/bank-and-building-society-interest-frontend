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

import forms.DecisionFormProvider
import models.{Decision, NormalMode}
import org.jsoup.nodes.Document
import play.api.data.Form
import viewmodels.BankAccountViewModel
import views.behaviours.ViewBehaviours
import views.html.decision

class DecisionViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "decision"

  val form = new DecisionFormProvider()()

  private val id = 1
  private val bankName = "TestName"
  val viewModel = BankAccountViewModel(id, bankName)
  val thisSectionIs = Some("This section is")

  def createView = () => decision(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  def createViewUsingForm = (form: Form[_]) => decision(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  "Decision view" must {
    behave like normalPage(createView, messageKeyPrefix,
      title = Some(messages("decision.title",bankName)),
      heading = Some(messages("decision.heading",bankName))
    )
    behave like pageWithBackLink(createView)
    behave like pageWithPreHeading(createView, messages("decision.preHeading"), thisSectionIs)
    behave like pageWithCancelLink(createView)
    behave like pageWithSubmitButton(createView,controllers.routes.DecisionController.onSubmit(NormalMode,1).url,messages("site.continue"))
  }

  "Decision view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- Decision.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- Decision.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- Decision.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
