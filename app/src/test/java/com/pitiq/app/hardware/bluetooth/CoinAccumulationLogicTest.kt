package com.pitiq.app.hardware.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CoinAccumulationLogicTest {

    private lateinit var coinTotal: MutableStateFlow<Int>

    @Before
    fun setUp() {
        coinTotal = MutableStateFlow(0)
    }

    private fun accumulate(message: BluetoothMessage) {
        when (message) {
            is BluetoothMessage.CoinInserted -> coinTotal.update { it + message.amountPeso }
            is BluetoothMessage.CoinBufferResponse -> coinTotal.update { it + message.totalPeso }
            else -> Unit
        }
    }

    @Test
    fun `four ten-peso coins reach session price`() {
        repeat(4) { accumulate(BluetoothMessage.CoinInserted(10)) }
        assertEquals(40, coinTotal.value)
    }

    @Test
    fun `mixed denominations total correctly`() {
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.CoinInserted(5))
        accumulate(BluetoothMessage.CoinInserted(5))
        assertEquals(40, coinTotal.value)
    }

    @Test
    fun `overpayment accumulates beyond session price`() {
        repeat(6) { accumulate(BluetoothMessage.CoinInserted(10)) }
        assertEquals(60, coinTotal.value)
    }

    @Test
    fun `forty one-peso coins reach session price`() {
        repeat(40) { accumulate(BluetoothMessage.CoinInserted(1)) }
        assertEquals(40, coinTotal.value)
    }

    @Test
    fun `buffer response from reconnect adds to existing total`() {
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.CoinBufferResponse(20))
        assertEquals(30, coinTotal.value)
    }

    @Test
    fun `buffer response alone sets total correctly`() {
        accumulate(BluetoothMessage.CoinBufferResponse(40))
        assertEquals(40, coinTotal.value)
    }

    @Test
    fun `disconnect ack does not affect total`() {
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.DisconnectAck)
        assertEquals(10, coinTotal.value)
    }

    @Test
    fun `reset clears accumulated total`() {
        accumulate(BluetoothMessage.CoinInserted(10))
        accumulate(BluetoothMessage.CoinInserted(10))
        coinTotal.value = 0
        assertEquals(0, coinTotal.value)
    }
}
