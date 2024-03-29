package no.nav.dagpenger.meldingskatalog

import mu.KotlinLogging
import no.nav.dagpenger.meldingskatalog.api.meldingskatalogAPI
import no.nav.dagpenger.meldingskatalog.behov.BehovRepositoryPostgres
import no.nav.dagpenger.meldingskatalog.behov.BehovSporer
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.clean
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.runMigration
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepositoryPostgres
import no.nav.dagpenger.meldingskatalog.rivers.BehovRiver
import no.nav.dagpenger.meldingskatalog.rivers.HendelseRiver
import no.nav.dagpenger.meldingskatalog.rivers.LøsningRiver
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(private val config: Map<String, String>) : RapidsConnection.StatusListener {
    private val meldingRepository = RapidMeldingRepositoryPostgres()
    private val behovRepository =
        BehovRepositoryPostgres().also {
            meldingRepository.leggTilObserver(BehovSporer(it))
        }
    private val rapidsConnection: RapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(config)).apply {
            withKtorModule { meldingskatalogAPI(meldingRepository, behovRepository) }
        }.build()

    init {
        rapidsConnection.register(this)
        HendelseRiver(rapidsConnection, meldingRepository)
        BehovRiver(rapidsConnection, meldingRepository)
        LøsningRiver(rapidsConnection, meldingRepository)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        if (config["CLEAN_ON_STARTUP"] == "true") clean()
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
