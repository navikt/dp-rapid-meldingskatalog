package no.nav.dagpenger.rapid.meldingskatalog

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.lyttere.Meldingslogger
import no.nav.dagpenger.rapid.meldingskatalog.lyttere.Meldingsteller
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(configuration: Map<String, String>) : RapidsConnection.StatusListener {
    private val rapidsConnection: RapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(configuration)).build()

    init {
        rapidsConnection.register(this)
        Meldingskatalog(rapidsConnection).apply { kjenteMeldinger() }.also {
            it.leggTilLytter(Meldingslogger())
            it.leggTilLytter(Meldingsteller())
        }
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
