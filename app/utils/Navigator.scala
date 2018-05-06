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

import javax.inject.{Inject, Singleton}

import controllers.routes
import forms.BankAccountClosingInterestForm
import identifiers._
import models.Decision.{Close, Remove, Update}
import models.{CheckMode, CloseAccount, Mode, NormalMode}
import org.joda.time.LocalDate
import play.api.mvc.Call
import uk.gov.hmrc.time.TaxYearResolver

@Singleton
class Navigator @Inject()() extends JourneyConstants with FormValuesConstants {

  private def routeMap(mode: Mode): Map[Identifier, UserAnswers => Call] = Map(
    DecisionId -> ( _.decision match {
      case Some(Remove) =>
        routes.RemoveAccountController.onPageLoad()
      case Some(Update) =>
        routes.UpdateInterestController.onPageLoad(mode)
      case Some(Close) =>
        routes.CloseAccountController.onPageLoad(mode)
      case _ => routes.OverviewController.onPageLoad()
    }),
    UpdateInterestId -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    CloseAccountId -> (_.closeAccount match {
        case Some(CloseAccount(day,month,year)) => {
          if(TaxYearResolver.fallsInThisTaxYear(new LocalDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)))){
            routes.ClosingInterestController.onPageLoad(mode)
          }else{
            routes.CheckYourAnswersController.onPageLoadClose()
          }
        }
        case None => routes.OverviewController.onPageLoad()
    }),
    ClosingInterestId -> (_.closingInterest match {
      case Some(BankAccountClosingInterestForm(_,_)) => routes.CheckYourAnswersController.onPageLoadClose()
      case _ => routes.OverviewController.onPageLoad()
    })
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map()

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = (id, mode) match {
    case (_, NormalMode) =>
      routeMap(mode).getOrElse(id , _ => routes.OverviewController.onPageLoad())
    case (ClosingInterestId, CheckMode) =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoadClose())
    case (CloseAccountId, CheckMode) =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoadClose())
    case (_, CheckMode) =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
