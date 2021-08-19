package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap3
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor
import io.github.higherkt.typeclasses.Functor

@Higher
data class Jokers<P, C, J>(val it: Ap<P, J>) { companion object }

fun <P, C, J> jokers(it: Ap<P, J>): Ap3<JokersKind, P, C, J> = Jokers.inj(Jokers(it))

fun <P> Jokers.Companion.bifunctor(p: Functor<P>) = object : Bifunctor<Ap<JokersKind, P>> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) =
        Jokers.lift { (it): Jokers<P, X, Y> -> Jokers<P, Z, W>(p.fmap(g)(it))  }
}
