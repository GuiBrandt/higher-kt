package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Functor

@Higher
data class K1<A, X>(val k: A) {
    override fun toString(): String = k.toString()

    fun <Y> coerce(): K1<A, Y> = K1(k)

    companion object
}

fun <A, X> k1(k: A): Ap2<K1Kind, A, X> = K1.inj(K1(k))
fun <A, X, Y> Ap2<K1Kind, A, X>.coerce() = K1.lift { k: K1<A, X> -> k.coerce<Y>() }(this)

fun <A> K1.Companion.functor() = object : Functor<Ap<K1Kind, A>> {
    override fun <X, Y> fmap(f: (X) -> Y) = K1.lift { (k): K1<A, X> -> K1<A, Y>(k) }
}
