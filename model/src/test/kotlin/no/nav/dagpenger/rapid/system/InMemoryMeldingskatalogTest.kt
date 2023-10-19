package no.nav.dagpenger.rapid.system

import no.nav.dagpenger.rapid.system.melding.HendelseMelding
import no.nav.dagpenger.rapid.system.meldingskatalog.InMemoryMeldingskatalog
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
