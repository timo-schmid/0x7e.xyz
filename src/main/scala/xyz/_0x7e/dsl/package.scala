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

package xyz._0x7e

import org.http4s.Request

import scala.util.matching.Regex

package object dsl {

  // Regex to match the key
  val RE_KEY: Regex = "[0-9a-zA-Z]+".r

  // Returns whether a string is a key
  def isValidKey(key: String): Boolean =
    key.length == 7 && RE_KEY.findFirstIn(key).isDefined

  // adds extra operations to a request
  implicit def toRequestOps(request: Request): RequestOps =
    new RequestOps(request)

}
