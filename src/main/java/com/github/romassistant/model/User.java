package com.github.romassistant.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class User {
    private String id;
    private String name;
    private String nickname;


    /**
     * Checks if the given id is a valid Line user id.
     *
     * @return id if valid.
     * @throws IllegalArgumentException if id is invalid.
     */
    public static String validate(String id) {
        if (id.matches("U[0-9a-f]{32}")) {
            return id;
        } else {
            throw new IllegalArgumentException("Invalid user id.");
        }
    }


    /**
     * @param id the Line user id of length 33 that matches the regex "U[0-9a-f]{32}".
     * @param name the Line user display name. The maximum length is 100, and any exceeded
     *             characters are discarded.
     * @param nickname the user name in the game. The maximum length is 100, and any exceeded
     *                 characters are discarded.
     *
     * @throws NullPointerException if either id or name is null.
     * @throws IllegalArgumentException if id does not match the regex depicted above.
     */
    public User(@Nonnull String id, @Nonnull String name, @Nullable String nickname) {
        this.id = User.validate(Objects.requireNonNull(id));
        this.name = StringUtils.truncate(Objects.requireNonNull(name), 100);
        this.nickname = StringUtils.truncate(nickname, 100);
    }

    /**
     * @return the Line user id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the Line user display name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the user name in the game.
     */
    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname);
    }
}
