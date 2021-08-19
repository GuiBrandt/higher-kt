package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Functor

@Higher
data class Id<X>(val it: X) {
    override fun toString(): String = it.toString()

    companion object
}

fun <X> id(it: X): Ap<IdKind, X> = Id.inj(Id(it))

fun Id.Companion.functor() = object : Functor<IdKind> {
    override fun <X, Y> fmap(f: (X) -> Y) = Id.lift { (it): Id<X> -> Id(f(it)) }
}
