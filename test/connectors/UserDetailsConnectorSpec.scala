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

import builders.UserBuilder
import models.UserDetails
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.http.{CoreGet, HeaderCarrier}
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps


class UserDetailsConnectorSpec extends PlaySpec with MockitoSugar {

  "userDetails with userDetailsUri as parameter" should {
    "return UserDetails from Http get" in {
      val sut = new UserDetailsConnector {
        override def http = mockHttp
      }
      implicit val hc = HeaderCarrier()
      val userDetails = UserDetails("")
      val userDetailsUri = "user-details/user-details"
      when(mockHttp.GET[UserDetails](any())(any(), any(), any())).thenReturn(Future.successful(userDetails))

      val result = Await.result(sut.userDetails(userDetailsUri), 5 seconds)

      result mustBe userDetails
    }
  }

  "userDetails with authContext as parameter" should {
    "return UserDetails from Http get" when {
      "UserDetailsUri exists" in {
        val sut = new UserDetailsConnector {
          override def http = mockHttp
        }
        implicit val hc = HeaderCarrier()
        val userDetails = UserDetails("")
        val authContext: AuthContext = UserBuilder().authContext
        when(mockHttp.GET[UserDetails](any())(any(), any(), any())).thenReturn(Future.successful(userDetails))

        val result = Await.result(sut.userDetails(authContext), 5 seconds)

        result mustBe userDetails
      }
    }

    "return failure" when {
      "UserDetailsUri does not exist" in {
        val sut = new UserDetailsConnector {
          override def http = mockHttp
        }
        implicit val hc = HeaderCarrier()
        val userDetails = UserDetails("")
        val authContext: AuthContext = UserBuilder().authContext.copy(userDetailsUri = None)

        val result = sut.userDetails(authContext)
        ScalaFutures.whenReady(result.failed) { e =>
          e mustBe a[RuntimeException]
        }
      }
    }
  }

  private lazy val mockHttp = mock[CoreGet]

}
