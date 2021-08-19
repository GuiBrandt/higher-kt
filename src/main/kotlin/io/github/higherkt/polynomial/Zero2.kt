package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2

typealias Zero2Kind = Ap<K2Kind, Nothing>
typealias Zero2<X, Y> = Ap2<Zero2Kind, X, Y>

fun <X, Y> zero2(): Zero2<X, Y> = throw IllegalStateException()

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
fun <X, Y, Z> Zero2<X, Y>.refute(): Z = K2.prj(this).k
