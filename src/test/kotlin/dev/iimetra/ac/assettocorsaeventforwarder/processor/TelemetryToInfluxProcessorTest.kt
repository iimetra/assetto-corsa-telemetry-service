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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TelemetryToInfluxProcessorTest(@Mock val influxDBClient: InfluxDBClient) {
    
    private lateinit var processor: TelemetryToInfluxProcessor

    @BeforeEach
    internal fun setUp() {
        processor = TelemetryToInfluxProcessor(influxDBClient, "test_bucket", "test_org")
    }

    @Test
    internal fun testProcess() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val driver = HandshakeResponse("carName", "driverName", 1, 1, "trackName", "trackConfig")
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
            .hasSize(12)
            .extracting("name", "time", "fields")
            .isNotEmpty
    }

    @Test
    internal fun testProcessorNoWriteForNoChanges() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val driver = HandshakeResponse("carName", "driverName", 1, 1, "trackName", "trackConfig")
        val carTelemetry = testCarTelemetryData()
        exchange.message.body = TelemetryByDriver(driver, carTelemetry)

        val writeApiMock = mock(WriteApi::class.java)
        `when`(influxDBClient.writeApi).thenReturn(writeApiMock)

        processor.process(exchange)
        processor.process(exchange)

        verify(writeApiMock).writePoints(anyString(), anyString(), anyList())
    }

    private fun testCarTelemetryData() = CarTelemetry(
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
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        floatArrayOf(5f),
        1f,
        1f,
        floatArrayOf(5f)
    )
}