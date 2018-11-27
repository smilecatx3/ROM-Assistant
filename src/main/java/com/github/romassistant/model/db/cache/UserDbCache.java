package com.github.romassistant.model.db.cache;

import com.github.romassistant.model.User;
import com.github.romassistant.model.db.UserDao;
import com.linecorp.bot.client.LineMessagingClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;


@Repository
@ConditionalOnExpression("${db.enabled:true}")
public class UserDbCache extends AbstractDatabaseCache<User> {
    private static final Log log = LogFactory.getLog(UserDbCache.class);

    @Autowired
    private LineMessagingClient client;

    @Autowired
    private UserDao userDao;
    private Map<String, User> userMap = new HashMap<>();


    @Override
    public void initialize() {
        for (var user : userDao.getAll(1000)) {
            userMap.put(user.getId(), user);
        }
    }

    @Override
    public Optional<User> read(@Nonnull Object key) {
        return Optional.ofNullable(userMap.get(Objects.requireNonNull(key).toString()));
    }

    @Override
    public Collection<User> readAll() {
        return Collections.unmodifiableCollection(userMap.values());
    }

    @Override
    public void add(@Nonnull User u) {
        Objects.requireNonNull(u);
        userMap.put(u.getId(), u);
    }

    @Override
    public void delete(@Nonnull Object key) {
        Objects.requireNonNull(key);
        if (!userMap.containsKey(key.toString())) {
            throw new NoSuchElementException();
        }
        userMap.remove(key.toString());
    }

    @Override
    public void invalidate() {
        userMap.clear();
    }

    @Override
    public Policy getPolicy() {
        return Policy.WRITE_THROUGH;
    }

    public void update(@Nonnull String userId) {
        Objects.requireNonNull(userId);
        if (!userMap.containsKey(userId)) {
            client.getProfile(userId).whenComplete((profile, throwable) -> {
                if (throwable != null) {
                    log.error(throwable);
                } else {
                    var user = new User(userId, profile.getDisplayName(), null);
                    userDao.create(user);
                    userMap.put(userId, user);
                }
            });
        }
    }
}
