package com.xep.thutiendien

import org.junit.Test

import org.junit.Assert.*
import java.text.Normalizer
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val BILL =
            """ 
    
    
    Biên nhận thanh toán tiền điện
    Kỳ thanh toán: 5/2020
    Tên KH: Nguyễn Thanh Tùng
    Địa chỉ: Thửa 210, tờ 23, Ấp Xóm Gò Bà Ký, Xã Long Phước
    Mã KH: pk07000171041
    TỔNG TIỀN: 1.000.000 (VND)
    Ngày in: 24/05/2020
    Ngày thanh toán: 24-05-2020 21:500:46
    HD điện tử: http:cskh.evnspc.vn
    Nơi thanh toán
    Tên cửa hàng: Điểm thu Minh Hiền
    SDT: 03827088766
    Địa chỉ: Khu Phước Hải
    Hotline: 1900 1006 - 1900 6906
    ĐÃ THANH TOÁN
    Xác nhận của điểm giao dịch
    
    ............................
    ............................
    
    Cảm ơn quý khách và hẹn gặp lại!
    
"""
        val nfdNormalizedString = Normalizer.normalize(BILL, Normalizer.Form.NFD);
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        print(pattern.matcher(nfdNormalizedString).replaceAll(""))
    }
}
