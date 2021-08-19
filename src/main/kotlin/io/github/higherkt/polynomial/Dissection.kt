package io.github.higherkt.polynomial

import io.github.higherkt.*
import io.github.higherkt.algebraic.*

interface Dissection<P, D> {
    fun <C, J> right(it: Ap<P, J>): Either<Pair<J, Ap2<D, C, J>>, Ap<P, C>> = right(Left(it))
    fun <C, J> right(it: Ap2<D, C, J>, c: C): Either<Pair<J, Ap2<D, C, J>>, Ap<P, C>> = right(Right(Pair(it, c)))

    fun <C, J> right(it: Either<Ap<P, J>, Pair<Ap2<D, C, J>, C>>): Either<Pair<J, Ap2<D, C, J>>, Ap<P, C>> =
        when (it) {
            is Left -> right(it.left)
            is Right -> right(it.right.first, it.right.second)
        }

    fun <C, J> left(it: Ap<P, C>): Either<Ap<P, J>, Pair<Ap2<D, C, J>, C>> = left(Right(it))
    fun <C, J> left(j: J, it: Ap2<D, C, J>): Either<Ap<P, J>, Pair<Ap2<D, C, J>, C>> = left(Left(Pair(j, it)))

    fun <C, J> left(it: Either<Pair<J, Ap2<D, C, J>>, Ap<P, C>>): Either<Ap<P, J>, Pair<Ap2<D, C, J>, C>> =
        when (it) {
            is Left -> left(it.left.first, it.left.second)
            is Right -> left(it.right)
        }
}

fun <A> K1.Companion.dissection() = object : Dissection<Ap<K1Kind, A>, Zero2Kind> {
    override fun <C, J> right(it: Ap2<K1Kind, A, J>): Either<Pair<J, Ap2<Zero2Kind, C, J>>, Ap2<K1Kind, A, C>> =
        Right(it.coerce())

    override fun <C, J> right(it: Zero2<C, J>, c: C): Either<Pair<J, Ap2<Zero2Kind, C, J>>, Ap2<K1Kind, A, C>> =
        it.refute()

    override fun <C, J> left(it: Ap2<K1Kind, A, C>): Either<Ap2<K1Kind, A, J>, Pair<Ap2<Zero2Kind, C, J>, C>> =
        Left(it.coerce())

    override fun <C, J> left(j: J, it: Zero2<C, J>): Either<Ap2<K1Kind, A, J>, Pair<Ap2<Zero2Kind, C, J>, C>> =
        it.refute()
}

fun Id.Companion.dissection() = object : Dissection<IdKind, One2Kind> {
    override fun <C, J> right(it: Ap<IdKind, J>): Either<Pair<J, Ap2<One2Kind, C, J>>, Ap<IdKind, C>> {
        val (j) = Id.prj(it)
        return Left(Pair(j, one2()))
    }

    override fun <C, J> right(it: Ap2<One2Kind, C, J>, c: C): Either<Pair<J, Ap2<One2Kind, C, J>>, Ap<IdKind, C>> =
        Right(id(c))

    override fun <C, J> left(it: Ap<IdKind, C>): Either<Ap<IdKind, J>, Pair<Ap2<One2Kind, C, J>, C>> {
        val (c) = Id.prj(it)
        return Right(Pair(one2(), c))
    }

    override fun <C, J> left(j: J, it: Ap2<One2Kind, C, J>): Either<Ap<IdKind, J>, Pair<Ap2<One2Kind, C, J>, C>> =
        Left(id(j))
}

