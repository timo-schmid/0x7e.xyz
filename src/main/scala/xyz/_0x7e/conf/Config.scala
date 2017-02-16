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

package xyz._0x7e.conf

import scala.sys.env
import scala.util.Try

/**
  * The application's configuration
  * - The application settings are read from environment variables
  * - Should there be no settings, default values will be used
  * These settings can be configured:
  * - HTTP_PROTOCOL
  * - HTTP_HOSTNAME
  * - HTTP_PORT
  * - DB_NAME
  * - DB_USER
  * - DB_PASSWORD
  */
object Config {

  // default values

  private val DEFAULT_HTTP_PROTOCOL = "http"

  private val DEFAULT_HTTP_HOSTNAME = "localhost"

  private val DEFAULT_HTTP_PORT = 8080

  private val DEFAULT_DB_NAME = "shortlinks"

  private val DEFAULT_DB_USER = "postgres"

  private val DEFAULT_DB_PASSWORD = "scala"

  // http settings
  object http {

    val protocol: String =
      env
        .get("HTTP_PROTOCOL")
        .filter(validProtocol)
        .getOrElse(DEFAULT_HTTP_PROTOCOL)

    val hostName: String =
      env.getOrElse("HTTP_HOSTNAME", DEFAULT_HTTP_HOSTNAME)

    val port: Int =
      env
        .get("HTTP_PORT")
        .flatMap(toIntOption)
        .getOrElse(DEFAULT_HTTP_PORT)

    private val shortPort: String =
      if(protocol.equalsIgnoreCase("http") && port == 80)
        ""
      else if(protocol.equalsIgnoreCase("https") && port == 443)
        ""
      else
        s":$port"

    val shortHostName: String =
      s"$hostName$shortPort"

    val longHostName: String =
      s"$protocol://$hostName$shortPort"

  }

  // db settings
  object db {

    val name: String =
      env.getOrElse("DB_NAME", DEFAULT_DB_NAME)

    val user: String =
      env.getOrElse("DB_USER", DEFAULT_DB_USER)

    val password: String =
      env.getOrElse("DB_PASSWORD", DEFAULT_DB_PASSWORD)

  }

  private def toIntOption(str: String): Option[Int] =
    Try(str.toInt).toOption

  private def validProtocol(protocol: String): Boolean =
    List("http", "https").exists(protocol.toLowerCase.equals)

}
