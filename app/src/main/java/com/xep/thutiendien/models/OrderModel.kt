package com.xep.thutiendien.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderModel(
    var customerName: String,
    var phoneNumber: String,
    var amount: String,
    var address: String,
    var transaction: String,
    var date: String,
    var status: String,
    var customerId: String,
    var note: String,
    var id: String
) : Parcelable