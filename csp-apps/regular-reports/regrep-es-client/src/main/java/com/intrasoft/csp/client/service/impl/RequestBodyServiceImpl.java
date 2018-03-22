package com.intrasoft.csp.client.service.impl;

import com.intrasoft.csp.client.DateMath;
import com.intrasoft.csp.client.service.RequestBodyService;
import com.intrasoft.csp.regrep.commons.model.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestBodyServiceImpl implements RequestBodyService{

    @Override
    public ElasticQuery constructQuery(DateMath gte, DateMath lt) {

        // Use the timestamp setters for the query time boundaries.
        ElasticQuery elasticQuery = new ElasticQuery();
        Query query = new Query();
        Bool bool = new Bool();
        List<MustItem> mustItemList = new ArrayList<>();
        MustItem mustItem = new MustItem();
        Range range = new Range();
        Timestamp timestamp = new Timestamp();

        // Time boundaries
        timestamp.setGte("now-" + gte.toString());
        timestamp.setLt(lt.toString());

        elasticQuery.setQuery(query);
        query.setBool(bool);
        mustItem.setRange(range);
        range.setTimestamp(timestamp);
        mustItemList.add(mustItem);
        bool.setMust(mustItemList);
        return elasticQuery;
    }
}
