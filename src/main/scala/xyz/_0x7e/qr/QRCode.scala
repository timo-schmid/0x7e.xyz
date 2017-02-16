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

package xyz._0x7e.qr

import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.{BarcodeFormat, EncodeHintType}

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import javax.imageio.ImageIO

import scala.collection.JavaConverters.mapAsJavaMap

import scalaz.concurrent.Task

object QRCode {

  private val WHITE: Int = 255 << 16 | 255 << 8 | 255

  private val BLACK: Int = 0

  def fromString(string: String): Task[InputStream] =
    for {
      bitMatrix     <- encode(string, 256, 256)
      bufferedImage <- toBufferedImage(bitMatrix)
      inputStream   <- toJpeg(bufferedImage)
    } yield inputStream

  private val hints: Map[EncodeHintType, _] = Map(
    EncodeHintType.MARGIN -> 0
  )

  private def encode(string: String, width: Int, height: Int): Task[BitMatrix] = Task {
    new QRCodeWriter().encode(string, BarcodeFormat.QR_CODE, width, height, mapAsJavaMap(hints))
  }

  private def toBufferedImage(bitMatrix: BitMatrix): Task[BufferedImage] = Task {
    val image = new BufferedImage(bitMatrix.getWidth, bitMatrix.getHeight, BufferedImage.TYPE_INT_RGB)
    for {
      w <- 0 until bitMatrix.getWidth
      h <- 0 until bitMatrix.getHeight
    } yield image.setRGB(w, h, pixel(bitMatrix.get(w, h)))
    image
  }

  private def toJpeg(image: BufferedImage): Task[InputStream] = Task {
    val os = new ByteArrayOutputStream
    ImageIO.write(image, "jpg", os)
    new ByteArrayInputStream(os.toByteArray)
  }

  private def pixel(on: Boolean): Int =
    if(on) BLACK else WHITE

}
