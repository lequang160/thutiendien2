package com.xep.thutiendien

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.widget.TextView
import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.EscPosConst
import com.github.anastaciocintra.escpos.image.BitImageWrapper
import com.github.anastaciocintra.escpos.image.Bitonal
import com.github.anastaciocintra.escpos.image.BitonalThreshold
import com.github.anastaciocintra.escpos.image.EscPosImage
import com.google.gson.Gson
import com.xep.thutiendien.models.OrderModel
import timber.log.Timber
import java.io.IOException
import java.util.*

class ElectricityApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Thread {
            try {
                mBluetoothSocket =
                    BluetoothAdapter.getDefaultAdapter()
                        .getRemoteDevice(XSharedPreferences(this, Gson()).getBluetoothDeviceId())
                        .createRfcommSocketToServiceRecord(applicationUUID)
                mBluetoothSocket?.connect()
            } catch (eConnectException: IOException) {
            }
        }.start()

    }

    companion object {
        lateinit var context: Context
        var enable: Boolean = true
        var mBluetoothSocket: BluetoothSocket? = null
        private val applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB")

        fun print(textView: TextView, mOrder: OrderModel) {
            val t: Thread = object : Thread() {
                override fun run() {
                    try {
                        val os = ElectricityApplication.mBluetoothSocket
                            ?.outputStream

                        // ---------- Settings printer --------------------------------
                        // Setting height
//                    val gs = 29
//                    os?.write(intToByteArray(gs).toInt())
//                    val h = 104
//                    os?.write(intToByteArray(h).toInt())
//                    val n = 162
//                    os?.write(intToByteArray(n).toInt())
//
//                    // Setting Width
//                    val gs_width = 29
//                    os?.write(intToByteArray(gs_width).toInt())
//                    val w = 119
//                    os?.write(intToByteArray(w).toInt())
//                    val n_width = 2
//                    os?.write(intToByteArray(n_width).toInt())
                        //---------------End settings printer ----------------
                        val escpos = EscPos(os)
                        escpos.setCharsetName(EscPos.CharacterCodeTable.WCP1258_Vietnamese.toString())
                        val options = BitmapFactory.Options()
                        options.inScaled = false
                        val bitmap = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.airpay2,
                            options
                        )
                        val imageWrapper = BitImageWrapper()
                        imageWrapper.setJustification(EscPosConst.Justification.Center)

                        // using ordered dither for dithering algorithm with default values

                        // using ordered dither for dithering algorithm with default values

                        //----Print logo airpay --------------
                        val algorithm: Bitonal = BitonalThreshold()
                        val escposImage =
                            EscPosImage(
                                BillImageAndroidImpl(
                                    bitmap
                                ), algorithm
                            )
                        escpos.write(imageWrapper, escposImage)
                        //-----------Print hoa don -------------------
                        textView.post {
                            val testB = Bitmap.createBitmap(
                                textView.width,
                                textView.height,
                                Bitmap.Config.ARGB_8888
                            )
                            val c = Canvas(testB)
                            //textView.layout(0, 0, 320, 800)
                            textView.isDrawingCacheEnabled = true
                            textView.draw(c)
                            val escposImage1 =
                                EscPosImage(
                                    BillImageAndroidImpl(
                                        testB
                                    ), algorithm
                                )
                            escpos.write(imageWrapper, escposImage1)
                        }
                        // ----------------Xong ------------------------

                    } catch (e: Exception) {
                        Log.e("MainActivity", "Exe ", e)
                    }
                }
            }
            t.start()
        }
    }


}