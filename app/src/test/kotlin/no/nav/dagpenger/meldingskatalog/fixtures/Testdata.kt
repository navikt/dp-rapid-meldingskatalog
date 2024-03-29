package no.nav.dagpenger.meldingskatalog.fixtures

import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Sporing
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import java.time.LocalDateTime
import java.util.UUID

object Testdata {
    val meldingsreferanseId1 = UUID.randomUUID()
    val meldingsreferanseId2 = UUID.randomUUID()
    val behov: RapidMelding<Innholdstype.BehovType> =
        RapidMelding(
            konvolutt =
                Konvolutt(
                    meldingsreferanseId1,
                    LocalDateTime.now(),
                    "behov",
                    listOf(Sporing(meldingsreferanseId1, LocalDateTime.now(), "service", "instance", "image")),
                ),
            innhold = Pakkeinnhold(Innholdstype.BehovType(UUID.randomUUID(), "behov")),
            json = """{"behovId": "123", "behov": "behov"}""",
        )
    val hendelse: RapidMelding<Innholdstype.HendelseType> =
        RapidMelding(
            konvolutt =
                Konvolutt(
                    meldingsreferanseId2,
                    LocalDateTime.now(),
                    "noe_skjedde",
                    listOf(Sporing(meldingsreferanseId2, LocalDateTime.now(), "service", "instance", "image")),
                ),
            innhold = Pakkeinnhold(Innholdstype.HendelseType("noe_skjedde")),
            json = "{}",
        )
}
