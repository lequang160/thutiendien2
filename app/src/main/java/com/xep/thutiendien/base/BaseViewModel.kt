package com.xep.thutiendien.base

import androidx.lifecycle.ViewModel
import com.xep.thutiendien.ResultState
import com.xep.thutiendien.module.Network
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException

abstract class BaseViewModel : ViewModel() {
    var mApi = Network.appApi

    override fun onCleared() {
        super.onCleared()
        mApi = null
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> T): ResultState<T> = newsApiOutput(call)

    private suspend fun <T : Any> newsApiOutput(
        call: suspend () -> T
    ): ResultState<T> {
        return try {
            ResultState.Success(call.invoke())
        } catch (e: HttpException) {
            ResultState.Error(e)
        } catch (e: TimeoutCancellationException) {
            ResultState.Error(e)
        } catch (e: Throwable) {
            ResultState.Error(Exception(e.message))
        }
    }
}