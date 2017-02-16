package xyz._0x7e

import java.text.SimpleDateFormat
import java.util.Date

object Asset {

  def at(path: String): String =
    s"/assets/$path?v=$version"

  private def version =
    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())

}
