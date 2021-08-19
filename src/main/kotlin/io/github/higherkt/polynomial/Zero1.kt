package io.github.higherkt.polynomial

import io.github.higherkt.Ap

typealias Zero1Kind = Ap<K1Kind, Nothing>
typealias Zero1<X> = Ap<Zero1Kind, X>

fun <X> zero1(): Zero1<X> = throw IllegalStateException()

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
fun <X, Y> Zero1<X>.refute(): Y = K1.prj(this).k
