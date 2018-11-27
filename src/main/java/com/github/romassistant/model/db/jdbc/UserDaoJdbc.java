package com.github.romassistant.model.db.jdbc;

import com.github.romassistant.model.User;
import com.github.romassistant.model.db.UserDao;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.sql.DataSource;


@Repository
@ConditionalOnExpression("${db.enabled:true} && ${db.impl:jdbc}")
public class UserDaoJdbc implements UserDao {
    private static final Log log = LogFactory.getLog(UserDaoJdbc.class);

    @Autowired
    private DataSource dataSource;


    @Override
    public void create(@Nonnull User u) {
        Objects.requireNonNull(u);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            var statement = connection.prepareStatement(
                    "INSERT INTO rom_assistant.user VALUES(?, ?, ?);");
            statement.setString(1, u.getId());
            statement.setString(2, u.getName());
            statement.setString(3, u.getNickname().orElse(null));
            log.debug(statement);

            statement.executeUpdate();
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException e) {
            log.error(e);
            DbUtils.rollbackAndCloseQuietly(connection);
        }
    }

    @Override
    public List<User> getAll(int numRows) {
        List<User> list = new ArrayList<>(numRows);

        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement("SELECT * FROM rom_assistant.user;");
            log.debug(statement);

            var results = statement.executeQuery();
            while (results.next()) {
                if (list.size() > numRows) {
                    break;
                }
                list.add(new User(results.getString(1), results.getString(2),
                                  results.getString(3)));
            }
        } catch (SQLException e) {
            log.error(e);
        }

        return Collections.unmodifiableList(list);
    }

    @Override
    public Optional<User> get(@Nonnull Object id) {
        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement(
                    "SELECT * FROM rom_assistant.user WHERE id=?;");
            statement.setString(1, id.toString());
            log.debug(statement);

            var results = statement.executeQuery();
            switch (results.getFetchSize()) {
                case 0:
                    return Optional.empty();
                case 1:
                    results.first();
                    return Optional.of(new User(results.getString(1), results.getString(2),
                                                results.getString(3)));
                default:
                    throw new IllegalArgumentException("The given id is not the primary key.");
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }
}
