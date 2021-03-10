package dev.iimetra.ac.assettocorsaeventforwarder.processor

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import dev.iimetra.ac.assettocorsaeventforwarder.model.TelemetryByDriver
import dev.iimetra.ac.assettocorsaeventforwarder.toGPower
import dev.iimetra.ac.assettocorsaeventforwarder.toPercent
import dev.iimetra.assettocorsa4j.telemetry.model.response.CarTelemetry
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

    var prev: TelemetryByDriver? = null

    override fun process(exchange: Exchange) {
        val telemetryByDriver = exchange.message.getBody(TelemetryByDriver::class.java)
        if (telemetryByDriver != prev) {
            prev = telemetryByDriver

            val carTelemetry = telemetryByDriver.carTelemetry
            val driver = telemetryByDriver.driver

            val now = Instant.now()

            val speedPoint = pointOf("speed at current time", driver, "speed", carTelemetry.speedKmh, now)
            val rpmPoint = pointOf("engine RPM at current time", driver, "rpm", carTelemetry.engineRPM, now)
            val gasPoint = pointOf("gas at current time", driver, "gas", carTelemetry.gas.toPercent(), now)
            val brakePoint = pointOf("brake at current time", driver, "brake", carTelemetry.brake.toPercent(), now)
            val clutchPoint = pointOf("clutch at current time", driver, "clutch", carTelemetry.clutch.toPercent(), now)
            val accelerationPoint = pointOf("acceleration at current time", driver, "acceleration", carTelemetry.speedMs.toGPower(), now)

            val lapCompletenessPoint = pointOf("lap completeness", driver, "carPositionNormalized", carTelemetry.carPositionNormalized.toPercent(), now)

            val absEnabled = pointOf("isAbsEnabled", driver, "isAbsEnabled", carTelemetry.isAbsEnabled, now)
            val absInAction = pointOf("isAbsInAction", driver, "isAbsInAction", carTelemetry.isAbsInAction, now)
            val tcInAction = pointOf("isTcInAction", driver, "isTcInAction", carTelemetry.isTcInAction, now)
            val tcEnabled = pointOf("isTcEnabled", driver, "isTcEnabled", carTelemetry.isTcEnabled, now)
            val inPit = pointOf("isInPit", driver, "isInPit", carTelemetry.isInPit, now)
            val engineLimiterOn = pointOf("isEngineLimiterOn", driver, "isEngineLimiterOn", carTelemetry.isEngineLimiterOn, now)

            val suspensionHeightPoint = pointOfWheel("suspensionHeight", driver, carTelemetry.suspensionHeight, now)
            val angularSpeedPoint = pointOfWheel("wheelAngularSpeed", driver, carTelemetry.wheelAngularSpeed, now)
            val slipAnglePoint = pointOfWheel("slipAngle", driver, carTelemetry.slipAngle, now)
            val slipAngleContactPatchPoint = pointOfWheel("slipAngleContactPatch", driver, carTelemetry.slipAngleContactPatch, now)
            val slipRatioPoint = pointOfWheel("slipRatio", driver, carTelemetry.slipRatio, now)
            val tyreSlipPoint = pointOfWheel("tyreSlip", driver, carTelemetry.tyreSlip, now)
            val ndSlipPoint = pointOfWheel("ndSlip", driver, carTelemetry.ndSlip, now)
            val loadPoint = pointOfWheel("load", driver, carTelemetry.load, now)

            val coordsPoint = coordinatesPoint(driver, carTelemetry, now)

            val points = listOf(
                speedPoint,
                rpmPoint,
                gasPoint,
                brakePoint,
                clutchPoint,
                accelerationPoint,
                absEnabled,
                absInAction,
                tcInAction,
                tcEnabled,
                inPit,
                engineLimiterOn,
                suspensionHeightPoint,
                tyreSlipPoint,
                angularSpeedPoint,
                slipAnglePoint,
                slipAngleContactPatchPoint,
                slipRatioPoint,
                ndSlipPoint,
                loadPoint,
                coordsPoint,
                lapCompletenessPoint
            )
            influxDbClient.writeApi.use { it.writePoints(bucket, org, points) }
        }
    }

    private fun coordinatesPoint(driver: HandshakeResponse, carTelemetry: CarTelemetry, now: Instant): Point {
        return Point.measurement("carCoordinates")
            .time(now, WritePrecision.NS)
            .addTags(
                mapOf(
                    "driver" to driver.driverName,
                    "track" to driver.trackName,
                    "car" to driver.carName,
                    "track_config" to driver.trackConfig
                )
            )
            .addFields(
                mapOf(
                    "x" to carTelemetry.carCoordinates[0],
                    "y" to carTelemetry.carCoordinates[1],
                    "z" to carTelemetry.carCoordinates[2]
                )
            )
    }

    private fun pointOfWheel(measurement: String, driver: HandshakeResponse, wheels: FloatArray, now: Instant): Point {
        assert(wheels.size == 4) { "Only four wheel cars expected, but was ${wheels.size}" }
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
            .addFields(
                mapOf(
                    "leftFrontWheel" to wheels[0],
                    "rightFrontWheel" to wheels[1],
                    "leftRearWheel" to wheels[2],
                    "rightRearWheel" to wheels[3]
                )
            )
    }

    private fun pointOf(measurement: String, driver: HandshakeResponse, fieldName: String, fieldValue: Float, now: Instant): Point {
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

    private fun pointOf(measurement: String, driver: HandshakeResponse, fieldName: String, fieldValue: Boolean, now: Instant): Point {
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
