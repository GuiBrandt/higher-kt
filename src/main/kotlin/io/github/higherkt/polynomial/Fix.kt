package io.github.higherkt.polynomial

import io.github.higherkt.Ap

interface Fix<P, T> {
    fun inF(it: Ap<P, T>): T
    fun outF(fix: T): Ap<P, T>
}

data class FixT<P>(val it: Ap<P, FixT<P>>) : Ap<P, FixT<P>> by it {
    override fun toString(): String = it.toString()

    companion object {
        fun <P> instance(): Fix<P, FixT<P>> = object : Fix<P, FixT<P>> {
            override fun inF(it: Ap<P, FixT<P>>): FixT<P> = FixT(it)
            override fun outF(fix: FixT<P>): Ap<P, FixT<P>> = fix.it
        }
    }
}

fun <A, B> fix(f: ((A) -> B) -> ((A) -> B)): (A) -> B = { x -> f(fix(f))(x) }
