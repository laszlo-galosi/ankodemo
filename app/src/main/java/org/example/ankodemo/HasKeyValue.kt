package org.example.ankodemo

import android.util.SparseArray

/**
 * @author László Gálosi
 * @since 21/08/18
 */
interface HasKeyValue<K, V> {
    /**
     * Returns the array of selectable option id-s with type K.
     */
    fun keyMap(): SparseArray<K>

    /**
     * Returns the array of selectable option values with type V.
     */
    fun valueMap(): SparseArray<V>

    /**
     * Returns the option value by the given key.
     */
    fun valueByKey(key: K): V

    /**
     * Returns the option index  by the given key.
     */
    fun indexByKey(key: K): Int

    /**
     * Returns the option's index in the options array by the given value.
     */
    fun indexByValue(value: V): Int

    /**
     * Returns the id of the option by the given index in the options array.
     */
    fun keyAt(index: Int): K
}
