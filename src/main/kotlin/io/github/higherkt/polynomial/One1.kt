package io.github.higherkt.polynomial

import io.github.higherkt.Ap

typealias One1Kind = Ap<K1Kind, Unit>
typealias One1<X> = Ap<One1Kind, X>

fun <X> one1(): One1<X> = k1(Unit)
