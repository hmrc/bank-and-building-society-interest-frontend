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

package controllers.auth

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


import model.domain.Person
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.frontend.auth.{Attorney, AuthContext, Principal}
import uk.gov.hmrc.play.test.UnitSpec

class BBSIUserSpec extends UnitSpec with MockitoSugar {

  "BBSIUser" should {

    "isDisplayName" should {
      val authContext = mock[AuthContext]
      val principal = mock[Principal]
      val bBSIUser = BBSIUser(authContext, Person())
      val attorney = mock[Attorney]

      "return name " in {
        when(authContext.isDelegating).thenReturn(false)
        when(authContext.principal).thenReturn(principal)
        when(principal.name).thenReturn(Option("Test"))
        bBSIUser.getDisplayName shouldBe "Test"

      }

      "return attorney name" in {
        when(authContext.isDelegating).thenReturn(true)
        when(authContext.attorney).thenReturn(Option(attorney))
        when(attorney.name).thenReturn("Attorney")

        bBSIUser.getDisplayName shouldBe "Attorney"

      }

    }

    "getAuthContext" should {
      val authContext = mock[AuthContext]
      val principal = mock[Principal]
      val bBSIUser = BBSIUser(authContext, Person(Some("FIRST"), Some("SECOND")))

      "return same auth " in {
        when(authContext.isDelegating).thenReturn(true)
        when(authContext.principal).thenReturn(principal)
        when(principal.name).thenReturn(Option("Test"))

        bBSIUser.authContext.principal.name.get shouldBe "Test"

        bBSIUser.getAuthContext.authContext.principal.name.get shouldBe "Test"

      }

      "return new auth " in {
        when(authContext.isDelegating).thenReturn(false)
        when(authContext.principal).thenReturn(principal)
        when(authContext.copy(any(), any(), any(), any(), any(), any())).thenCallRealMethod()
        when(authContext.principal.copy(any(), any())).thenCallRealMethod()
        when(principal.name).thenReturn(Option("Test"))

        bBSIUser.authContext.principal.name.get shouldBe "Test"

        bBSIUser.getAuthContext.authContext.principal.name.get shouldBe "FIRST SECOND"

      }


    }


  }

}