fun <P, Q, DP, DQ> Sum1.Companion.dissection(dp: Dissection<P, DP>, dq: Dissection<Q, DQ>) =
    object : Dissection<Ap2<Sum1Kind, P, Q>, Ap2<Sum2Kind, DP, DQ>> {
        override fun <C, J> right(
            it: Ap3<Sum1Kind, P, Q, J>
        ): Either<Pair<J, Ap4<Sum2Kind, DP, DQ, C, J>>, Ap3<Sum1Kind, P, Q, C>> =
            when (val sum = Sum1.prj(it)) {
                is L1 -> mindPR(dp.right(sum.left))
                is R1 -> mindQR(dq.right(sum.right))
            }

        override fun <C, J> right(
            it: Ap4<Sum2Kind, DP, DQ, C, J>,
            c: C
        ): Either<Pair<J, Ap4<Sum2Kind, DP, DQ, C, J>>, Ap3<Sum1Kind, P, Q, C>> =
            when (val sum = Sum2.prj(it)) {
                is L2 -> mindPR(dp.right(sum.left, c))
                is R2 -> mindQR(dq.right(sum.right, c))
            }

        override fun <C, J> left(
            it: Ap3<Sum1Kind, P, Q, C>
        ): Either<Ap3<Sum1Kind, P, Q, J>, Pair<Ap4<Sum2Kind, DP, DQ, C, J>, C>> =
            when (val sum = Sum1.prj(it)) {
                is L1 -> mindPL(dp.left(sum.left))
                is R1 -> mindQL(dq.left(sum.right))
            }

        override fun <C, J> left(
            j: J,
            it: Ap4<Sum2Kind, DP, DQ, C, J>
        ): Either<Ap3<Sum1Kind, P, Q, J>, Pair<Ap4<Sum2Kind, DP, DQ, C, J>, C>> =
            when (val sum = Sum2.prj(it)) {
                is L2 -> mindPL(dp.left(j, sum.left))
                is R2 -> mindQL(dq.left(j, sum.right))
            }

        private fun <C, J> mindPR(
            pr: Either<Pair<J, Ap2<DP, C, J>>, Ap<P, C>>
        ): Either<Pair<J, Ap4<Sum2Kind, DP, DQ, C, J>>, Ap3<Sum1Kind, P, Q, C>> =
            when (pr) {
                is Left -> Left(Pair(pr.left.first, l2(pr.left.second)))
                is Right -> Right(l1(pr.right))
            }

        private fun <C, J> mindQR(
            qr: Either<Pair<J, Ap2<DQ, C, J>>, Ap<Q, C>>
        ): Either<Pair<J, Ap4<Sum2Kind, DP, DQ, C, J>>, Ap3<Sum1Kind, P, Q, C>> =
            when (qr) {
                is Left -> Left(Pair(qr.left.first, r2(qr.left.second)))
                is Right -> Right(r1(qr.right))
            }

        private fun <C, J> mindPL(
            pl: Either<Ap<P, J>, Pair<Ap2<DP, C, J>, C>>
        ): Either<Ap3<Sum1Kind, P, Q, J>, Pair<Ap4<Sum2Kind, DP, DQ, C, J>, C>> =
            when (pl) {
                is Left -> Left(l1(pl.left))
                is Right -> Right(Pair(l2(pl.right.first), pl.right.second))
            }

        private fun <C, J> mindQL(
            ql: Either<Ap<Q, J>, Pair<Ap2<DQ, C, J>, C>>
        ): Either<Ap3<Sum1Kind, P, Q, J>, Pair<Ap4<Sum2Kind, DP, DQ, C, J>, C>> =
            when (ql) {
                is Left -> Left(r1(ql.left))
                is Right -> Right(Pair(r2(ql.right.first), ql.right.second))
            }
    }

typealias DProduct1Kind<P, Q, DP, DQ> =
        Ap2<Sum2Kind,
                Ap2<Product2Kind, DP, Ap<JokersKind, Q>>,
                Ap2<Product2Kind, Ap<ClownsKind, P>, DQ>>

