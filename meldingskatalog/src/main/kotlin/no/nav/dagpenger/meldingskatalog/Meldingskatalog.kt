package no.nav.dagpenger.meldingskatalog

import no.nav.dagpenger.meldingskatalog.Melding.Meldingstype.Hendelse

class Meldingskatalog(
    private val bygger: MeldingsinformasjonBygger,
    private val meldinger: List<KjentMelding>,
) {
    init {
        require(meldinger.groupBy { it.type }.all { it.value.size == 1 }) { "Ingen duplikate meldingstyper" }
    }

    fun identifiser(melding: UkjentMelding): Meldingsinformasjon? {
        return meldinger.firstOrNull { it.er(melding) }?.let {
            bygger.bygg(it.type, melding.json)
        }
    }
}

data class Melding(val navn: String, val meldingstype: Meldingstype) {
    enum class Meldingstype {
        Hendelse,
        Behov,
    }
}

data class KjentMelding(val type: Melding, private val strategi: MeldingIdentifiseringStrategi) {
    constructor(type: String, strategi: MeldingIdentifiseringStrategi) : this(Melding(type, Hendelse), strategi)

    fun er(melding: UkjentMelding) = strategi.identifiser(melding)
}

data class UkjentMelding(val json: String)
