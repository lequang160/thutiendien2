package com.xep.thutiendien.entries

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.xep.thutiendien.models.OrderModel

class OrderEntry {
    @Expose
    @SerializedName("HoTen")
    var customerName: String = ""

    @Expose
    @SerializedName("SDT")
    var phoneNumber: String = ""

    @Expose
    @SerializedName("TongNop")
    var amount: String = ""

    @Expose
    @SerializedName("DiaChi")
    var address: String = ""

    @Expose
    @SerializedName("MaCG")
    var transaction: String = ""

    @Expose
    @SerializedName("ThangNam")
    var date: String = ""

    @Expose
    @SerializedName("TrangThai")
    var status: String = ""

    @Expose
    @SerializedName("MaKhachHang")
    var customerId: String = ""

    @Expose
    @SerializedName("GhiChu")
    var note: String = ""

    @Expose
    @SerializedName("idThuTien")
    var id: String = ""

    fun toOrderModel(): OrderModel {
        return OrderModel(
            customerId = customerId,
            address = address,
            amount = amount,
            customerName = customerName,
            date = date,
            id = id,
            note = note,
            phoneNumber = phoneNumber,
            status = status,
            transaction = transaction
        )
    }

}