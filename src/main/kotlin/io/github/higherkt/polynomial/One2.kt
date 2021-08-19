package io.github.higherkt.polynomial

import io.github.higherkt.Ap
import io.github.higherkt.Ap2

typealias One2Kind = Ap<K2Kind, Unit>
typealias One2<X, Y> = Ap2<One2Kind, X, Y>

fun <X, Y> one2(): One2<X, Y> = k2(Unit)
