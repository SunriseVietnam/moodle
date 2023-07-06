package com.moodle.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

// Persistance Manager Factory
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");//"transactions-optional"

    private PMF() {
    }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}

