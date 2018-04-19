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

package builders

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

import controllers.auth.BBSIUser
import model.domain.Person
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.play.frontend.auth.connectors.domain._
import uk.gov.hmrc.play.frontend.auth.{Attorney, AuthContext, Link}

object UserBuilder {

  val nino: Nino = new Generator().nextNino

  def apply(title: String = "Mr", firstName: String = "Jjj", secondName: Option[String] = None, lastName: String = "Bbb") = {

    val person = Person(Some(firstName), Some(lastName),nino.nino)

    def bbsiAuthority(id: String, nino: String): Authority =
      Authority(uri = s"/path/to/authority",
        accounts =  Accounts(tai = Some(TaxForIndividualsAccount(s"/tai/$nino", Nino(nino))),
          paye = Some(PayeAccount(s"/tai/$nino", Nino(nino)))),
        loggedInAt = None,
        previouslyLoggedInAt = None,
        credentialStrength = CredentialStrength.Strong,
        confidenceLevel = ConfidenceLevel.L200,
        userDetailsLink = Some(s"/user-details/$id"),
        enrolments = Some(s"/auth/oid/$id/enrolments"),
        ids = Some(s"/auth/oid/$id/ids"),
        legacyOid = s"$id")


    def payeAuthority(id: String, nino: String): Authority =
      Authority(uri = s"/path/to/authority",
        accounts =  Accounts(paye = Some(PayeAccount(s"/tai/$nino", Nino(nino)))),
        loggedInAt = None,
        previouslyLoggedInAt = None,
        credentialStrength = CredentialStrength.Strong,
        confidenceLevel = ConfidenceLevel.L200,
        userDetailsLink = Some(s"/user-details/$id"),
        enrolments = Some(s"/auth/oid/$id/enrolments"),
        ids = Some(s"/auth/oid/$id/ids"),
        legacyOid = s"$id")


    def bbsiAuthContext(id: String, nino: String) = {
      AuthContext(bbsiAuthority(id, nino),governmentGatewayToken = Some("a token"))
    }

    BBSIUser(
      authContext = bbsiAuthContext(s"${firstName.head.toLower}${lastName.toLowerCase}", person.nino),
      person = person
    )

  }

  def createUserWithAttorney(attorneyName: String, attorneyLink: Link): BBSIUser = {
    val user = apply()
    BBSIUser(user.authContext.copy(attorney = Some(Attorney(attorneyName,attorneyLink))), user.person)
  }

}
