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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import forms.BankAccountClosingInterestForm
import identifiers.{CloseAccountId, ClosingInterestId}
import models.CloseAccount
import models.domain.{AmountRequest, CloseAccountRequest}
import org.joda.time.LocalDate
import play.api.i18n.{I18nSupport, MessagesApi}
import service.BBSIService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.renderer.TemplateRenderer
import uk.gov.hmrc.time.TaxYearResolver
import utils._
import viewmodels.{BankAccountViewModel, CloseBankAccountCheckAnswersViewModel, UpdateInterestViewModelCheckAnswers}
import views.html.{check_your_answers, close_account_check_your_answers}

import scala.concurrent.Future

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           navigator: Navigator,
                                           authenticate: AuthAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           bbsiService: BBSIService)
                                          (implicit templateRenderer: TemplateRenderer) extends FrontendController with I18nSupport with JourneyConstants with Enumerable.Implicits{

  def onPageLoad() = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId
      val updateInterest = request.userAnswers.updateInterest
      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(BankAccountViewModel(id, bankName)) => {
          updateInterest match {
            case Some(value) =>
              val viewModel = UpdateInterestViewModelCheckAnswers(id, value, bankName)
              Future.successful(Ok(check_your_answers(appConfig, viewModel)))
            case _ => Future.successful(NotFound)
          }
        }
        case _ => Future.successful(NotFound)
      }
  }

  def onSubmit() = (authenticate andThen getData andThen requireData).async {
    implicit request =>

      val nino = request.externalId

      val updateInterest = request.userAnswers.updateInterest
      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(BankAccountViewModel(id, _)) => {
          updateInterest match {
            case Some(value) =>
              bbsiService.updateBankAccountInterest(Nino(nino), id, AmountRequest(BigDecimal(FormHelpers.stripNumber(value)))) flatMap { _ =>
                dataCacheConnector.flush(nino) map { _ =>
                  Redirect(controllers.routes.ConfirmationController.onPageLoad)
                }
              }
            case _ => Future.successful(NotFound)
          }
        }
        case _ => Future.successful(NotFound)
      }
  }

  def onPageLoadClose() = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId
      request.userAnswers.cacheMap.getEntry[BankAccountViewModel](BankAccountDetailsKey) match {
        case Some(BankAccountViewModel(id, bankName)) =>
          request.userAnswers.cacheMap.getEntry[CloseAccount](CloseAccountId.toString) match {
            case Some(CloseAccount(day, month, year)) => {
              val localDate = new LocalDate(year.toInt, month.toInt, day.toInt)
              if(TaxYearResolver.fallsInThisTaxYear(localDate)) {
                request.userAnswers.cacheMap.getEntry[BankAccountClosingInterestForm](ClosingInterestId.toString) match {
                  case Some(BankAccountClosingInterestForm(_, closingInterestEntry)) => {
                    val viewModel = CloseBankAccountCheckAnswersViewModel(id, localDate.toString(), Some(bankName), closingInterestEntry)
                    dataCacheConnector.save[CloseBankAccountCheckAnswersViewModel](nino, CloseAccountAnswersKey, viewModel)
                    Future.successful(Ok(close_account_check_your_answers(appConfig, viewModel)))
                  }
                  case _ => Future.successful(NotFound)
                }
              }
              else {
                val viewModel = CloseBankAccountCheckAnswersViewModel(id, localDate.toString(), Some(bankName), None)
                dataCacheConnector.save[CloseBankAccountCheckAnswersViewModel](nino, CloseAccountAnswersKey, viewModel)
                Future.successful(Ok(close_account_check_your_answers(appConfig, viewModel)))
              }
            }
            case _ => Future.successful(NotFound)
          }
        case _ => Future.successful(NotFound)
      }
  }

  def onSubmitClose() = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val nino = request.externalId
      request.userAnswers.cacheMap.getEntry[CloseBankAccountCheckAnswersViewModel](CloseAccountAnswersKey) match {
        case Some(CloseBankAccountCheckAnswersViewModel(id, closeBankAccountDate, _, interestAmount)) => {
          val amount = interestAmount match {
            case Some(value) => Some(BigDecimal(value))
            case _ => None
          }
          val viewModel = CloseAccountRequest(LocalDate.parse(closeBankAccountDate), amount)
          bbsiService.closeBankAccount(Nino(nino), id, viewModel) flatMap { _ =>
            dataCacheConnector.flush(nino) map { _ =>
              Redirect(controllers.routes.ConfirmationController.onPageLoad)
            }
          }
        }
        case _ => Future.successful(NotFound)
      }
  }
}
