package com.xep.thutiendien

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xep.thutiendien.entries.OrderEntry
import com.xep.thutiendien.module.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val api = Network.appApi
        CoroutineScope(Dispatchers.IO).launch {
            val res = api.fetchOrderList("")
            withContext(Dispatchers.Main){
                print(res)
                assertEquals(res.size, 4)
            }

        }
       // assertEquals("com.xep.thutiendien", appContext.packageName)
    }

}
