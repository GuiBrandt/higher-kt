package io.github.higherkt.sample

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.typeclasses.Functor
import io.github.higherkt.polynomial.*

typealias MaybeKind = Ap2<Sum1Kind, One1Kind, IdKind>
typealias Maybe<X> = Ap<MaybeKind, X>

fun <X> empty(): Maybe<X> = l1(k1(Unit))
fun <X> just(x: X): Maybe<X> = r1(id(x))

val maybeFunctor: Functor<MaybeKind> = Sum1.functor(K1.functor(), Id.functor())

fun main() {
    val f = maybeFunctor.fmap { n: Int -> n * 2 }

    val a = empty<Int>()
    println(a)
    println(f(a))

    val b = just(5)
    println(b)
    println(f(b))
}
