package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.Ap3
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Functor

@Higher
data class Product1<P, Q, X>(val left: Ap<P, X>, val right: Ap<Q, X>) {
    override fun toString(): String = "($left, $right)"

    fun <Y> bimap(f: (Ap<P, X>) -> Ap<P, Y>, g: (Ap<Q, X>) -> Ap<Q, Y>) = Product1(f(left), g(right))

    companion object
}

fun <P, Q, X> product1(left: Ap<P, X>, right: Ap<Q, X>): Ap3<Product1Kind, P, Q, X> =
    Product1.inj(Product1(left, right))

fun <P, Q> Product1.Companion.functor(p: Functor<P>, q: Functor<Q>) = object : Functor<Ap2<Product1Kind, P, Q>> {
    override fun <X, Y> fmap(f: (X) -> Y) =
        Product1.lift { product: Product1<P, Q, X> -> product.bimap(p.fmap(f), q.fmap(f)) }
}
