package com.github.romassistant.model.db.jdbc;

import com.github.romassistant.model.Ruin;
import com.github.romassistant.model.db.RuinDao;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.sql.DataSource;


@Repository
@ConditionalOnExpression("${db.enabled:true} && ${db.impl:jdbc}")
public class RuinDaoJdbc implements RuinDao {
    private static final Log log = LogFactory.getLog(RuinDaoJdbc.class);

    @Autowired
    private DataSource dataSource;


    @Override
    public void create(@Nonnull Ruin r) {
        Objects.requireNonNull(r);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            var statement = connection.prepareStatement(
                    "INSERT INTO rom_assistant.ruin VALUES(?, ?, ?, ?);");
            statement.setInt(1, r.getLevel());
            statement.setString(2, r.getBosses());
            statement.setTimestamp(3, Timestamp.from(r.getTime()));
            statement.setString(4, r.getReporter().orElse(null));
            log.debug(statement);

            statement.executeUpdate();
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException e) {
            log.error(e);
            DbUtils.rollbackAndCloseQuietly(connection);
        }
    }

    @Override
    public Collection<Ruin> getAll(int numRows) {
        return get("SELECT * FROM rom_assistant.ruin;", numRows);
    }

    @Override
    public Collection<Ruin> get(int level, int numRows) {
        return get(String.format(
                "SELECT * FROM rom_assistant.ruin WHERE level=%d;", level), numRows);
    }

    private Collection<Ruin> get(String statement, int numRows) {
        List<Ruin> list = new ArrayList<>(numRows);

        try (Connection connection = dataSource.getConnection()) {
            var stmt = connection.prepareStatement(statement);
            log.debug(statement);
            var results = stmt.executeQuery();

            while (results.next()) {
                if (list.size() > numRows) {
                    break;
                }
                list.add(new Ruin(results.getInt(1), results.getString(2),
                                  results.getTimestamp(3).toInstant(), results.getString(4)));
            }
        } catch (SQLException e) {
            log.error(e);
        }

        return Collections.unmodifiableList(list);
    }
}
