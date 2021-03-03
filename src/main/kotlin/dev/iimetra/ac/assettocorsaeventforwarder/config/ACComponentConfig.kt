package dev.iimetra.ac.assettocorsaeventforwarder.config

import dev.iimetra.assettocorsa4j.telemetry.client.ACClient
import dev.iimetra.assettocorsa4j.telemetry.serializer.PoJoToBinarySerializer
import dev.iimetra.assettocorsa4j.telemetry.serializer.Reader
import dev.iimetra.assettocorsa4j.telemetry.serializer.Writer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.DatagramSocket

@Configuration
class ACComponentConfig {

    @Bean
    fun acClient(@Value("\${device.ip.address}") deviceIpAddress: String): ACClient {
        return ACClient.of(deviceIpAddress, DatagramSocket(), PoJoToBinarySerializer(Writer(), Reader()))
    }
}