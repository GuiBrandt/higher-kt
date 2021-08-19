package io.github.higherkt.polynomial

import io.github.higherkt.Ap2
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor

@Higher
data class Fst<X, Y>(val it: X) {
    override fun toString(): String = it.toString()

    companion object
}

fun <X, Y> fst(it: X): Ap2<FstKind, X, Y> = Fst.inj(Fst(it))

fun Fst.Companion.bifunctor() = object : Bifunctor<FstKind> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) = Fst.lift { (it): Fst<X, Y> -> Fst<Z, W>(f(it)) }
}
