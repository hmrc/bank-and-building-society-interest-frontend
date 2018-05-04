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

import forms.UpdateInterestFormProvider
import models.NormalMode
import models.bbsi.TaxYear
import play.api.data.Form
import viewmodels.UpdateInterestViewModel
import views.behaviours.StringViewBehaviours
import views.html.updateInterest

class UpdateInterestViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "updateInterest"

  val form = new UpdateInterestFormProvider()()

  private val id = 1
  private val amount: BigDecimal = 20.3
  private val bankName = "Test Name"
  val thisSectionIs = Some("This section is")

  private lazy val dateFormat = "d MMMM yyyy"

  val viewModel = UpdateInterestViewModel(id, amount, bankName)

  def createView = () => updateInterest(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  def createViewUsingForm = (form: Form[String]) => updateInterest(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  def createViewUsingErrorForm = () => updateInterest(frontendAppConfig, form.bind(Map("updateInterest" -> "")), NormalMode, viewModel)(fakeRequest, messages, templateRenderer)

  "UpdateInterest view" must {
    behave like normalPage(createView, messageKeyPrefix, title = Some(messages("updateInterest.title", bankName)), heading = Some(messages("updateInterest.title", viewModel.bankName)))
    behave like pageWithBackLink(createView)
    behave like pageWithPreHeading(createView, messages("updateInterest.preHeading"), thisSectionIs)
    behave like pageWithCancelLink(createView)

    "render parag 1 with text" when {
      behave like pageWithText(createView, messages("updateInterest.para1",
        TaxYear().start.toString(dateFormat),
        TaxYear().end.toString(dateFormat),
        "£20"))
    }

    "render textBox title with text" when {
      behave like pageWithText(createView, messages("updateInterest.textBox.title"))
    }

    "render para2 with text" when {
      behave like pageWithText(createView, messages("updateInterest.para2"))
    }

    "render para3 with text" when {
      behave like pageWithText(createView, messages("updateInterest.para3"))
    }

    "render accordion desc 1 with text" when {
      behave like pageWithText(createView, messages("updateInterest.accordion.desc1"))
    }

    "render accordion desc 2 with text" when {
      behave like pageWithText(createView, messages("updateInterest.accordion.desc2"))
    }

    "render para4 with text" when {
      behave like pageWithText(createView, messages("updateInterest.para4",
        TaxYear().year.toString,
        TaxYear().end.getYear.toString,
        TaxYear().end.toString(dateFormat)))
    }
      "display error link with text when untaxed interest is empty" when {
        behave like pageWithText(createViewUsingErrorForm, messages("updateInterest.blank"))
      }
  }
}
