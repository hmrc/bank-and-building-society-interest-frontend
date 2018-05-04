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

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.UserDetailsConnector
import controllers.actions.AuthAction
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.play.frontend.auth.DelegationAwareActions
import uk.gov.hmrc.renderer.TemplateRenderer

class SignOutController @Inject()(val appConfig: FrontendAppConfig,
                                  val messagesApi: MessagesApi,
                                  authenticate: AuthAction,) extends FrontendController with I18nSupport {

  def userDetailsConnector: UserDetailsConnector

  def signOut = authenticate.async {
    implicit user =>

      val thing = user

      val test = userDetailsConnector.userDetails(user.authContext)

        test.map { x =>
        if (x.hasVerifyAuthProvider) {
          Redirect(appConfig.citizenAuthFrontendSignOutUrl)
        } else {
          Redirect(appConfig.companyAuthFrontendSignOutUrl)
        }
      }

//    Redirect(appConfig.companyAuthFrontendSignOutUrl)
  }


}

trait WithAuthorisedForTaiLite extends DelegationAwareActions {

  def authorisedTest = {
    AuthorisedFor(TaiRegime, TaiConfidenceLevelPredicate)
  }
}
