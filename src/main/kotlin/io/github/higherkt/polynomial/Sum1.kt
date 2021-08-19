package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.Ap3
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Functor

@Higher
sealed class Sum1<P, Q, X> {
    abstract fun <Y> bimap(f: (Ap<P, X>) -> Ap<P, Y>, g: (Ap<Q, X>) -> Ap<Q, Y>): Sum1<P, Q, Y>

    companion object
}

data class L1<P, Q, X>(val left: Ap<P, X>): Sum1<P, Q, X>() {
    override fun <Y> bimap(f: (Ap<P, X>) -> Ap<P, Y>, g: (Ap<Q, X>) -> Ap<Q, Y>) = L1<P, Q, Y>(f(left))
    override fun toString(): String = "L[$left]"
}

data class R1<P, Q, X>(val right: Ap<Q, X>): Sum1<P, Q, X>() {
    override fun <Y> bimap(f: (Ap<P, X>) -> Ap<P, Y>, g: (Ap<Q, X>) -> Ap<Q, Y>) = R1<P, Q, Y>(g(right))
    override fun toString(): String = "R[$right]"
}

fun <P, Q, X> l1(x: Ap<P, X>): Ap3<Sum1Kind, P, Q, X> = Sum1.inj(L1(x))
fun <P, Q, X> r1(x: Ap<Q, X>): Ap3<Sum1Kind, P, Q, X> = Sum1.inj(R1(x))

fun <P, Q> Sum1.Companion.functor(p: Functor<P>, q: Functor<Q>) = object : Functor<Ap2<Sum1Kind, P, Q>> {
    override fun <X, Y> fmap(f: (X) -> Y) = Sum1.lift { sum: Sum1<P, Q, X> -> sum.bimap(p.fmap(f), q.fmap(f)) }
}
