package io.github.higherkt.sample

import io.github.higherkt.Ap
import io.github.higherkt.Ap2
import io.github.higherkt.polynomial.*
import io.github.higherkt.typeclasses.Functor

typealias ExprPKind = Ap2<Sum1Kind, Ap<K1Kind, Int>, Ap2<Product1Kind, IdKind, IdKind>>
typealias ExprP<X> = Ap<ExprPKind, X>

val exprPFunctor: Functor<ExprPKind> = Sum1.functor(K1.functor(), Product1.functor(Id.functor(), Id.functor()))

typealias Expr = FixT<ExprPKind>

fun <X> valueP(i: Int): ExprP<X> = l1(k1(i))
fun <X> addP(e1: X, e2: X): ExprP<X> = r1(product1(id(e1), id(e2)))

fun value(i: Int): Expr = FixT(valueP(i))
fun add(e1: Expr, e2: Expr) = FixT(addP(e1, e2))

operator fun Expr.plus(other: Expr) = add(this, other)

fun <P, V> catamorphism(functor: Functor<P>, phi: (Ap<P, V>) -> V): (FixT<P>) -> V =
    { (p) -> phi(functor.fmap(catamorphism(functor, phi))(p)) }

val eval: (Expr) -> Int =
    catamorphism(exprPFunctor) {
        when (val expr = Sum1.prj(it)) {
            is L1 -> K1.prj(expr.left).k
            is R1 -> {
                val (a, b) = Product1.prj(expr.right)
                val (x) = Id.prj(a)
                val (y) = Id.prj(b)
                x + y
            }
        }
    }

fun main() {
    val x = value(1) + value(2) + value(3) + value(4)
    println(x)
    println(eval(x))
}
