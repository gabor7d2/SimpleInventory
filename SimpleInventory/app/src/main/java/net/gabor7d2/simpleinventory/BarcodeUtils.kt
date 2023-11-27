package net.gabor7d2.simpleinventory

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import net.gabor7d2.simpleinventory.model.Item
import java.io.File
import java.util.Date
import kotlin.math.ceil

object BarcodeUtils {

    const val BARCODE_LENGTH = 8

    private const val PDF_PAGE_WIDTH = 595
    private const val PDF_PAGE_HEIGHT = 842

    fun getBarcodeForItem(item: Item): String {
        return item.barcode.toString().padStart(BARCODE_LENGTH, '0')
    }

    fun generateBitmapForBarcode(barcodeText: String, width: Int = 800, height: Int = 300): Bitmap {
        try {
            val bitMatrix = MultiFormatWriter().encode(barcodeText, BarcodeFormat.CODE_128, width, height)
            val bitmap = BarcodeEncoder().createBitmap(bitMatrix)
            /*bitmap = removeWhiteFromBitmap(bitmap)
            if (requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                bitmap = invertBitmap(bitmap)
            }*/
            return bitmap
        } catch (e: WriterException) {
            throw RuntimeException("Error while generating barcode bitmap: ", e)
        }
    }

    private fun removeWhiteFromBitmap(bitmap: Bitmap): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in pixels.indices) {
            if (pixels[i] == -1) {
                pixels[i] = 0
            }
        }
        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }

    private fun invertBitmap(bitmap: Bitmap): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in pixels.indices) {
            pixels[i] = pixels[i] xor 0x00ffffff
        }
        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }

    fun exportBarcodes(context: Context, items: List<Item>) {
        val imagePaint = Paint()
        val textPaint = Paint()
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = ContextCompat.getColor(context, R.color.black)
        textPaint.textSize = 28f

        val pdfDocument = PdfDocument()

        for (i in 0 until ceil(items.size / 18.0).toInt()) {
            val myPageInfo = PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, i + 1).create()
            val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

            val canvas: Canvas = myPage.canvas

            for (x in 0 until 3) {
                for (y in 0 until 6) {
                    val itemIndex = i * 18 + x * 6 + y
                    if (itemIndex >= items.size) {
                        break
                    }
                    val item = items[itemIndex]
                    val barcodeText = getBarcodeForItem(item)
                    val barcode = generateBitmapForBarcode(barcodeText, 180, 60)

                    canvas.drawBitmap(barcode, 20f + x * 180f, 45f + y * 130f, imagePaint)
                    canvas.drawText(barcodeText, 42.5f + x * 180f, 135f + y * 130f, textPaint)
                }
            }

            pdfDocument.finishPage(myPage)
        }

        val pdfFile = File(context.filesDir, "Barcodes-${Date().time}.pdf")
        try {
            pdfDocument.writeTo(pdfFile.outputStream())
            Toast.makeText(context, "PDF file generated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF file", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }

        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", pdfFile)
        val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, "application/pdf")
        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}