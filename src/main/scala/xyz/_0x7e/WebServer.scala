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

import doobie.imports._
import doobie.util.transactor.DriverManagerTransactor
import org.http4s.server.{Server, ServerApp}
import org.http4s.server.blaze.BlazeBuilder
import xyz._0x7e.service.v1.ShortenUrl
import xyz._0x7e.conf.Config
import xyz._0x7e.db.Urls
import xyz._0x7e.service.{ServerInfoService, WebsiteService}
import xyz._0x7e.webjars.WebjarService

import scalaz.concurrent.Task

/**
  * The main entry point of the application
  * - Connects to the database
  * - Creates the database tables, if they don't exist
  * - Starts the webserver
  */
object WebServer extends ServerApp {

  // The database transactor
  private val xa = DriverManagerTransactor[Task](
    "org.postgresql.Driver",
    s"jdbc:postgresql://${Config.db.host}:${Config.db.port}/${Config.db.name}",
    Config.db.user,
    Config.db.password
  )

  // Routes for server info
  private val serverInfo = new ServerInfoService(List("127.0.0.1"))

  // Routes for the website
  private val websiteRoutes = new WebsiteService(xa)

  // Routes for Webjars
  private val webjarRoutes = new WebjarService(Map(
    "material-design-lite" -> BuildInfo.mdlVersion,
    "jquery" -> BuildInfo.jqueryVersion
  ))

  // Routes for the API
  private val apiRoutes = new ShortenUrl(
    Config.http.shortHostName,
    Config.http.longHostName,
    xa
  )

  // starts the server
  override def server(args: List[String]): Task[Server] =
    Urls.createSchema.transact(xa).flatMap { i =>
      BlazeBuilder
        .bindHttp(8080)
        .mountService(serverInfo.service, "/")
        .mountService(websiteRoutes.service, "/")
        .mountService(webjarRoutes.service, "/assets/webjars")
        .mountService(apiRoutes.service, "/api/v1")
        .start
    }

}

