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

package xyz._0x7e.db

import doobie.imports._

import scala.util.Random

object Urls {

  /**
    * Creates the db schema
    *
    * @return The number of affected rows
    */
  def createSchema: ConnectionIO[Int] =
    for {
      numCreateUrls <- createUrlsTable
      numCreateHits <- createHitsTable
    } yield numCreateUrls + numCreateHits

  /**
    * Creates the "urls" table if it doesn't exist
    *
    * @return The number of affected rows
    */
  def createUrlsTable: ConnectionIO[Int] =
    sql""" CREATE TABLE IF NOT EXISTS urls (
             key VARCHAR(7) PRIMARY KEY,
             url VARCHAR(255) NOT NULL
           ) """.update.run

  /**
    * Creates the "hits" table if it doesn't exist
    *
    * @return The number of affected rows
    */
  def createHitsTable: ConnectionIO[Int] =
    sql""" CREATE TABLE IF NOT EXISTS hits (
             key VARCHAR(7) NOT NULL REFERENCES urls (key),
             ip VARCHAR(15),
             ua TEXT,
             referrer TEXT,
             ts TIMESTAMP WITH TIME ZONE
           ) """.update.run

  /**
    * Creates a new url in the db and returns the alphanumeric key.
    * If the key was already used, the operation is retried
    *
    * @param url The url to shorten
    * @return The key for the shortened url
    */
  def create(url: String): ConnectionIO[String] = {
    val key = randomUrlKey()
    findUrlByKey(key).flatMap {
      case Some(_) => create(url)
      case None    => insert(key, url)
    }
  }

  /**
    * Tracks a click on a link by a user
    * @param key The key of the link
    * @param ip The ip of the user
    * @param ua The user agent of the user
    * @param referrer The referrer
    * @return The amount of updated rows
    */
  def trackClick(key: String, ip: Option[String], ua: Option[String], referrer: Option[String]): ConnectionIO[Int] =
    sql""" INSERT INTO hits (key, ip, ua, referrer, ts) VALUES ($key, $ip, $ua, $referrer, CURRENT_TIMESTAMP) """.update.run

  /**
    * Tries to insert a key and a value into the database
    *
    * @param key The key
    * @param url The URL
    * @return The key for the shortened url
    */
  def insert(key: String, url: String): ConnectionIO[String] =
    sql""" INSERT INTO urls (key, url) VALUES ($key, $url) """.update.run.map(i => key)

  /**
    * Finds an URL by the shorthand key
    *
    * @param key The key for the shortened url
    * @return The URL associated with the key
    */
  def findUrlByKey(key: String): ConnectionIO[Option[String]] =
    sql""" SELECT url FROM urls WHERE key = $key """.query[String].option

  /**
    * Generates a new 7 character long random key (alphanumeric)
    *
    * @return The new url key
    */
  def randomUrlKey(): String =
    Random.alphanumeric.take(7).mkString

}
