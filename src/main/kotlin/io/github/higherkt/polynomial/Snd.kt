package io.github.higherkt.polynomial

import io.github.higherkt.Ap2
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor

@Higher
data class Snd<X, Y>(val it: Y) {
    override fun toString(): String = it.toString()

    companion object
}

fun <X, Y> snd(it: Y): Ap2<SndKind, X, Y> = Snd.inj(Snd(it))

fun Snd.Companion.bifunctor() = object : Bifunctor<SndKind> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) = Snd.lift { (it): Snd<X, Y> -> Snd<Z, W>(g(it)) }
}
