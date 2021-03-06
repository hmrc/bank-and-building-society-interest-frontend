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

import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmation"

  def createView = () => confirmation(frontendAppConfig)(fakeRequest, messages, templateRenderer)

  "Confirmation view" must {

    behave like normalPage(createView, "confirmation", Some(messages("confirmation.heading")))

    "renders heading" when {
      behave like pageWithText(createView, messages("confirmation.sectionOne.heading"))
    }

    "render first part of description" when {
      behave like pageWithText(createView, messages("confirmation.paraOne"))
    }

    "render second part of description" when {
      behave like pageWithText(createView, messages("confirmation.paraTwo"))
    }

    "render third part of description" when {
      behave like pageWithText(createView, messages("confirmation.paraThree"))
    }
    "display return button" in {
      val doc = asDocument(createView())
      assert(doc.getElementById("returnToAccounts").text() == messages("confirmation.back"))
      assert(doc.getElementById("returnToAccounts").attr("href") == controllers.routes.AccountDetailsController.onPageLoad().url)
    }
  }


}
