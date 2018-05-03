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

import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  def normalPage(view: () => HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 title: Option[String] = None,
                 heading: Option[String] =  None,
                 preHeading: Option[String] = None,
                 expectedGuidanceKeys: Seq[String] = Seq.empty) = {

    "behave like a normal page" when {

      "rendered" must {

        "display the correct browser title" in {
          val doc = asDocument(view())
          val fallbackTitle = messages(s"$messageKeyPrefix.title")
          assertEqualsValue(doc, messages("site.title"), title.getOrElse(fallbackTitle) + messages("site.gov.uk"))
        }

        "display the correct page heading" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, heading.getOrElse(s"$messageKeyPrefix.heading"))
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }
      }
    }
  }

  def pageWithBackLink(view: () => HtmlFormat.Appendable) = {

    "behave like a page with a back link" must {
      "have a back link" in {
        val doc = asDocument(view())
        assertRenderedById(doc, "back-link")
      }
    }
  }

  def pageWithPreHeading(view: () => HtmlFormat.Appendable,
                         preHeadingText: String,
                         preHeadingAnnouncementText: Option[String] = None): Unit = {
    "have an accessible pre heading" in {
      val doc = asDocument(view())
      if(preHeadingAnnouncementText.isDefined){
        assertEqualsValueText(doc, "header>p", s"${preHeadingAnnouncementText.get} ${preHeadingText}")
      } else {
        assertEqualsValueText(doc, "header>p", preHeadingText)
      }
    }
  }

  def pageWithCancelLink(view: () => HtmlFormat.Appendable) = {
    "render a cancel link with the correct url" in {
      val cancelId = "cancelLink"
      val doc = asDocument(view())
      assertRenderedById(doc, cancelId)
      assert(doc.getElementById(cancelId).attr("href") == controllers.routes.AccountDetailsController.cancelJourney().url)
    }
  }

  def pageWithText(view: () => HtmlFormat.Appendable, messageKey: String) = {
    "render a paragraph with the correct content" in {
      val doc = asDocument(view())
      assertContainsText(doc, messages(messageKey))
    }
  }

  def pageWithSubmitButton(view: () => HtmlFormat.Appendable, submitUrl: String, buttonText: String): Unit = {
    val doc = asDocument(view())
    val submit = "submit"

    "have a form with a submit button" in {
      assertRenderedById(doc, submit)
    }

    "have a form with a submit button of input labelled as buttonText" in {
      assert(doc.getElementById(submit).text() == buttonText)
    }

    "have a form with the correct submit url" in {
      assert(doc.getElementsByTag("form").attr("action") == submitUrl)
    }
  }

  def pageWithChangeLink(view: () => HtmlFormat.Appendable, id: Int, submitUrl: String) = {
    val doc = asDocument(view())
    s"have a page with change link on line $id" in {
      assert(doc.getElementById(s"confirmation-line-$id-change-link").attr("href") == submitUrl)
    }
  }

  def pageWithQuestionLine(view: () => HtmlFormat.Appendable, id: Int, text: String) = {
    val doc = asDocument(view())
    s"have a page with question on line $id" in {
      assert(doc.getElementById(s"confirmation-line-$id-question").text() == text)
    }
  }

  def pageWithAnswerLine(view: () => HtmlFormat.Appendable, id: Int, text: String) = {
    val doc = asDocument(view())
    s"have a page with answer on line $id" in {
      assert(doc.getElementById(s"confirmation-line-$id-answer").text() == text)
    }
  }

  def pageWithHeadingH2(view: () => HtmlFormat.Appendable,
                        h2Text: String) = {
    "render a header two" in {
      val doc = asDocument(view())
      assertPageHeaderTwo(doc, h2Text)
    }
  }
}