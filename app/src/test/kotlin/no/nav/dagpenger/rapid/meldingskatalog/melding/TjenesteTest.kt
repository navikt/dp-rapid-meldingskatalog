package no.nav.dagpenger.rapid.meldingskatalog.melding

import no.nav.dagpenger.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.UkjentMelding
import no.nav.dagpenger.rapid.meldingskatalog.rapid.JsonMessageBygger
import no.nav.dagpenger.rapid.meldingskatalog.rapid.behov
import no.nav.dagpenger.rapid.meldingskatalog.rapid.hendelse
import no.nav.helse.rapids_rivers.JsonMessage
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TjenesteTest {
    @Test
    fun `JsonMessageBygger og meldingskatalogen spiller sammen`() {
        val katalog =
            Meldingskatalog(
                JsonMessageBygger(),
                listOf(hendelse("vedtak_fattet"), behov("journalpost")),
            )

        val identifisert = katalog.identifiser(UkjentMelding(json))

        assertNotNull(identifisert, "Vi har identifisert meldingen")
    }

    private val json = JsonMessage.newMessage("vedtak_fattet").toJson()
}
