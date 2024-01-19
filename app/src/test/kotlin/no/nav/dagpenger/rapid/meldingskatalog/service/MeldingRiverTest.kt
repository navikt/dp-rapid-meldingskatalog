package no.nav.dagpenger.rapid.meldingskatalog.service

import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.Meldingslytter
import no.nav.dagpenger.rapid.meldingskatalog.rapid.JsonMessageBygger
import no.nav.dagpenger.rapid.meldingskatalog.rapid.hendelse
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MeldingRiverTest {
    private val rapid = TestRapid()
    private val testLytter = TestLytter()
    private val meldingskatalog = Meldingskatalog(JsonMessageBygger(), listOf(hendelse("vedtak_fattet")))
    private val river = MeldingRiver(rapid, meldingskatalog, listOf(testLytter))

    @Test
    fun `identifiserer meldinger`() {
        rapid.sendTestMessage(json)

        assertEquals(1, testLytter.gjenkjenteMeldinger)
    }

    private val json = JsonMessage.newMessage("vedtak_fattet").toJson()
}

private class TestLytter : Meldingslytter {
    var gjenkjenteMeldinger = 0

    override fun gjenkjentMelding(melding: Meldingsinformasjon) {
        gjenkjenteMeldinger++
    }

    override fun ukjentMelding(melding: String) {
        TODO("Not yet implemented")
    }
}
