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

package xyz._0x7e.service.v1

import argonaut.Argonaut._
import doobie.imports._
import org.http4s._
import org.http4s.dsl._
import org.http4s.argonaut._
import org.http4s.util.CaseInsensitiveString

import scalaz.\/-
import scalaz.concurrent.Task
import xyz._0x7e.db.Urls
import xyz._0x7e.qr.QRCode
import xyz._0x7e.service.v1.protocol.{ShortenError, ShortenRequest, ShortenResult}

class ShortenUrl(val shortHostName: String, val fullHostName: String, xa: Transactor[Task]) {

  val service = HttpService {

    case GET -> Root /  key => {
      println("hier")
      Urls
        .findUrlByKey(key)
        .transact(xa)
        .flatMap { oKey =>
          oKey.map { key =>
            Ok(result(key).asJson)
          } getOrElse {
            NotFound(ShortenError(404, "Not found").asJson)
          }
        }
    }

    case httpRequest @ POST -> Root / "shorten" =>
      for {
        shortenRequest <- httpRequest.as(jsonOf[ShortenRequest])
        response       <- validateUrl(shortenRequest.url)(shortenUri)
      } yield response

    case GET -> Root / "qr" / key =>
      Ok(QRCode.fromString(full(key)))

  }

  private def validateUrl(url: String)(success: (Uri) => Task[Response]): Task[Response] =
    Uri.fromString(url) match {

      case \/-(uri)
        if uri.scheme.isDefined && uri.host.isDefined =>
        success(uri)

      case _ =>
        BadRequest(ShortenError(400, s"The url '$url' could not be parsed.").asJson)

    }

  private def isValidScheme(scheme: CaseInsensitiveString): Boolean =
    List("http".ci, "https".ci).contains(scheme)

  private def shortenUri(uri: Uri): Task[Response] =
    for {
      shortenResult <- create(uri.toString())
      response      <- Ok(shortenResult.asJson)
    } yield response

  private def create(url: String): Task[ShortenResult] =
    Urls.create(url).transact(xa).map(result)

  private def result(key: String): ShortenResult =
    ShortenResult(full(key), short(key), qr(key))

  private def short(key: String): String =
    shortHostName + "/" + key

  private def full(key: String): String =
    fullHostName + "/" + key

  private def qr(key: String): String =
    fullHostName + "/api/v1/qr/" + key


}
