package dev.iimetra.ac.assettocorsaeventforwarder.processor

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import dev.iimetra.ac.assettocorsaeventforwarder.model.TelemetryByDriver
import dev.iimetra.ac.assettocorsaeventforwarder.toGPower
import dev.iimetra.ac.assettocorsaeventforwarder.toPercent
import dev.iimetra.assettocorsa4j.telemetry.model.response.HandshakeResponse
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TelemetryToInfluxProcessor(
    private val influxDbClient: InfluxDBClient,
    @Value("\${influx.db.bucket}")
    private val bucket: String,
    @Value("\${influx.db.org}")
    private val org: String
) : Processor {

    override fun process(exchange: Exchange) {
        val telemetryByDriver = exchange.message.getBody(TelemetryByDriver::class.java)
        val carTelemetry = telemetryByDriver.carTelemetry
        val driver = telemetryByDriver.driver

        val now = Instant.now()

        val speedPoint = pointOf("speed at current time", driver, "speed", carTelemetry.speedKmh, now)
        val rpmPoint = pointOf("engine RPM at current time", driver, "rpm", carTelemetry.engineRPM, now)
        val gasPoint = pointOf("gas at current time", driver, "gas", carTelemetry.gas.toPercent(), now)
        val brakePoint = pointOf("brake at current time", driver, "brake", carTelemetry.brake.toPercent(), now)
        val clutchPoint = pointOf("clutch at current time", driver, "clutch", carTelemetry.clutch.toPercent(), now)
        val accelerationPoint = pointOf("acceleration at current time", driver, "acceleration", carTelemetry.speedMs.toGPower(), now)

        influxDbClient.writeApi.use { it.writePoints(bucket, org, listOf(speedPoint, rpmPoint, gasPoint, brakePoint, clutchPoint, accelerationPoint)) }
    }

    private fun pointOf(measurement: String, driver: HandshakeResponse, fieldName: String, fieldValue: Float, now: Instant) : Point {
        return Point.measurement(measurement)
            .time(now, WritePrecision.NS)
            .addTags(
                mapOf(
                    "driver" to driver.driverName,
                    "track" to driver.trackName,
                    "car" to driver.carName,
                    "track_config" to driver.trackConfig
                )
            )
            .addField(fieldName, fieldValue)
    }
}
