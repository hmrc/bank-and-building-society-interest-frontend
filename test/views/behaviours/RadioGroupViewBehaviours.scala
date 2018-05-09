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

package views.behaviours

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat

trait RadioGroupViewBehaviours[A] extends ViewBehaviours {

  val form: Form[A]

  def pageWithYesNoRadioButton( createView: (Form[A]) => HtmlFormat.Appendable,
                                field:String,
                              idYes:String,
                              idNo:String,
                                errorMessage:String) : Unit = {
    "have a yes/no radio button" in {

      val doc = asDocument(createView(form))
      assertRenderedById(doc, idYes)
      assertRenderedById(doc, idNo)
    }

    "have an error message when no radio selection input is provided" in{
      val doc = asDocument(createView(form.withError(FormError(field, errorMessage))))
      doc.getElementsByClass("error-message").text mustBe errorMessage
    }

    "show an error summary when no radio selection input is provided" in {
      val doc = asDocument(createView(form.withError(FormError(field, "error"))))
      assertRenderedById(doc, "error-summary-heading")
    }
  }

  def pageWithInputField(createView: (Form[A]) => HtmlFormat.Appendable,
                         field:String,
                         errorMessage:String): Unit ={

    "have input field in" in {
      val doc = asDocument(createView(form))
      assertRenderedById(doc, field)
    }

    "have an error message when no input is provided" in {
      val doc = asDocument(createView(form.withError(FormError(field, errorMessage))))
      doc.getElementsByClass("error-message").text mustBe errorMessage
    }

    "show an error summary when no input is provided" in {
      val doc = asDocument(createView(form.withError(FormError(field, "error"))))
      assertRenderedById(doc, "error-summary-heading")
    }

  }

}
