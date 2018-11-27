package com.github.romassistant.model.db;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;


@SuppressWarnings("unused")
interface DataAccessObject<T> {
    /**
     * Inserts an entry to the table if not exists.
     *
     * @throws NullPointerException if obj is null.
     */
    default void create(@Nonnull T obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets an entry from the table.
     *
     * @param primaryKey the primary key to be used in the WHERE clause for the sql statement.
     *
     * @throws UnsupportedOperationException if there is no primary key for the table.
     * @throws IllegalArgumentException if primaryKey is not the primary key.
     * @throws NullPointerException if primaryKey is null.
     */
    default Optional<T> get(@Nonnull Object primaryKey) {
        throw new UnsupportedOperationException();
    }

    /**
     * Selects the columns by using the given condition.
     *
     * @param condition the WHERE clause for the sql statement.
     * @param columns the columns to select.
     * @param numRows the maximum length of the returned list.
     *
     * @return an immutable collection of the query result. The key and value of the map are the
     *         column name and its corresponding value, respectively.
     *
     * @throws NullPointerException if any arguments is null.
     */
    default Collection<Map<String, Object>> get(@Nonnull String condition,
                                                @Nonnull Set<String> columns,
                                                int numRows) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves all entries from the table.
     *
     * @param numRows the maximum length of the returned list.
     *
     * @return an immutable collection of the query result.
     */
    default Collection<T> getAll(int numRows) {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates an entry in the table to obj.
     *
     * @param primaryKey the primary key to be used in the WHERE clause for the sql statement.
     *
     * @throws UnsupportedOperationException if there is no primary key for the table.
     * @throws IllegalStateException if no entry can be found by the given primary key.
     * @throws IllegalArgumentException if primaryKey is not the primary key.
     * @throws NullPointerException if any arguments is null.
     */
    default void update(@Nonnull Object primaryKey, @Nonnull T obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates the values of an entry in the table by using the given condition.
     *
     * @param condition the WHERE clause for the sql statement.
     * @param values the new values to update. The key of the map is the column name, and the value
     *               is the new value of the column.
     *
     * @throws IllegalStateException if no entry can be found by the given condition.
     * @throws NullPointerException if any arguments is null.
     */
    default void update(@Nonnull String condition, @Nonnull Map<String, Object> values) {
       throw new UnsupportedOperationException();
    }

    /**
     * Removes an entry from the table.
     *
     * @param primaryKey the primary key to be used in the WHERE clause for the sql statement.
     *
     * @throws UnsupportedOperationException if there is no primary key for the table.
     * @throws IllegalStateException if no entry can be found by the given primary key.
     * @throws IllegalArgumentException if primaryKey is not the primary key.
     * @throws NullPointerException if primaryKey is null.
     */
    default void delete(@Nonnull Object primaryKey) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes an entry from the table by using the given condition.
     *
     * @param condition the WHERE clause for the sql statement.
     *
     * @throws IllegalStateException if no entry can be found by the given condition.
     * @throws NullPointerException if any arguments is null.
     */
    default void delete(@Nonnull String condition) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes all entries in the table.
     */
    default void deleteAll() {
        throw new UnsupportedOperationException();
    }
}
