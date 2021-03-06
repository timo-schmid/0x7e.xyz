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

package xyz._0x7e.service.v1.protocol

import argonaut.Argonaut.casecodec2

case class ShortenError(code: Int, message: String)

object ShortenError {

  implicit def ShortenErrorCodec =
    casecodec2(ShortenError.apply, ShortenError.unapply)("code", "message")

}
