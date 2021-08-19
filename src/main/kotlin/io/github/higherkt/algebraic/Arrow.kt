package io.github.higherkt.algebraic

import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Profunctor

@Higher
data class Arrow<A, B>(private val fn: (A) -> B): (A) -> B by fn { companion object }

fun Arrow.Companion.profunctor() = object : Profunctor<ArrowKind> {
    override fun <X, Y, Z, W> dimap(f: (X) -> Z, g: (Y) -> W) =
        Arrow.lift<Z, Y, X, W> { Arrow { x -> g(it(f(x))) } }
}
