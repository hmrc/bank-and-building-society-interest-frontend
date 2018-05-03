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
import play.api.data.FormError
import play.api.libs.json.Json

class UpdateInterestFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "updateInterest.error.required"
  val lengthKey = "updateInterest.error.length"
  val maxLength = 90

  val form = new UpdateInterestFormProvider()()


  "Update Income Form" must {
    "return no error with valid data" in {
      val untaxedInterest = form.bind(Json.obj("updateInterest" -> "1,000"))

      untaxedInterest.errors shouldBe empty
    }

    "return error" when {
      "passed empty value" in {
        val untaxedInterest = form.bind(Json.obj("updateInterest" -> ""))

        untaxedInterest.errors should contain(FormError("updateInterest", List("updateInterest.blank")))
      }

      "passed characters" in {
        val untaxedInterest = form.bind(Json.obj("updateInterest" -> "dasdas"))

        untaxedInterest.errors should contain(FormError("updateInterest", "updateInterest.isCurrency"))
      }

      "entered (,) at wrong place" in {
        val untaxedInterest = form.bind(Json.obj("updateInterest" -> "1,00"))

        untaxedInterest.errors should contain(FormError("updateInterest", "updateInterest.isCurrency"))
      }

      "passed decimal value" in {
        val untaxedInterest = form.bind(Json.obj("updateInterest" -> "1.00"))

        untaxedInterest.errors should contain(FormError("updateInterest", "updateInterest.wholeNumber"))
      }

    }

  }
}
