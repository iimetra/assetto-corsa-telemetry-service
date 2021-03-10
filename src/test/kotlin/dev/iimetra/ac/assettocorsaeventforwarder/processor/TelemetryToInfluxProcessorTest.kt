package dev.iimetra.ac.assettocorsaeventforwarder.processor

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.WriteApi
import com.influxdb.client.write.Point
import com.nhaarman.mockitokotlin2.argumentCaptor
import dev.iimetra.ac.assettocorsaeventforwarder.model.TelemetryByDriver
import dev.iimetra.assettocorsa4j.telemetry.model.response.CarTelemetry
import dev.iimetra.assettocorsa4j.telemetry.model.response.HandshakeResponse
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.support.DefaultExchange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TelemetryToInfluxProcessorTest(@Mock val influxDBClient: InfluxDBClient) {

    private lateinit var processor: TelemetryToInfluxProcessor
    private val driver = HandshakeResponse("carName", "driverName", 1, 1, "trackName", "trackConfig")

    @BeforeEach
    internal fun setUp() {
        processor = TelemetryToInfluxProcessor(influxDBClient, "test_bucket", "test_org")
    }

    @Test
    internal fun testProcess() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val carTelemetry = testCarTelemetryData()
        exchange.message.body = TelemetryByDriver(driver, carTelemetry)

        val writeApiMock = mock(WriteApi::class.java)
        `when`(influxDBClient.writeApi).thenReturn(writeApiMock)

        processor.process(exchange)

        val argumentCaptor = argumentCaptor<MutableList<Point>>()
        verify(writeApiMock).writePoints(anyString(), anyString(), argumentCaptor.capture())

        val points = argumentCaptor.firstValue
        assertThat(points)
            .isNotEmpty
            .hasSize(22)
            .extracting("name", "time", "fields")
            .isNotEmpty
    }

    @Test
    internal fun testProcessorNoWriteForNoChanges() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val carTelemetry = testCarTelemetryData()
        exchange.message.body = TelemetryByDriver(driver, carTelemetry)

        val writeApiMock = mock(WriteApi::class.java)
        `when`(influxDBClient.writeApi).thenReturn(writeApiMock)

        processor.process(exchange)
        processor.process(exchange)

        verify(writeApiMock).writePoints(anyString(), anyString(), anyList())
    }

    @Test
    internal fun testProcessor_sixWheelsCar() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val carTelemetry = testCarTelemetryData(true)
        exchange.message.body = TelemetryByDriver(driver, carTelemetry)

        val writeApiMock = mock(WriteApi::class.java)
        `when`(influxDBClient.writeApi).thenReturn(writeApiMock)

        assertThatThrownBy { processor.process(exchange) }
            .isInstanceOf(AssertionError::class.java)
            .hasMessage("Only four wheel cars expected, but was 6")
    }

    private fun testCarTelemetryData(sixWheel: Boolean = false) = CarTelemetry(
        1,
        2,
        100f,
        160f,
        5.5f,
        true,
        true,
        true,
        true,
        true,
        true,
        'b',
        1f,
        1f,
        1f,
        1,
        1,
        1,
        1,
        1f,
        0f,
        0.5f,
        5555.5f,
        5f,
        1,
        5f,
        if (sixWheel) floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f) else floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(5f, 5f, 5f, 5f),
        floatArrayOf(4.98f, 4.98f, 5.03f, 5.03f),
        1f,
        1f,
        floatArrayOf(5f, 5f, 5f)
    )
}