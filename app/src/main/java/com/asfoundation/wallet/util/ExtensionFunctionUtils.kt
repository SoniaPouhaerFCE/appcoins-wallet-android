package com.asfoundation.wallet.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.WindowManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 *
 * Class file to create kotlin extension functions
 *
 */

fun BigDecimal.scaleToString(scale: Int): String {
  val format = DecimalFormat("#.##")
  return format.format(this.setScale(scale, RoundingMode.FLOOR))
}

fun Throwable?.isNoNetworkException(): Boolean {
  return this != null && (this is IOException || this.cause != null && this.cause is IOException)
}

fun Bitmap.mergeWith(centeredImage: Bitmap): Bitmap {

  val combined = Bitmap.createBitmap(width, height, config)
  val canvas = Canvas(combined)
  canvas.drawBitmap(this, Matrix(), null)

  val resizeLogo =
      Bitmap.createScaledBitmap(centeredImage, canvas.width / 5, canvas.height / 5, true)
  val centreX = (canvas.width - resizeLogo.width) / 2f
  val centreY = (canvas.height - resizeLogo.height) / 2f
  canvas.drawBitmap(resizeLogo, centreX, centreY, null)
  return combined
}

fun String.generateQrCode(windowManager: WindowManager, logo: Drawable): Bitmap {
  val size = Point()
  windowManager.defaultDisplay
      .getSize(size)
  val imageSize = (size.x * 0.9).toInt()
  val bitMatrix =
      MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, imageSize, imageSize,
          null)
  val barcodeEncoder = BarcodeEncoder()
  val qrCode = barcodeEncoder.createBitmap(bitMatrix)
  return qrCode.mergeWith(logo.toBitmap())
}

fun Drawable.toBitmap(): Bitmap {
  if (this is BitmapDrawable) {
    return this.bitmap
  }
  val bitmap =
      Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  this.setBounds(0, 0, canvas.width, canvas.height)
  this.draw(canvas)
  return bitmap
}

inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
  var sum = BigDecimal.ZERO
  for (element in this) {
    sum += selector(element)
  }
  return sum
}