package net.gabor7d2.simpleinventory.db

interface CollectionListener<T> {

    fun onAdded(entity: T)

    fun onChanged(entity: T)

    fun onRemoved(entity: T)
}