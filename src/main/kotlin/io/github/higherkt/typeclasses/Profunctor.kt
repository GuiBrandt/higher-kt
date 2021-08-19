package io.github.higherkt.typeclasses

import io.github.higherkt.Ap
import io.github.higherkt.Ap2

interface Profunctor<F> {
    fun <X, Y, Z, W> dimap(f: (X) -> Z, g: (Y) -> W): (Ap2<F, Z, Y>) -> Ap2<F, X, W>
}

fun <F, A> Profunctor<F>.functor() = object : Functor<Ap<F, A>> {
    override fun <X, Y> fmap(f: (X) -> Y) = dimap<A, X, A, Y>({ it }, f)
}
