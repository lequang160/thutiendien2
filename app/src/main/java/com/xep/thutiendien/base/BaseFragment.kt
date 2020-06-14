package com.xep.thutiendien.base

import android.os.Handler
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.xep.thutiendien.module.Network
import kotlinx.coroutines.delay

abstract class BaseFragment : Fragment() {
    var mApi = Network.appApi

    override fun onDetach() {
        super.onDetach()
        mApi = null
    }

    private var mProgressLoading: KProgressHUD? = null
    public fun showLoading() {
        mProgressLoading = KProgressHUD.create(activity)
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
}