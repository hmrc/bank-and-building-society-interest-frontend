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

import config.FrontendAppConfig
import mocks.MockTemplateRenderer
import models.bbsi.TaxYear
import models.domain.UntaxedInterest
import play.api.inject.Injector
import play.twirl.api.Html
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.{BBSIApp, BBSIViewSpec}
import views.html.overview



class OverviewViewSpec extends BBSIViewSpec with BBSIApp {

  "BankBuildingSociety Overview page" should {
    behave like pageWithTitle(messages("bbsi.overview.heading"))
    // TODO TAAS behave like pageWithBackLink
    //TODO TAAS behave like pageWithHeader(messages("bbsi.overview.heading"))

    "display first section" in {
      page must haveParagraphWithText(messages("bbsi.overview.para1"))
      page must haveParagraphWithText(messages("bbsi.overview.para2"))
      page must haveParagraphWithText("£2,000 " + messages("bbsi.overview.interest.year.desc",
        TaxYear().start.toString(dateFormatPattern), TaxYear().end.toString(dateFormatPattern)))
      page must haveParagraphWithText(messages("bbsi.overview.interest.estimate.desc",
        TaxYear().prev.start.toString(dateFormatPattern), TaxYear().prev.end.toString(dateFormatPattern)))
    }

    "display second section" in {
      page must haveHeadingH2WithText(messages("bbsi.overview.whatYouMustDo.title"))
      page must haveParagraphWithText(messages("bbsi.overview.whatYouMustDo.desc"))
      page must haveBulletPointWithText(messages("bbsi.overview.whatYouMustDo.point1",
        TaxYear().start.toString(dateFormatPattern), TaxYear().end.toString(dateFormatPattern)))
      page must haveBulletPointWithText(messages("bbsi.overview.whatYouMustDo.point2"))
    }

    "display third section" in {
      page must haveHeadingH2WithText(messages("bbsi.overview.whyThisIsImp.title"))
      page must haveParagraphWithText(messages("bbsi.overview.whyThisIsImp.desc"))
      page must haveBulletPointWithText(messages("bbsi.overview.whyThisIsImp.point1"))
      page must haveBulletPointWithText(messages("bbsi.overview.whyThisIsImp.point2"))
      page must haveBulletPointWithText(messages("bbsi.overview.whyThisIsImp.point3"))
    }

    "display details link" in {
      page must haveLinkWithUrlWithID("checkYourAccounts", controllers.routes.AccountDetailsController.onPageLoad().url)
    }

  }

  private val dateFormatPattern = "d MMMM yyy"
  private lazy val page = doc(view)
  def injector: Injector = app.injector

  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val templateRenderer: TemplateRenderer = MockTemplateRenderer

  val untaxedInterest = UntaxedInterest(2000,Seq.empty)
  override def view: Html = overview(untaxedInterest, appConfig)(request ,messages, templateRenderer)
}
