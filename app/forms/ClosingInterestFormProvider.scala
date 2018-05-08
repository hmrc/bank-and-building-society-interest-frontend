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

package forms

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.libs.json.Json
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import uk.gov.voa.play.form.ConditionalMappings._
import utils.FormHelpers.isValidCurrency
import utils.{BankAccountClosingInterestConstants, FormValuesConstants}

case class BankAccountClosingInterestForm(closingBankAccountInterestChoice: Option[String], closingInterestEntry: Option[String])

object BankAccountClosingInterestForm extends FormValuesConstants with BankAccountClosingInterestConstants{
  implicit val format = Json.format[BankAccountClosingInterestForm]
}

class ClosingInterestFormProvider @Inject() extends FormErrorHelper with FormValuesConstants
  with BankAccountClosingInterestConstants {

  def apply(): Form[BankAccountClosingInterestForm] = Form(
    mapping(
      ClosingInterestChoice -> optional(text).verifying(yesNoChoiceValidation),
      ClosingInterestEntry -> mandatoryIfEqual(ClosingInterestChoice,
        YesValue,
        text.verifying(StopOnFirstFail(
          nonEmptyText("closingInterest.error.blank"),
          isNumber("update.form.interest.isCurrency"),
          validateWholeNumber("update.form.interest.wholeNumber")
        ))
      )
    )(BankAccountClosingInterestForm.apply)(BankAccountClosingInterestForm.unapply)
    )

  private def yesNoChoiceValidation() = Constraint[Option[String]]("") {
    case Some(txt) if txt == YesValue || txt == NoValue => Valid
    case _ => Invalid("closingInterest.error.selectOption")
  }

  def nonEmptyText(requiredErrMsg : String): Constraint[String] = {
    Constraint[String]("required") {
      case textValue:String if notBlank(textValue) => Valid
      case _ => Invalid("closingInterest.error.blank")
    }
  }

  def isNumber(currencyErrorMsg : String): Constraint[String] = {
    Constraint[String]("invalidCurrency") {
      case textValue if isValidCurrency(Some(textValue)) => Valid
      case _ => Invalid(currencyErrorMsg)
    }
  }

  def validateWholeNumber(currencyErrorMsg : String): Constraint[String] = {
    Constraint[String]("invalidCurrency") {
      case textValue if isValidCurrency(Some(textValue), isWholeNumRequired = true) => Valid
      case _ => Invalid(currencyErrorMsg)
    }
  }

  def notBlank(value: String): Boolean = !value.trim.isEmpty
}
