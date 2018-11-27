package com.github.romassistant.model.db;

import com.github.romassistant.model.Ruin;

import java.util.Collection;


@SuppressWarnings("unused")
public interface RuinDao extends DataAccessObject<Ruin> {
    /**
     * @param numRows the maximum length of the returned list.
     *
     * @return an immutable collection of the query result in descending order
     *         (from the latest date).
     *
     * @throws IllegalArgumentException if invalid level is given.
     */
    default Collection<Ruin> get(int level, int numRows) {
        throw new UnsupportedOperationException();
    }
}
