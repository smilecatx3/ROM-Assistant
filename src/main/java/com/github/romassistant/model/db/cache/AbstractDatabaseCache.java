package com.github.romassistant.model.db.cache;

import javax.annotation.PostConstruct;


abstract class AbstractDatabaseCache<T> implements DatabaseCache<T> {
    Policy policy;


    @PostConstruct
    private void init() {
        policy = getPolicy();
        initialize();
    }
}
