package net.gabor7d2.simpleinventory.persistence

interface CollectionListener<T> {

    fun onAdded(entity: T)

    fun onChanged(entity: T)

    fun onRemoved(entity: T)
}