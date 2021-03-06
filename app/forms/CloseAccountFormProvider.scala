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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.CloseAccount
import org.joda.time.LocalDate

class CloseAccountFormProvider @Inject() extends Mappings {

   def apply(): Form[CloseAccount] = Form(
     mapping(
      "accountClosedDay" -> texts("error.date.dayBlank")
        .verifying(maxLength(2, "closeAccount.error.field1.length")),
      "accountClosedMonth" -> texts("error.date.monthBlank")
        .verifying(maxLength(2, "closeAccount.error.field2.length")),
      "accountClosedYear" -> texts("error.date.yearBlank")
        .verifying(maxLength(4, "closeAccount.error.field3.length"))
    )(CloseAccount.apply)(CloseAccount.unapply) verifying("date.error.future", fields => fields match {
       case closeAccount => {
         val closedDate = new LocalDate(closeAccount.accountClosedYear.toInt,
                                        closeAccount.accountClosedMonth.toInt,
                                        closeAccount.accountClosedDay.toInt)

         !closedDate.isAfter(LocalDate.now()) match {
           case(true) => true
           case(false) => false
         }
       }
     })

   )


 }
