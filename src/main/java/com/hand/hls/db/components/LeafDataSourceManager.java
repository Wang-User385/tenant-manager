package com.hand.hls.db.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2021/5/1
 * @version 2.0
 * @description:  添加多数据源
 */
public class LeafDataSourceManager {
    private static Logger logger = LoggerFactory.getLogger(LeafDataSourceManager.class);

    public enum DataSource {
        MASTER
    }

    private static final ThreadLocal<DataSource> threadLocalDataSource = ThreadLocal.withInitial(() -> DataSource.MASTER);

    public static DataSource get() {
        return threadLocalDataSource.get();
    }

    public static void set(DataSource source) {
        logger.debug("SET Current dataSource is {}", source.name());
        threadLocalDataSource.set(source);
    }

    public static void reset() {
        logger.debug("RESET Current dataSource is {}", DataSource.MASTER);
        threadLocalDataSource.set(DataSource.MASTER);
    }
}
