package dev.iimetra.ac.assettocorsaeventforwarder.route

import dev.iimetra.ac.assettocorsaeventforwarder.processor.AssettoCorsaPullProcessor
import dev.iimetra.ac.assettocorsaeventforwarder.processor.TelemetryToInfluxProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TelemetryRouterTest(
    @Mock val assettoCorsaPullProcessor: AssettoCorsaPullProcessor,
    @Mock val telemetryToInfluxProcessor: TelemetryToInfluxProcessor
) {

    private lateinit var router: TelemetryRouter

    @BeforeEach
    internal fun setUp() {
        router = TelemetryRouter(assettoCorsaPullProcessor, telemetryToInfluxProcessor)
    }

    @Test
    internal fun testInitialization() {
        assertThat(router)
            .isNotNull
    }
}