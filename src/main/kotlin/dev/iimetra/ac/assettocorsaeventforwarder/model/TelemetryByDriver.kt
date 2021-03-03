package dev.iimetra.ac.assettocorsaeventforwarder.model

import dev.iimetra.assettocorsa4j.telemetry.model.response.CarTelemetry
import dev.iimetra.assettocorsa4j.telemetry.model.response.HandshakeResponse

data class TelemetryByDriver(val driver: HandshakeResponse, val carTelemetry: CarTelemetry)