package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap3
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor
import io.github.higherkt.typeclasses.Functor

@Higher
data class Clowns<P, C, J>(val it: Ap<P, C>) { companion object }

fun <P, C, J> clowns(it: Ap<P, C>): Ap3<ClownsKind, P, C, J> = Clowns.inj(Clowns(it))

fun <P> Clowns.Companion.bifunctor(p: Functor<P>) = object : Bifunctor<Ap<ClownsKind, P>> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) =
        Clowns.lift { (it): Clowns<P, X, Y> -> Clowns<P, Z, W>(p.fmap(f)(it))  }
}
