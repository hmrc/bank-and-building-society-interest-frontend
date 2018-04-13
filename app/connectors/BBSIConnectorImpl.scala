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

package connectors

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


import com.google.inject.{ImplementedBy, Inject, Singleton}
import config.FrontendAppConfig
import models.domain.{AmountRequest, BankAccount, CloseAccountRequest, UntaxedInterest}
import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

@Singleton
class BBSIConnectorImpl @Inject()(val appConfig: FrontendAppConfig,
                                  val httpHandler: HttpHandlerImpl) extends BBSIConnector {

  def serviceUrl: String = appConfig.baseUrl("tai")


  def bankAccounts(nino: Nino)(implicit hc: HeaderCarrier): Future[Seq[BankAccount]] = {
    httpHandler.getFromApi(bbsiAccountsUrl(nino)) map ( json => (json \ "data").as[Seq[BankAccount]]) recover {
      case _: Exception =>
        Logger.warn(s"Exception generated while reading bank-accounts for nino $nino")
        Seq.empty[BankAccount]
    }
  }

  def bankAccount(nino:Nino, id:Int)(implicit hc: HeaderCarrier) : Future[Option[BankAccount]] = {
    httpHandler.getFromApi(bbsiAccountUrl(nino, id)) map {
      json: JsValue => (json \ "data").asOpt[BankAccount]

    } recover {
      case NonFatal(_) =>
        Logger.warn(s"Could not find bank account for nino: $nino and id: $id")
        None
    }
  }

  def closeBankAccount(nino: Nino, id: Int, closeAccountRequest: CloseAccountRequest)(implicit hc: HeaderCarrier): Future[Option[String]] = {
    httpHandler.putToApi[CloseAccountRequest](bbsiEndAccountUrl(nino, id), closeAccountRequest).map(response => (response.json \ "data").asOpt[String])
  }


  def untaxedInterest(nino: Nino)(implicit hc: HeaderCarrier): Future[Option[UntaxedInterest]] = {
    httpHandler.getFromApi(bbsiSavingsInvestmentsUrl(nino)) map {
      json: JsValue => (json \ "data").asOpt[UntaxedInterest]
    }
  }


  def removeBankAccount(nino: Nino, id: Int)(implicit hc: HeaderCarrier): Future[Option[String]] ={
    httpHandler.deleteFromApi(bbsiAccountUrl(nino, id)).map(response => (response.json \ "data").asOpt[String])
  }

  def updateBankAccountInterest(nino: Nino, id: Int, amountRequest: AmountRequest)(implicit hc: HeaderCarrier): Future[Option[String]] = {
    httpHandler.putToApi[AmountRequest](bbsiUpdateAccountUrl(nino, id), amountRequest).map(response => (response.json \ "data").asOpt[String])
  }

  private def bbsiAccountsUrl(nino: Nino): String = s"$serviceUrl/tai/$nino/tax-account/income/savings-investments/untaxed-interest/bank-accounts"
  private def bbsiAccountUrl(nino: Nino, id: Int): String = s"$serviceUrl/tai/$nino/tax-account/income/savings-investments/untaxed-interest/bank-accounts/$id"
  private def bbsiEndAccountUrl(nino: Nino, id: Int): String = s"$serviceUrl/tai/$nino/tax-account/income/savings-investments/untaxed-interest/bank-accounts/$id/closedAccount"
  private def bbsiUpdateAccountUrl(nino: Nino, id: Int): String = s"$serviceUrl/tai/$nino/tax-account/income/savings-investments/untaxed-interest/bank-accounts/$id/interest-amount"
  private def bbsiSavingsInvestmentsUrl(nino: Nino): String = s"$serviceUrl/tai/$nino/tax-account/income/savings-investments/untaxed-interest"

}

@ImplementedBy(classOf[BBSIConnectorImpl])
trait BBSIConnector {
  def serviceUrl: String
  def bankAccounts(nino: Nino)(implicit hc: HeaderCarrier): Future[Seq[BankAccount]]
  def bankAccount(nino:Nino, id:Int)(implicit hc: HeaderCarrier) : Future[Option[BankAccount]]
  def closeBankAccount(nino: Nino, id: Int, closeAccountRequest: CloseAccountRequest)(implicit hc: HeaderCarrier): Future[Option[String]]
  def untaxedInterest(nino: Nino)(implicit hc: HeaderCarrier): Future[Option[UntaxedInterest]]
  def removeBankAccount(nino: Nino, id: Int)(implicit hc: HeaderCarrier): Future[Option[String]]
  def updateBankAccountInterest(nino: Nino, id: Int, amountRequest: AmountRequest)(implicit hc: HeaderCarrier): Future[Option[String]]
}