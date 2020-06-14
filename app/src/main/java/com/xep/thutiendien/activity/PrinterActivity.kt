package com.xep.thutiendien.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.xep.thutiendien.*
import com.xep.thutiendien.models.OrderModel
import timber.log.Timber
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


class PrinterActivity : Activity(), Runnable {
    protected val TAG = "TAG"
    private val REQUEST_CONNECT_DEVICE = 1
    private val REQUEST_ENABLE_BT = 2
    var mScan: Button? =
        null
    lateinit var mPrint: Button
    lateinit var mDisc: Button
    var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val applicationUUID = UUID
        .fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    var mBluetoothDevice: BluetoothDevice? = null
    var BILL: String = ""

    var mOrder: OrderModel? = null
    lateinit var textView: TextView
    lateinit var imageView: ImageView

    override fun onCreate(mSavedInstanceState: Bundle?) {
        super.onCreate(mSavedInstanceState)
        setContentView(R.layout.activity_printer)

        val bundle = intent.extras
        if (bundle != null) {
            mOrder = bundle.getParcelable("data")
        }

        // --------------- create bill ----------------------------
        BILL = """
            <p style="font-family: Arial, Helvetica, sans-serif">
<b>Biên nhận thanh toán tiền điện</b><br>
Kỳ thanh toán: ${mOrder?.date}<br><br>

<b>Tên KH: ${mOrder?.customerName}</b><br><br>

Địa chỉ: ${mOrder?.address}<br>
Mã KH: ${mOrder?.customerId}<br><br>

<b>TỔNG TIỀN: ${mOrder?.amount} đ</b><br><br>
 
Ngày in: ${curentDate("dd/MM/yyyy")}<br>
Ngày thanh toán: ${curentDate()}<br>
HĐ điện tử: http://cskh.evnspc.vn<br>
Nơi thanh toán<br>
<b>Tên cửa hàng: Điểm thu Minh Hiền</b><br>
<b>SDT: 0382708876</b><br>
Địa chỉ: Khu Phước Hải<br>
Hotline: 1900 1006 - 1900 6906<br><br>

<b>ĐÃ THANH TOÁN</b><br><br>

Xác nhận của điểm giao dịch<br>
...........................<br>
...........................<br>
Cảm ơn quý khách và hen gặp lại! </p>
"""
        // -----------End create bill -----------------------

        textView = findViewById(R.id.text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(BILL, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView.text = Html.fromHtml(BILL);
        }
        textView.post {
            Timber.d("QUANG ${textView.width} : ${textView.height}")
        }

        if (XSharedPreferences(
                this,
                Gson()
            ).getBluetoothDeviceId().length > 3 && mBluetoothAdapter.isEnabled
        ) {
            mBluetoothDevice = mBluetoothAdapter
                .getRemoteDevice(
                    XSharedPreferences(
                        this,
                        Gson()
                    ).getBluetoothDeviceId())
            val mBlutoothConnectThread = Thread(this)
            mBlutoothConnectThread.start()
        }
        showLoading()



        mScan = findViewById<View>(R.id.Scan) as Button
        mScan!!.setOnClickListener {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (!mBluetoothAdapter.isEnabled) {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.enable()
                    ListPairedDevices()
                    val connectIntent = Intent(
                        this@PrinterActivity,
                        DeviceListActivity::class.java
                    )
                    startActivityForResult(
                        connectIntent,
                        REQUEST_CONNECT_DEVICE
                    )
                }
            } else {
                ListPairedDevices()
                val connectIntent = Intent(
                    this@PrinterActivity,
                    DeviceListActivity::class.java
                )
                startActivityForResult(
                    connectIntent,
                    REQUEST_CONNECT_DEVICE
                )
            }
        }
        mPrint = findViewById<View>(R.id.mPrint) as Button
        mPrint.setOnClickListener(View.OnClickListener {
            if (mBluetoothAdapter.isEnabled) {
                if (ElectricityApplication.mBluetoothSocket != null && ElectricityApplication.mBluetoothSocket!!.isConnected) {
                    //startPrint(mOrder)
                    ElectricityApplication.print(
                        textView,
                        mOrder!!
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Máy in chưa được kết nối, vui lòng kết nối máy in và thử lại !",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this,
                    "Bluetooth đã tắt, vui lòng mở bluetooth và thử lại !",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
        mDisc = findViewById<View>(R.id.dis) as Button
        mDisc.setOnClickListener(View.OnClickListener { if (mBluetoothAdapter != null) mBluetoothAdapter.disable() })
    } // onCreate

    /*   fun startPrint(mOrder: OrderModel?) {
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

                       // Setting Width
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
                           resources,
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
                           EscPosImage(CoffeeImageAndroidImpl(bitmap), algorithm)
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
                               EscPosImage(CoffeeImageAndroidImpl(testB), algorithm)
                           escpos.write(imageWrapper, escposImage1)
                       }
                       // ----------------Xong ------------------------

                   } catch (e: Exception) {
                       Log.e("MainActivity", "Exe ", e)
                   }
               }
           }
           t.start()
       }*/

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        try {
            if (mBluetoothSocket != null) mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }
    }

    override fun onBackPressed() {
        try {
            if (mBluetoothSocket != null) mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(
        mRequestCode: Int, mResultCode: Int,
        mDataIntent: Intent
    ) {
        // super.onActivityResult(mRequestCode, mResultCode, mDataIntent)
        when (mRequestCode) {
            REQUEST_CONNECT_DEVICE -> if (mResultCode == Activity.RESULT_OK) {
                val mExtra = mDataIntent.extras
                val mDeviceAddress = mExtra!!.getString("DeviceAddress")
                XSharedPreferences(this, Gson()).saveBluetoothDeviceId(mDeviceAddress!!)
                mBluetoothDevice = mBluetoothAdapter
                    .getRemoteDevice(mDeviceAddress)
                mBluetoothConnectProgressDialog = ProgressDialog.show(
                    this,
                    "Connecting...", mBluetoothDevice?.name + " : "
                            + mBluetoothDevice?.address, true, false
                )
                val mBlutoothConnectThread = Thread(this)
                mBlutoothConnectThread.start()
                // pairToDevice(mBluetoothDevice); This method is replaced by
                // progress dialog with thread
            }
            REQUEST_ENABLE_BT -> if (mResultCode == Activity.RESULT_OK) {
                ListPairedDevices()
                val connectIntent = Intent(
                    this@PrinterActivity,
                    DeviceListActivity::class.java
                )
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
            } else {
                Toast.makeText(this@PrinterActivity, "Message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ListPairedDevices() {
        val mPairedDevices = mBluetoothAdapter
            .bondedDevices
        if (mPairedDevices.size > 0) {
            for (mDevice in mPairedDevices) {
                Log.v(
                    TAG, "PairedDevices: " + mDevice.name + "  "
                            + mDevice.address
                )
            }
        }
    }

    override fun run() {
        try {
            ElectricityApplication.mBluetoothSocket =
                mBluetoothDevice?.createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter.cancelDiscovery()
            ElectricityApplication.mBluetoothSocket?.connect()
            mHandler.sendEmptyMessage(0)
        } catch (eConnectException: IOException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException)
            mProgressLoading?.dismiss()
            mHandler.sendEmptyMessage(1)
            closeSocket(mBluetoothSocket)
            return
        }
    }

    private fun closeSocket(nOpenSocket: BluetoothSocket?) {
        try {
            nOpenSocket?.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }
    }

    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            hideLoading()
            mBluetoothConnectProgressDialog?.dismiss()
            if (msg.what == 0) {
                //startPrint(mOrder)
                ElectricityApplication.print(
                    textView,
                    mOrder!!
                )
                Toast.makeText(this@PrinterActivity, "Kết nối thành công", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@PrinterActivity, "Kết nối thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun curentDate(parttern: String = "dd-MM-yyyy HH:mm:ss"): String {
        val dateFormat = SimpleDateFormat(parttern)
        return dateFormat.format(Date())
    }

    private var mProgressLoading: KProgressHUD? = null
    fun showLoading() {
        mProgressLoading = KProgressHUD.create(this)
        mProgressLoading?.apply {
            setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            setLabel("Please wait")
            setCancellable(false)
            setAnimationSpeed(2)
            setDimAmount(0.5f)
            show()
        }
    }

    fun hideLoading() {
        mProgressLoading?.apply {
            Handler().postDelayed({
                dismiss()
            }, 1000)
        }
    }

/*    public fun removeAccents(str: String): String {
        val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val str1 = pattern.matcher(nfdNormalizedString).replaceAll("")
        return str1
    }*/


}
