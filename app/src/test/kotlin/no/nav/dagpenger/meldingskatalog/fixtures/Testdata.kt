package no.nav.dagpenger.meldingskatalog.fixtures

import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import java.time.LocalDateTime
import java.util.UUID

object Testdata {
    val behov: RapidMelding<Innholdstype.BehovType> =
        RapidMelding(
            konvolutt = Konvolutt(UUID.randomUUID(), LocalDateTime.now(), "behov", emptyList()),
            innhold = Pakkeinnhold(Innholdstype.BehovType(UUID.randomUUID(), "behov")),
            json = """{"behovId": "123", "behov": "behov"}""",
        )
    val hendelse: RapidMelding<Innholdstype.HendelseType> =
        RapidMelding(
            konvolutt = Konvolutt(UUID.randomUUID(), LocalDateTime.now(), "noe_skjedde", emptyList()),
            innhold = Pakkeinnhold(Innholdstype.HendelseType("noe_skjedde")),
            json = "{}",
        )
}
