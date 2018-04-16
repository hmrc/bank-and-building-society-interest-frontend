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

package service


import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.{BBSIConnector, HttpHandler}
import models.domain.{AmountRequest, BankAccount, CloseAccountRequest, UntaxedInterest}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BBSIServiceImpl @Inject()(val appConfig: FrontendAppConfig,
                                val httpHandler: HttpHandler,
                                val connector: BBSIConnector) extends BBSIService {


  def bankAccounts(nino:Nino)(implicit hc: HeaderCarrier): Future[Seq[BankAccount]] = {
    connector.bankAccounts(nino)
  }

  def bankAccount(nino:Nino, id: Int)(implicit hc: HeaderCarrier): Future[Option[BankAccount]] = {
    connector.bankAccount(nino, id)
  }

  def closeBankAccount(nino: Nino, id: Int, closeAccountRequest: CloseAccountRequest)(implicit hc: HeaderCarrier): Future[String] = {
    connector.closeBankAccount(nino, id, closeAccountRequest) map {
      case Some(envelopeId) => envelopeId
      case None => throw new RuntimeException("Failed while closing the bank account")
    }
  }

  def untaxedInterest(nino: Nino)(implicit hc: HeaderCarrier): Future[UntaxedInterest] = {
    connector.untaxedInterest(nino) map {
      case Some(untaxedInterest) => untaxedInterest
      case None => throw new RuntimeException("Failed while retrieving untaxed interest")
    }
  }

  def removeBankAccount(nino: Nino, id: Int)(implicit hc: HeaderCarrier): Future[String] = {
    connector.removeBankAccount(nino, id) map {
      case Some(envelopeId) => envelopeId
      case None => throw new RuntimeException("Failed while removing the bank account")
    }
  }

  def updateBankAccountInterest(nino: Nino, id: Int, amountRequest: AmountRequest)(implicit hc: HeaderCarrier): Future[String] = {
    connector.updateBankAccountInterest(nino, id, amountRequest) map {
      case Some(envelopeId) => envelopeId
      case None => throw new RuntimeException("Failed while updating the bank account")
    }
  }
}

trait BBSIService {
  def bankAccounts(nino:Nino)(implicit hc: HeaderCarrier): Future[Seq[BankAccount]]
  def bankAccount(nino:Nino, id: Int)(implicit hc: HeaderCarrier): Future[Option[BankAccount]]
  def updateBankAccountInterest(nino: Nino, id: Int, amountRequest: AmountRequest)(implicit hc: HeaderCarrier): Future[String]
  def removeBankAccount(nino: Nino, id: Int)(implicit hc: HeaderCarrier): Future[String]
  def untaxedInterest(nino: Nino)(implicit hc: HeaderCarrier): Future[UntaxedInterest]
  def closeBankAccount(nino: Nino, id: Int, closeAccountRequest: CloseAccountRequest)(implicit hc: HeaderCarrier): Future[String]
}