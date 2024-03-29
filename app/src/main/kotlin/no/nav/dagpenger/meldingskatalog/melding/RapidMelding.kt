package no.nav.dagpenger.meldingskatalog.melding

import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.HendelseType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.LøsningType
import java.util.UUID

sealed interface Innholdstype {
    data class HendelseType(
        val navn: String,
    ) : Innholdstype

    data class BehovType(
        val behovId: UUID,
        val behov: String,
    ) : Innholdstype

    data class LøsningType(
        val behovId: UUID,
        val løser: String,
    ) : Innholdstype
}

data class Pakkeinnhold<T>(
    private val innhold: List<T> = emptyList(),
) : List<T> by innhold {
    constructor(vararg innhold: T) : this(innhold.toList())
}

class RapidMelding<T : Innholdstype>(
    val konvolutt: Konvolutt,
    val innhold: Pakkeinnhold<T>,
    val json: String,
) : RapidKonvolutt by konvolutt {
    val type get() = innhold.first()

    constructor(
        konvolutt: Konvolutt,
        json: String,
        vararg innhold: T,
    ) : this(konvolutt, Pakkeinnhold(innhold.toList()), json)
}

typealias HendelseMessage = RapidMelding<HendelseType>
typealias BehovMessage = RapidMelding<BehovType>
typealias LøsningMessage = RapidMelding<LøsningType>