fun <P, Q, DP, DQ> Product1.Companion.dissection(dp: Dissection<P, DP>, dq: Dissection<Q, DQ>) =
    object : Dissection<Ap2<Product1Kind, P, Q>, DProduct1Kind<P, Q, DP, DQ>> {
        override fun <C, J> right(
            it: Ap3<Product1Kind, P, Q, J>
        ): Either<Pair<J, Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>>, Ap3<Product1Kind, P, Q, C>> {
            val (pj, qj) = Product1.prj(it)
            val pr = dp.right<C, J>(pj)
            return mindPR(pr, qj)
        }

        override fun <C, J> right(
            it: Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>,
            c: C
        ): Either<Pair<J, Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>>, Ap3<Product1Kind, P, Q, C>> =
            when (val sum = Sum2.prj(it)) {
                is L2 -> {
                    val (pd, qj) = Product2.prj(sum.left)
                    mindPR(dp.right(pd, c), Jokers.prj(qj).it)
                }
                is R2 -> {
                    val (pc, qd) = Product2.prj(sum.right)
                    mindQR(Clowns.prj(pc).it, dq.right(qd, c))
                }
            }

        override fun <C, J> left(
            it: Ap3<Product1Kind, P, Q, C>
        ): Either<Ap3<Product1Kind, P, Q, J>, Pair<Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>, C>> {
            val (pc, qc) = Product1.prj(it)
            val qr = dq.left<C, J>(qc)
            return mindPL(pc, qr)
        }

        override fun <C, J> left(
            j: J,
            it: Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>
        ): Either<Ap3<Product1Kind, P, Q, J>, Pair<Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>, C>> =
            when (val sum = Sum2.prj(it)) {
                is L2 -> {
                    val (pd, qj) = Product2.prj(sum.left)
                    mindQL(dp.left(j, pd), Jokers.prj(qj).it)
                }
                is R2 -> {
                    val (pc, qd) = Product2.prj(sum.right)
                    mindPL(Clowns.prj(pc).it, dq.left(j, qd))
                }
            }

        private fun <J, C> mindPR(
            pr: Either<Pair<J, Ap2<DP, C, J>>, Ap<P, C>>,
            qj: Ap<Q, J>
        ): Either<Pair<J, Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>>, Ap3<Product1Kind, P, Q, C>> = when (pr) {
            is Left -> {
                val (j, pd) = pr.left
                Left(Pair(j, l2(product2(pd, jokers(qj)))))
            }
            is Right -> mindQR(pr.right, dq.right(qj))
        }

        private fun <J, C> mindQR(
            pc: Ap<P, C>,
            qr: Either<Pair<J, Ap2<DQ, C, J>>, Ap<Q, C>>
        ): Either<Pair<J, Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>>, Ap3<Product1Kind, P, Q, C>> = when (qr) {
            is Left -> {
                val (j, qd) = qr.left
                Left(Pair(j, r2(product2(clowns(pc), qd))))
            }
            is Right -> {
                val qc = qr.right
                Right(product1(pc, qc))
            }
        }

        private fun <J, C> mindPL(
            pc: Ap<P, C>,
            qr: Either<Ap<Q, J>, Pair<Ap2<DQ, C, J>, C>>
        ): Either<Ap3<Product1Kind, P, Q, J>, Pair<Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>, C>> =
            when (qr) {
                is Left -> mindQL(dp.left(pc), qr.left)
                is Right -> {
                    val (qd, c) = qr.right
                    Right(Pair(r2(product2(clowns(pc), qd)), c))
                }
            }

        private fun <J, C> mindQL(
            qr: Either<Ap<P, J>, Pair<Ap2<DP, C, J>, C>>,
            qj: Ap<Q, J>
        ): Either<Ap3<Product1Kind, P, Q, J>, Pair<Ap2<DProduct1Kind<P, Q, DP, DQ>, C, J>, C>> =
            when (qr) {
                is Left -> {
                    val pj = qr.left
                    Left(product1(pj, qj))
                }
                is Right -> {
                    val (pd, c) = qr.right
                    Right(Pair(l2(product2(pd, jokers(qj))), c))
                }
            }
    }

fun <P, D, X, Y> tmap(dissection: Dissection<P, D>, f: (X) -> Y): (Ap<P, X>) -> Ap<P, Y> {
    tailrec fun traverse(it: Either<Pair<X, Ap2<D, Y, X>>, Ap<P, Y>>): Ap<P, Y> =
        when (it) {
            is Left -> {
                val (x, pd) = it.left
                traverse(dissection.right(pd, f(x)))
            }
            is Right -> it.right
        }

    return { it: Ap<P, X> -> traverse(dissection.right(it)) }
}

class TailCatamorphism<P, D, V>(
    private val dissection: Dissection<P, D>,
    private val phi: (Ap<P, V>) -> V
): (FixT<P>) -> V {
    sealed class DissectionStack<P, D, V>
    class Nil<P, D, V> : DissectionStack<P, D, V>()
    data class Cons<P, D, V>(
        val head: Ap2<D, V, FixT<P>>,
        val tail: DissectionStack<P, D, V>
    ) : DissectionStack<P, D, V>()

    override operator fun invoke(it: FixT<P>): V = load(it, Nil())

    private fun load(
        it: FixT<P>,
        stk: DissectionStack<P, D, V>
    ): V = next(dissection.right(it.it), stk)

    private fun next(
        it: Either<Pair<FixT<P>, Ap2<D, V, FixT<P>>>, Ap<P, V>>,
        stk: DissectionStack<P, D, V>
    ): V = when (it) {
        is Left -> {
            val (t, pd) = it.left
            load(t, Cons(pd, stk))
        }
        is Right -> unload(phi(it.right), stk)
    }

    private fun unload(
        v: V,
        stk: DissectionStack<P, D, V>
    ): V = when (stk) {
        is Nil -> v
        is Cons -> next(dissection.right(stk.head, v), stk.tail)
    }
}
