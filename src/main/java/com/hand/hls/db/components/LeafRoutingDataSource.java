package com.hand.hls.db.components;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2021/5/1
 * @version 2.0
 * @description:  多数据源
 */
public class LeafRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        LeafDataSourceManager.DataSource dataSource = LeafDataSourceManager.get();
        return dataSource;
    }


}
