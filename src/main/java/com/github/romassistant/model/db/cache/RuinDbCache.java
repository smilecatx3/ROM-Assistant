package com.github.romassistant.model.db.cache;

import com.github.romassistant.model.Ruin;
import com.github.romassistant.model.db.RuinDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;


@Repository
@ConditionalOnExpression("${db.enabled:true}")
public class RuinDbCache extends AbstractDatabaseCache<Ruin> {
    private static final Log log = LogFactory.getLog(RuinDbCache.class);

    @Autowired
    private RuinDao ruinDao;
    private Set<Ruin> ruins = new HashSet<>();


    @Override
    public void initialize() {
        ruins.addAll(ruinDao.getAll(1000));
    }

    @Override
    public Collection<Ruin> readAll() {
        return Collections.unmodifiableCollection(ruins);
    }

    @Override
    public void add(@Nonnull Ruin r) {
        ruins.add(Objects.requireNonNull(r));
        if (policy == Policy.WRITE_THROUGH) {
            ruinDao.create(r);
        }
    }

    @Override
    public void invalidate() {
        ruins.clear();
    }

    @Override
    public Policy getPolicy() {
        return Policy.WRITE_THROUGH;
    }
}
