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

package xyz._0x7e.webjars

import org.http4s.dsl._
import org.http4s.{HttpService, StaticFile}

import scalaz.concurrent.Task

class WebjarService(webjars: Map[String, String]) {

  val service = HttpService {

    // route for files from the webjars
    case request @ GET -> Root / libraryName / libraryVersion / path if isWebjar(libraryName, libraryVersion) =>
        StaticFile
          .fromResource(
            webjarPath(libraryName, libraryVersion, path),
            Some(request)
          )
          .map(Task.now)
          .getOrElse(NotFound())

  }

  private def isWebjar(libraryName: String, libraryVersion: String): Boolean =
    webjars.exists(webjar => webjar._1.equals(libraryName) && webjar._2.equals(libraryVersion))

  private def webjarPath(libraryName: String, libraryVersion: String, path: String): String =
    s"/META-INF/resources/webjars/$libraryName/$libraryVersion/$path"

}
