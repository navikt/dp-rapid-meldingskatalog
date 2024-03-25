package no.nav.dagpenger.meldingskatalog

import mu.KotlinLogging
import no.nav.dagpenger.meldingskatalog.api.meldingskatalogAPI
import no.nav.dagpenger.meldingskatalog.db.MeldingRepositoryInMemory
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.clean
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.runMigration
import no.nav.dagpenger.meldingskatalog.rivers.BehovRiver
import no.nav.dagpenger.meldingskatalog.rivers.HendelseRiver
import no.nav.dagpenger.meldingskatalog.rivers.LøsningRiver
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(configuration: Map<String, String>) : RapidsConnection.StatusListener {
    private val repository = MeldingRepositoryInMemory()
    private val rapidsConnection: RapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(configuration)).apply {
            withKtorModule { meldingskatalogAPI(repository) }
        }.build()

    init {
        rapidsConnection.register(this)
        HendelseRiver(rapidsConnection, repository)
        BehovRiver(rapidsConnection, repository)
        LøsningRiver(rapidsConnection, repository)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        clean()
        runMigration()
        logger.info { "Starter applikasjonen" }
    }

    override fun onShutdown(rapidsConnection: RapidsConnection) {
        logger.info { "Skrur av applikasjonen" }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
