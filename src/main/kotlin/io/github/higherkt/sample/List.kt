package io.github.higherkt.sample

import io.github.higherkt.Ap
import io.github.higherkt.annotations.Higher
import io.github.higherkt.typeclasses.Functor

@Higher
sealed class List<T> { companion object }
class Nil<T> : List<T>()
data class Cons<T>(val head: T, val tail: List<T>) : List<T>()

fun List.Companion.functor() = object : Functor<ListKind> {
    override fun <X, Y> fmap(f: (X) -> Y): (Ap<ListKind, X>) -> Ap<ListKind, Y> =
        List.lift { list: List<X> ->
            when (list) {
                is Nil -> Nil()
                is Cons -> Cons(f(list.head), List.unlift(fmap(f))(list.tail))
            }
        }
}

fun <X, Y> List.Companion.fmap(f: (X) -> Y): (List<X>) -> List<Y> = List.unlift(List.functor().fmap(f))
fun <X, Y> List<X>.map(f: (X) -> Y): List<Y> = List.fmap(f)(this)

fun main() {
    val list = Cons(1, Cons(2, Cons(3, Nil())))
    println(list.map { it * 2 })
}
