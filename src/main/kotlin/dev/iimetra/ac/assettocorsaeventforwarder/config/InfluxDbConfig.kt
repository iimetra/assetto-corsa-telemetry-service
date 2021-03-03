package dev.iimetra.ac.assettocorsaeventforwarder.config

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDbConfig {

    @Bean
    fun influxDbClient(@Value("\${influx.db.url}") url: String, @Value("\${influx.db.token}") token: String, @Value("\${influx.db.org}") org: String) : InfluxDBClient {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org).enableGzip()
    }
}