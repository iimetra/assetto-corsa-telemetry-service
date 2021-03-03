package dev.iimetra.ac.assettocorsaeventforwarder.route

import dev.iimetra.ac.assettocorsaeventforwarder.processor.AssettoCorsaPullProcessor
import dev.iimetra.ac.assettocorsaeventforwarder.processor.TelemetryToInfluxProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class TelemetryRouter(
    val assettoCorsaPullProcessor: AssettoCorsaPullProcessor,
    val telemetryToInfluxProcessor: TelemetryToInfluxProcessor
) : RouteBuilder() {

    override fun configure() {
        from("timer://foo?period=10")
            .process(assettoCorsaPullProcessor)
            .process(telemetryToInfluxProcessor)
    }
}