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

import forms.behaviours.StringFieldBehaviours
import models.bbsi.TaxYear
import play.api.data.FormError
import play.api.libs.json.Json

class CloseAccountFormProviderSpec extends StringFieldBehaviours {

  val form = new CloseAccountFormProvider()()

  "accountClosedDay field" must {

    val fieldName = "accountClosedDay"
    val requiredKey = "error.date.dayBlank"
    val lengthKey = "closeAccount.error.field1.length"
    val maxLength = 2

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "accountClosedMonth field" must {

    val fieldName = "accountClosedMonth"
    val requiredKey = "error.date.monthBlank"
    val lengthKey = "closeAccount.error.field2.length"
    val maxLength = 2
    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "accountClosedYear field" must {

    val fieldName = "accountClosedYear"
    val requiredKey = "error.date.yearBlank"
    val lengthKey = "closeAccount.error.field3.length"
    val maxLength = 4

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "closed date" must {
    "not be in the future" in {
      val futureDate = Json.obj("accountClosedDay" -> "01", "accountClosedMonth" -> "01","accountClosedYear" -> TaxYear().next.toString)
      val validatedForm = form.bind(futureDate)
      validatedForm.hasErrors shouldBe(true)
    }
  }
}
