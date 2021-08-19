package io.github.higherkt.typeclasses

import io.github.higherkt.Ap
import io.github.higherkt.Ap2

interface Bifunctor<F> {
    fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W): (Ap2<F, X, Y>) -> Ap2<F, Z, W>
}

// TODO: functorial on the first argument

fun <F, A> Bifunctor<F>.functor() = object : Functor<Ap<F, A>> {
    override fun <X, Y> fmap(f: (X) -> Y) = bimap<A, X, A, Y>({ it }, f)
}
