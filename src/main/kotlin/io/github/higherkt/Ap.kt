package io.github.higherkt

interface Ap<T, X>
typealias Ap1<T, X> = Ap<T, X>
typealias Ap2<T, X, Y> = Ap<Ap1<T, X>, Y>
typealias Ap3<T, X, Y, Z> = Ap<Ap2<T, X, Y>, Z>
typealias Ap4<T, X, Y, Z, W> = Ap<Ap3<T, X, Y, Z>, W>
typealias Ap5<T, X, Y, Z, W, V> = Ap<Ap4<T, X, Y, Z, W>, V>
typealias Ap6<T, X, Y, Z, W, V, U> = Ap<Ap5<T, X, Y, Z, W, V>, U>
typealias Ap7<T, X, Y, Z, W, V, U, S> = Ap<Ap6<T, X, Y, Z, W, V, U>, S>
typealias Ap8<T, X, Y, Z, W, V, U, S, R> = Ap<Ap7<T, X, Y, Z, W, V, U, S>, R>
