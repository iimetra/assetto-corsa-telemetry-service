package dev.iimetra.ac.assettocorsaeventforwarder.processor

import dev.iimetra.assettocorsa4j.telemetry.client.ACClient
import dev.iimetra.assettocorsa4j.telemetry.model.response.CarTelemetry
import dev.iimetra.assettocorsa4j.telemetry.model.response.HandshakeResponse
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.support.DefaultExchange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class AssettoCorsaPullProcessorTest(@Mock val acClient: ACClient) {

    private lateinit var processor: AssettoCorsaPullProcessor

    @BeforeEach
    internal fun setUp() {
        processor = AssettoCorsaPullProcessor(acClient)
    }

    @Test
    internal fun testInit() {
        val carName = "carName"
        val driverName = "driverName"
        val trackName = "trackName"
        val trackConfig = "trackConfig"
        `when`(acClient.connect()).thenReturn(HandshakeResponse(carName, driverName, 1, 1, trackName, trackConfig))

        processor.afterInit()

        verify(acClient).connect()
        verify(acClient).subscribeCarTelemetry()

        assertThat(processor.handshakeResponse)
            .isNotNull
            .hasFieldOrPropertyWithValue("carName", carName)
            .hasFieldOrPropertyWithValue("driverName", driverName)
            .hasFieldOrPropertyWithValue("trackName", trackName)
            .hasFieldOrPropertyWithValue("trackConfig", trackConfig)
    }

    @Test
    internal fun testProcess() {
        val exchange = DefaultExchange(DefaultCamelContext())
        val carTelemetry = CarTelemetry()
        `when`(acClient.connect()).thenReturn(HandshakeResponse())
        `when`(acClient.carTelemetry).thenReturn(carTelemetry)

        processor.afterInit()

        assertThat(exchange.message.body).isNull()

        processor.process(exchange)

        verify(acClient).carTelemetry

        assertThat(exchange.message.body)
            .isNotNull
    }
}