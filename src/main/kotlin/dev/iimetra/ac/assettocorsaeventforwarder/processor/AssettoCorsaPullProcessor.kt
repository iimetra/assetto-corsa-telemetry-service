package dev.iimetra.ac.assettocorsaeventforwarder.processor

import dev.iimetra.ac.assettocorsaeventforwarder.model.TelemetryByDriver
import dev.iimetra.assettocorsa4j.telemetry.client.ACClient
import dev.iimetra.assettocorsa4j.telemetry.model.response.HandshakeResponse
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class AssettoCorsaPullProcessor(private val acClient: ACClient) : Processor {

    lateinit var handshakeResponse: HandshakeResponse

    @PostConstruct
    fun afterInit() {
        handshakeResponse = acClient.connect()
        acClient.subscribeCarTelemetry()
    }

    override fun process(exchange: Exchange) {
        exchange.message.body = TelemetryByDriver(handshakeResponse, acClient.carTelemetry)
    }

}
