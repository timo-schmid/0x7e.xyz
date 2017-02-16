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

package xyz._0x7e.service

import doobie.imports._
import doobie.util.transactor.Transactor
import org.http4s.dsl._
import org.http4s.twirl._
import org.http4s._

import xyz._0x7e.db.Urls
import xyz._0x7e.dsl._

import scalaz.concurrent.Task

/**
  * Routes that display the website
  *
  * @param xa The database transactor
  */
class WebsiteService(xa: Transactor[Task]) {

  val service = HttpService {

    // route for the index page
    case GET -> Root =>
      Ok(html.index.apply())

    // route for the redirects
    case request @ GET -> Root / key if isValidKey(key) =>
      routeRedirect(key, request)

    // route for asset files
    case request @ GET -> Root / "assets" / path if isAssetFile(path) =>
      staticFile("assets/" + path, request)

  }

  // routes a redirect
  private def routeRedirect(key: String, request: Request): Task[Response] =
    for {
      oUrl      <- Urls.findUrlByKey(key).transact(xa)
      response  <- redirectUrl(key, oUrl, request)
    } yield response


  // redirects to an url
  private def redirectUrl(key: String, oUrl: Option[String], request: Request): Task[Response] =
    oUrl match {

      case Some(url) =>
        for {
          _         <- trackClick(key, request)
          redirect  <- PermanentRedirect(Uri.unsafeFromString(url))
        } yield redirect

      case None =>
        NotFound()

    }

  // tracks a click
  private def trackClick(key: String, request: Request): Task[Int] =
    Urls.trackClick(
      key,
      request.remoteAddr,
      request.header("User-Agent"),
      request.header("Referrer")
    ).transact(xa)

  // returns whether a file is an asset file
  private def isAssetFile(file: String): Boolean =
    List("bg.jpg", "0x7e.css", "0x7e.js").exists(file.equals)

  // serves static files from the jar
  private def staticFile(file: String, request: Request) =
    StaticFile
      .fromResource("/" + file, Some(request))
      .map(Task.now)
      .getOrElse(NotFound())

}
