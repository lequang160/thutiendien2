package com.xep.thutiendien.services

import com.xep.thutiendien.entries.*
import retrofit2.http.GET
import retrofit2.http.Query

interface AppApi {
    @GET("/api/thutiendien/danhsachkhachhang")
    suspend fun fetchOrderList(@Query("isStatus") status: String, @Query("Page") page: String, @Query("TuKhoa") tukhoa: String): List<OrderEntry>

    @GET("/api/thutiendien/thutienkhachhang")
    suspend fun updateStatusOrder(@Query("idThuTien") id: String,
                                  @Query("TrangThai") status: String) : StatusEntry

    @GET("/api/thutiendien/tongtiennop?TuKhoa=")
    suspend fun fetchSumMoney(@Query("isTrangThai") status: String
                                 ) : SumMoneyEntry

    @GET("/api/thutiendien/demhoadon?TuKhoa=")
    suspend fun fetchNumberOrder(@Query("Status") status: String
    ) : NumberOrderEntry

    @GET("https://le-quang.herokuapp.com/api/enable")
    suspend fun fetchPermission()  : EnableEntry
}