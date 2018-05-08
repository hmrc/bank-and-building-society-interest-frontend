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

import forms.{BankAccountClosingInterestForm, ClosingInterestFormProvider}
import models.NormalMode
import play.api.data.Form
import uk.gov.hmrc.time.TaxYearResolver
import utils.BankAccountClosingInterestConstants
import views.behaviours.RadioGroupViewBehaviours
import views.html.closingInterest

class ClosingInterestViewSpec extends RadioGroupViewBehaviours[BankAccountClosingInterestForm] with BankAccountClosingInterestConstants {
  val messageKeyPrefix = "closingInterest"

  val form = new ClosingInterestFormProvider()()

  def createView = () => closingInterest(frontendAppConfig, form, NormalMode)(fakeRequest, messages, templateRenderer)

  def createViewUsingForm = (form: Form[BankAccountClosingInterestForm]) => closingInterest(frontendAppConfig, form, NormalMode)(fakeRequest, messages, templateRenderer)

  "ClosingInterest view" must {

    behave like normalPage(createView,messageKeyPrefix,
      Some(messages("closingInterest.title",TaxYearResolver.currentTaxYear.toString)),
      Some(messages("closingInterest.title",TaxYearResolver.currentTaxYear.toString)))
    behave like pageWithPreHeading(createView, messages("closeAccount.preHeading"), Some(messages("This section is")))
    behave like pageWithBackLink(createView)
    behave like pageWithCancelLink(createView)
    behave like pageWithYesNoRadioButton(createViewUsingForm,ClosingInterestChoice, s"$ClosingInterestChoice-yes", s"$ClosingInterestChoice-no",
      messages("closingInterest.error.selectOption"))
    behave like pageWithInputField(createViewUsingForm, ClosingInterestEntry,messages("closingInterest.error.blank"))
    behave like pageWithSubmitButton(createView, controllers.routes.ClosingInterestController.onSubmit(NormalMode).url, messages("site.continue"))
  }

}
