package com.github.romassistant.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Ruin {
    private static final Set<Integer> validLevels = Set.of(40, 60, 80, 100);

    private int level;
    private String bosses;
    private Instant time;
    private String reporter;


    /**
     * Checks if the given level is valid.
     * @return the level if valid.
     * @throws IllegalArgumentException if level is invalid.
     */
    public static int validate(int level) {
        if (validLevels.contains(level)) {
            return level;
        } else {
            throw new IllegalArgumentException("Invalid level");
        }
    }


    /**
     * @param level the level of the ruin, which must be 40, 60, 80 or 100.
     * @param bosses the bosses information of the ruin.
     * @param time the time when the information is reported.
     * @param reporter the Line user id who reports the bosses information.
     *
     * @throws IllegalArgumentException if either level or reporter is invalid.
     * @throws NullPointerException if any arguments is null.
     */
    public Ruin(int level, @Nonnull String bosses, @Nonnull Instant time, @Nullable String reporter) {
        this.level = Ruin.validate(level);
        this.bosses = Objects.requireNonNull(bosses);
        this.time = Objects.requireNonNull(time);
        this.reporter = Objects.isNull(reporter) ? null : User.validate(reporter);
    }

    /**
     * @return the level of the ruin.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the bosses information of the ruin.
     */
    public String getBosses() {
        return bosses;
    }

    /**
     * @return the time when the information is reported.
     */
    public Instant getTime() {
        return time;
    }

    /**
     * @return the Line user id who reports the bosses information.
     */
    public Optional<String> getReporter() {
        return Optional.ofNullable(reporter);
    }

    @Override
    public boolean equals(Object o) {
        // Auto-generated code by Intellij IDEA
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Ruin ruin = (Ruin) o;
        return new EqualsBuilder()
                .append(level, ruin.level)
                .append(bosses, ruin.bosses)
                .append(time, ruin.time)
                .isEquals();
    }

    @Override
    public int hashCode() {
        // Auto-generated code by Intellij IDEA
        return new HashCodeBuilder(17, 37)
                .append(level)
                .append(bosses)
                .append(time)
                .toHashCode();
    }
}
