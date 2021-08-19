package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.Ap3
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor
import io.github.higherkt.typeclasses.Functor

@Higher
data class K2<A, X, Y>(val k: A) {
    override fun toString(): String = k.toString()

    fun <Z, W> coerce(): K2<A, Z, W> = K2(k)

    companion object
}

fun <A, X, Y> k2(k: A): Ap3<K2Kind, A, X, Y> = K2.inj(K2(k))
fun <A, X, Y, Z, W> Ap3<K2Kind, A, X, Y>.coerce() = K2.lift { k: K2<A, X, Y> -> k.coerce<Z, W>() }(this)

fun <A> K2.Companion.bifunctor() = object : Bifunctor<Ap<K2Kind, A>> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) = K2.lift { (k): K2<A, X, Y> -> K2<A, Z, W>(k) }
}
