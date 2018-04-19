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
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.AuthContext

case class BBSIUser(authContext: AuthContext, person : Person) {
  def getAuthContext = if (!authContext.isDelegating) this.copy(authContext = authContext.copy(
    principal = authContext.principal.copy(Some(person.firstName.getOrElse("") + " "+ person.lastName.getOrElse(""))))) else this
  def getNino = authContext.principal.accounts.paye.fold(Nino(""))(t => t.nino)
  def getDisplayName = if (!authContext.isDelegating) authContext.principal.name.fold("NameNotDefined"){name => name} else authContext.attorney.fold("NameNotDefined"){name => name.name}
  def getPreviouslyLoggedInAt = authContext.user.previouslyLoggedInAt
}

