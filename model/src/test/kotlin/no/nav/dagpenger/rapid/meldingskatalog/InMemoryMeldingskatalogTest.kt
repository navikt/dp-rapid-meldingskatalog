package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.HendelseMelding
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.InMemoryMeldingskatalog
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class InMemoryMeldingskatalogTest {

    @Test
    fun testAddAndRetrieveMessageTypes() {
        val catalog = InMemoryMeldingskatalog()

        // Add message types
        catalog.leggTilMelding(HendelseMelding("typeA"))
        catalog.leggTilMelding(HendelseMelding("typeB"))

        // Retrieve message types
        val messageTypes = catalog.hentMeldingstyper()

        // Check if added message types are correctly retrieved
        assertEquals(setOf("typeA", "typeB"), messageTypes)
    }
}
