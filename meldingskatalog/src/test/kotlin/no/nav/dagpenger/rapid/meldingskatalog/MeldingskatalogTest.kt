package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MeldingskatalogTest {
    private val rapid = TestRapid()

    @Test
    fun `Teller meldinger som kjennes igjen`() {
        val teller = Meldingsteller()
        val katalog = Meldingskatalog(rapid).apply {
            behov("foob")
            behov("foo")
            behov("this")
            hendelse("this")
        }

        katalog.behov("fooby")
        katalog.leggTilLytter(teller)

        // Kjente meldinger telles
        val kjentBehov = JsonMessage.newNeed(listOf("foob"), emptyMap())
        rapid.sendTestMessage(kjentBehov.toJson())
        assertEquals(1, teller.kjenteMeldinger)

        // Ukjente meldinger telles
        val ukjentMelding = JsonMessage.newMessage("arne", emptyMap())
        rapid.sendTestMessage(ukjentMelding.toJson())
        assertEquals(1, teller.ukjenteMeldinger)

        // Ulike kjente meldinger telles
        rapid.sendTestMessage(JsonMessage.newNeed(listOf("foo"), emptyMap()).toJson())
        assertEquals(3, teller.ukjenteMeldinger + teller.kjenteMeldinger)

        // Pings blir ignorert
        rapid.sendTestMessage(JsonMessage.newMessage("ping", emptyMap()).toJson())
        assertEquals(3, teller.ukjenteMeldinger + teller.kjenteMeldinger)
    }
}

private class Meldingsteller : Meldingslytter {
    var kjenteMeldinger = 0
    var ukjenteMeldinger = 0

    override fun gjenkjentMelding(melding: IdentifisertMelding) {
        kjenteMeldinger++
    }

    override fun ukjentMelding(melding: String) {
        ukjenteMeldinger++
    }
}
