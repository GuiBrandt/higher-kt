package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.Ap4
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Bifunctor

@Higher
data class Product2<P, Q, X, Y>(val left: Ap2<P, X, Y>, val right: Ap2<Q, X, Y>) {
    fun <Z, W> bimap(
        f: (Ap2<P, X, Y>) -> Ap2<P, Z, W>,
        g: (Ap2<Q, X, Y>) -> Ap2<Q, Z, W>
    ): Product2<P, Q, Z, W> = Product2(f(left), g(right))
    
    companion object
}

fun <P, Q, X, Y> product2(left: Ap2<P, X, Y>, right: Ap2<Q, X, Y>): Ap4<Product2Kind, P, Q, X, Y> =
    Product2.inj(Product2(left, right))

fun <P, Q> Product2.Companion.bifunctor(p: Bifunctor<P>, q: Bifunctor<Q>) = object : Bifunctor<Ap2<Product2Kind, P, Q>> {
    override fun <X, Y, Z, W> bimap(f: (X) -> Z, g: (Y) -> W) =
        Product2.lift { sum: Product2<P, Q, X, Y> -> sum.bimap(p.bimap(f, g), q.bimap(f, g)) }
}
