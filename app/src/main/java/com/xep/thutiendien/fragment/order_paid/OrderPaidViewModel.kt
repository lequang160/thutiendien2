package com.xep.thutiendien.fragment.order_paid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xep.thutiendien.ElectricityApplication
import com.xep.thutiendien.ResultState
import com.xep.thutiendien.base.BaseViewModel
import com.xep.thutiendien.models.OrderModel
import com.xep.thutiendien.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderPaidViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    val orderLiveData = SingleLiveEvent<List<OrderModel>>()
    val moneyLiveData = SingleLiveEvent<String>()
    val numberOrderLiveData = SingleLiveEvent<String>()

    fun fetchOrderPaid(page: String, tukhoa: String){
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val order = safeApiCall { mApi.fetchOrderList("1", page, tukhoa) }
            withContext(Dispatchers.Main){
                when(order){
                    is ResultState.Success -> orderLiveData.postValue(order.data.map { it.toOrderModel() })
                    is ResultState.Error -> _text.value = order.exception.message
                }
            }
        }
    }

    fun fetchSumMoney(){
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val order = safeApiCall { mApi.fetchSumMoney("1") }
            withContext(Dispatchers.Main){
                when(order){
                    is ResultState.Success -> moneyLiveData.postValue(order.data.TongTien)
                    is ResultState.Error -> _text.value = order.exception.message
                }
            }
        }
    }

    fun fetchNumberOrder(){
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val order = safeApiCall { mApi.fetchNumberOrder("1") }
            withContext(Dispatchers.Main){
                when(order){
                    is ResultState.Success -> numberOrderLiveData.postValue(order.data.SoDong)
                    is ResultState.Error -> _text.value = order.exception.message
                }
            }
        }
    }

    fun updateOrder(id: String){
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val status = safeApiCall { mApi.updateStatusOrder(id, "-1") }
            withContext(Dispatchers.Main){
                when(status){
                    is ResultState.Success ->{
                        _text.value = "Cập nhật thành công"
                    }
                    is ResultState.Error -> _text.value = status.exception.message
                }
            }
        }
    }
}