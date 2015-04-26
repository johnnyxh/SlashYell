package com.example.johnny.myapplication.backend;

import com.beoui.geocell.GeocellQueryEngine;
import com.beoui.geocell.model.GeocellQuery;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by johnny on 4/26/15.
 */
public class ObjectifyGeocellQueryEngine implements GeocellQueryEngine {
    private String geocellsProperty;
    private Objectify ofy;
    public static final String DEFAULT_GEOCELLS_PROPERTY = "geocells";

    public ObjectifyGeocellQueryEngine(Objectify ofy) {
        this(ofy, DEFAULT_GEOCELLS_PROPERTY);
    }

    public ObjectifyGeocellQueryEngine(Objectify ofy, String geocellsProperty) {
        this.ofy = ofy;
        this.geocellsProperty = geocellsProperty;
    }

    @Override
    public <T> List<T> query(GeocellQuery baseQuery, List<String> geocells, Class<T> entityClass) {
        StringTokenizer st;
        int tokenNo = 0;
        Query<T> query = ofy.load().type(entityClass);
        if (baseQuery != null) {
            st = new StringTokenizer(baseQuery.getBaseQuery(), ",");
            while (st.hasMoreTokens()) {
                query.filter(st.nextToken(), baseQuery.getParameters().get(tokenNo++));
            }
        }
        return query.filter(geocellsProperty + " IN", geocells).list();
    }
}
