package io.github.higherkt.sample

import io.github.higherkt.*
import io.github.higherkt.polynomial.*

typealias Tuple3Kind = Ap2<Product1Kind, IdKind, Ap2<Product1Kind, IdKind, IdKind>>
typealias Tuple3<X> = Ap<Tuple3Kind, X>

fun main() {
    val tuple3Dissection = Product1.dissection(Id.dissection(), Product1.dissection(Id.dissection(), Id.dissection()))
    val t3: Tuple3<Int> = product1(id(1), product1(id(2), id(3)))
    val f = tmap(tuple3Dissection) { it: Int -> it * 2 }
    println(f(t3))

    val exprPDissection = Sum1.dissection(K1.dissection<Int>(), Product1.dissection(Id.dissection(), Id.dissection()))
    val e = value(1) + (value(2) + value(3))

    val eval: (Expr) -> Int =
        TailCatamorphism(exprPDissection) {
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

    println(eval(e))
}
