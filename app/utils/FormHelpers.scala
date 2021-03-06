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

package utils

import play.api.data.Form

object FormHelpers {

  def getErrorByKey[A](form: Form[_], errorKey: String) = {
    form.error(errorKey) match {
      case None => ""
      case Some(error) => error.message
    }
  }

  def stripNumber(stringValue: String): String = {
    val toRemove = Set[Char]('£', ',', ' ')
    val newValue = stringValue.filterNot(toRemove)
    newValue.takeWhile(_ != '.')
  }

  def isValidCurrency(stringValue: Option[String], isWholeNumRequired: Boolean = false): Boolean = {
    stringValue match {
      case (Some(value)) =>
        FormHelpers.isCurrency(value, isWholeNumRequired)
      case _ => true
    }
  }

  def isCurrency(stringValue: String, isWholeNumRequired: Boolean): Boolean = {
    val currencyRegex: String = "^\\£?(([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))?"
    val regex: String = if (isWholeNumRequired) currencyRegex else currencyRegex + "(\\.\\d\\d)?"

    stringValue matches regex.r.toString()
  }
}
