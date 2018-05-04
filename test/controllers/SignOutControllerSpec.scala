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

import play.api.test.Helpers._

class SignOutControllerSpec extends ControllerSpecBase {

  "signOut" should {
    "redirects to the signout URL" in {
      val controller = new SignOutController(frontendAppConfig, messagesApi)

      val result = controller.signOut()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(frontendAppConfig.companyAuthFrontendSignOutUrl)
    }

    "redirect to citizen auth frontend if it is a Verify user" in {
      val controller = new SignOutController(frontendAppConfig, messagesApi)

      val result = controller.signOut()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(frontendAppConfig.citizenAuthFrontendSignOutUrl)
    }
  }
}
