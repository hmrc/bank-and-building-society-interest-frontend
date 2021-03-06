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

import forms.BankAccountClosingInterestForm
import uk.gov.hmrc.http.cache.client.CacheMap
import identifiers._
import models._

class UserAnswers(val cacheMap: CacheMap) extends Enumerable.Implicits {
  def closingInterest: Option[BankAccountClosingInterestForm] = cacheMap.getEntry[BankAccountClosingInterestForm](ClosingInterestId.toString)
  def closeAccount: Option[CloseAccount] = cacheMap.getEntry[CloseAccount](CloseAccountId.toString)
  def updateInterest: Option[String] = cacheMap.getEntry[String](UpdateInterestId.toString)
  def decision: Option[Decision] = cacheMap.getEntry[Decision](DecisionId.toString)
}
