package io.github.higherkt.algebraic

import io.github.higherkt.annotations.Higher

@Higher
sealed class Either<X, Y> { companion object }
data class Left<X, Y>(val left: X): Either<X, Y>()
data class Right<X, Y>(val right: Y): Either<X, Y>()
