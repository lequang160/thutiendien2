package com.xep.thutiendien.fragment.order

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

class OrderViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
    val orderLiveData = SingleLiveEvent<List<OrderModel>>()
    val printLiveData = SingleLiveEvent<Boolean>()

    fun fetchOrder(page: String, tukhoa: String = "") {
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val order = safeApiCall { mApi.fetchOrderList("0", page, tukhoa) }
            withContext(Dispatchers.Main) {
                when (order) {
                    is ResultState.Success -> orderLiveData.postValue(order.data.map { it.toOrderModel() })
                    is ResultState.Error -> _text.value = order.exception.message
                }
            }
        }
    }

    fun updateOrder(id: String) {
        if (!ElectricityApplication.enable) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val status = safeApiCall { mApi.updateStatusOrder(id, "1") }
            withContext(Dispatchers.Main) {
                when (status) {
                    is ResultState.Success -> {
                        _text.value = status.data.KetQua
                        printLiveData.postValue(true)
                    }
                    is ResultState.Error -> _text.value = status.exception.message
                }
            }
        }
    }

    fun fetchheroku() {
        CoroutineScope(Dispatchers.IO).launch {
            val status = safeApiCall { mApi.fetchPermission() }
            withContext(Dispatchers.Main) {
                when (status) {
                    is ResultState.Success -> {
                        ElectricityApplication.enable = status.data.enable
                        if (!status.data.enable) {
                            _text.value = "Liên hệ để được hỗ trợ"
                        }
                    }
                    is ResultState.Error -> _text.value = status.exception.message
                }
            }
        }
    }
}