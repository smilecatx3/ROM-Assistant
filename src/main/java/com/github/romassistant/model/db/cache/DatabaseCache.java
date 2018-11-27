package com.github.romassistant.model.db.cache;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;


@SuppressWarnings("unused")
interface DatabaseCache<T> {
    enum Policy {
        WRITE_THROUGH,
        WRITE_BACK
    }

    /**
     * Initialize the cache content.
     */
    void initialize();

    /**
     * Gets an element in the cache.
     *
     * @param key the key to specify which element to read.
     *
     * @throws UnsupportedOperationException if there is no unique key for the cache.
     * @throws NullPointerException if key is null.
     */
    default Optional<T> read(@Nonnull Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets all the elements in the cache. The returned collection is immutable.
     */
    default Collection<T> readAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds an element to the cache as well as inserts to the corresponding table in the database.
     * The previous value will be overriden if obj already exists in the cache.
     *
     * @throws NullPointerException if key is null.
     */
    default void add(@Nonnull T obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes an element from the cache and the corresponding table in the database.
     *
     * @param key the key to specify which element to delete.
     *
     * @throws UnsupportedOperationException if there is no unique key for the cache.
     * @throws java.util.NoSuchElementException if there is no such element by the given key.
     * @throws NullPointerException if key is null;
     */
    default void delete(@Nonnull Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Flushes the cache content to the database.
     *
     * @throws UnsupportedOperationException if the cache writing policy is WRITE_THROUGH.
     */
    default void flush() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all elements from the cache.
     */
    default void invalidate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the writing policy of the cacahe.
     */
    Policy getPolicy();
}
