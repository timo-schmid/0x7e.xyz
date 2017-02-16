/*
 * Copyright 2016 Timo Schmid
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

package xyz._0x7e.dsl

import org.http4s.Request
import org.http4s.dsl._

class RequestOps(request: Request) {

  // returns a header of a request as an Option[String]
  def header(name: String): Option[String] =
    request.headers.get(name.ci).map(_.value)

}
