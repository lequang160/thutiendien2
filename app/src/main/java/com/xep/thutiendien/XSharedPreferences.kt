package com.xep.thutiendien

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import timber.log.Timber

@SuppressLint("CommitPrefEdits")
class XSharedPreferences constructor(val context: Context, val gson: Gson) {

    private var mPref: SharedPreferences
    private var mEditor: SharedPreferences.Editor


    init {
        mPref = context.getSharedPreferences(
            "PREF_FILE_NAME",
            Context.MODE_PRIVATE
        )
        mEditor = mPref.edit()
    }



    fun saveBluetoothDeviceId(
        deviceId: String
    ) {
        mEditor.putString(DEVICE_ID, deviceId)
        mEditor.apply()
    }

    fun getBluetoothDeviceId(): String {
        return mPref.getString(DEVICE_ID, " ") ?: ""
    }





    companion object {
        const val DEVICE_ID = "DEVICE_ID"
    }

}