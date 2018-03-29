/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.github.igla.ferriswheel

import android.util.Property

/**
 * An implementation of [android.util.Property] to be used specifically with fields of type
 * `float`. This type-specific subclass enables performance benefit by allowing
 * calls to a [setValue()][.setValue] function that takes the primitive
 * `float` type and avoids autoboxing and other overhead associated with the
 * `Float` class.
 *
 * @param <T> The class on which the Property is declared.
</T> */
internal abstract class FloatProperty<T> : Property<T, Float>(Float::class.java, "angle") {

    /**
     * A type-specific variant of [.set] that is faster when dealing
     * with fields of type `float`.
     */
    abstract fun setValue(obj: T, value: Float)

    override fun set(obj: T, value: Float) {
        setValue(obj, value)
    }

    override fun get(obj: T): Float = 0f
}