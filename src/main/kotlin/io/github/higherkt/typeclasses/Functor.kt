package io.github.higherkt.typeclasses

import io.github.higherkt.Ap

interface Functor<F> {
    fun <X, Y> fmap(f: (X) -> Y): (Ap<F, X>) -> Ap<F, Y>
}
