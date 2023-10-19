package no.nav.dagpenger.rapid.system

import mu.KotlinLogging
import no.nav.dagpenger.rapid.system.alarm.KafkaAlarmPublisher
import no.nav.dagpenger.rapid.system.alarm.KombinertLoggPublisher
import no.nav.dagpenger.rapid.system.alarm.LoggAlarmPublisher
import no.nav.dagpenger.rapid.system.river.MeldingskatalogSink
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(configuration: Map<String, String>) : RapidsConnection.StatusListener {
    private val rapidsConnection: RapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(configuration)).build()

    init {
        rapidsConnection.register(this)
        val alarmPublisher = KombinertLoggPublisher(
            LoggAlarmPublisher(),
            // KafkaAlarmPublisher(rapidsConnection),
        )
        MeldingskatalogSink(
            rapidsConnection,
            TellendeMeldingsmottak(kjenteMeldinger, alarmPublisher),
        )
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        logger.info { "Starter applikasjonen" }
    }

    override fun onShutdown(rapidsConnection: RapidsConnection) {
        logger.info { "Skrur av applikasjonen" }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
