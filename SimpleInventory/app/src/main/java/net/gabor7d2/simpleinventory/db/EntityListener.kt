package net.gabor7d2.simpleinventory.db

interface EntityListener<T> {

        fun onChanged(entity: T)

        fun onRemoved(entity: T)
}