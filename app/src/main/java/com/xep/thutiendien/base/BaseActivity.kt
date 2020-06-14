package com.xep.thutiendien.base

import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.xep.thutiendien.module.Network

abstract class BaseActivity : AppCompatActivity(){
    var mApi = Network.appApi

    override fun onDestroy() {
        super.onDestroy()
        mApi = null
    }

    private var mProgressLoading: KProgressHUD? = null
    public fun showLoading() {
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

    public fun hideLoading() {
        mProgressLoading?.apply {
            Handler().postDelayed({
                dismiss()
            }, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}