package io.github.higherkt.polynomial

import io.github.higherkt.Ap2
import io.github.higherkt.Ap4
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor

@Higher
sealed class Sum2<P, Q, X, Y> {
    abstract fun <Z, W> bimap(
        f: (Ap2<P, X, Y>) -> Ap2<P, Z, W>,
        g: (Ap2<Q, X, Y>) -> Ap2<Q, Z, W>
    ): Sum2<P, Q, Z, W>
    
    companion object
}

data class L2<P, Q, X, Y>(val left: Ap2<P, X, Y>): Sum2<P, Q, X, Y>() {
    override fun <Z, W> bimap(
        f: (Ap2<P, X, Y>) -> Ap2<P, Z, W>,
        g: (Ap2<Q, X, Y>) -> Ap2<Q, Z, W>
    ) = L2<P, Q, Z, W>(f(left))
}

data class R2<P, Q, X, Y>(val right: Ap2<Q, X, Y>): Sum2<P, Q, X, Y>() {
    override fun <Z, W> bimap(
        f: (Ap2<P, X, Y>) -> Ap2<P, Z, W>,
        g: (Ap2<Q, X, Y>) -> Ap2<Q, Z, W>
    ) = R2<P, Q, Z, W>(g(right))
}

fun <P, Q, X, Y> l2(x: Ap2<P, X, Y>): Ap4<Sum2Kind, P, Q, X, Y> = Sum2.inj(L2(x))
fun <P, Q, X, Y> r2(x: Ap2<Q, X, Y>): Ap4<Sum2Kind, P, Q, X, Y> = Sum2.inj(R2(x))

fun <P, Q> Sum2.Companion.bifunctor(p: Bifunctor<P>, q: Bifunctor<Q>) = object : Bifunctor<Ap2<Sum2Kind, P, Q>> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) =
        Sum2.lift { sum: Sum2<P, Q, X, Y> -> sum.bimap(p.bimap(f, g), q.bimap(f, g)) }
}
