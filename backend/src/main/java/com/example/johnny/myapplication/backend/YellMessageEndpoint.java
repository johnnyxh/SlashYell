package com.example.johnny.myapplication.backend;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.Point;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "yellMessageApi",
        version = "v1",
        resource = "yellMessage",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.johnny.example.com",
                ownerName = "backend.myapplication.johnny.example.com",
                packagePath = ""
        )
)
public class YellMessageEndpoint {

    private static final Logger logger = Logger.getLogger(YellMessageEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(YellMessage.class);
    }

    /**
     * Returns the {@link YellMessage} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code YellMessage} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "yellMessage/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public YellMessage get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting YellMessage with ID: " + id);
        YellMessage yellMessage = ofy().load().type(YellMessage.class).id(id).now();
        if (yellMessage == null) {
            throw new NotFoundException("Could not find YellMessage with ID: " + id);
        }
        return yellMessage;
    }

    /**
     * Returns the replies of the {@link YellMessage} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code YellMessage} with the provided ID.
     */
    @ApiMethod(
            name = "getReplies",
            path = "yellMessageReplies/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<YellMessage> getReplies(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting replies to YellMessage with ID: " + id);
        Query<YellMessage> replies = ofy().load().type(YellMessage.class).filter("opId", id).order("date");
        QueryResultIterator<YellMessage> queryIterator = replies.iterator();
        List<YellMessage> yellMessageList = new ArrayList<YellMessage>();
        while (queryIterator.hasNext()) {
            yellMessageList.add(queryIterator.next());
        }
        return CollectionResponse.<YellMessage>builder().setItems(yellMessageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    /**
     * Returns the messages of the {@link YellMessage} around (5) the location given.
     *
     * @param latitude the center of the search area
     * @param longitude fuck
     * @return the messages which have below 10 units of distance from the center
     * @throws
     */
    @ApiMethod(
            name = "getAround",
            path = "yellMessage/getAround",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<YellMessage> getAround(@Named("latitude") float latitude, @Named("longitude") float longitude) throws NotFoundException {
        Query<YellMessage> messages = ofy().load().type(YellMessage.class);
        QueryResultIterator<YellMessage> queryIterator = messages.iterator();
        List<YellMessage> yellMessageList;
        Point center = new Point(latitude, longitude);
        ObjectifyGeocellQueryEngine om = new ObjectifyGeocellQueryEngine(ofy(), "cells", "date");

        List<Object> params = new ArrayList<Object>();
        params.add(null);
        GeocellQuery query = new GeocellQuery("opId == opIdParam", "Long opIdParam", params);
        yellMessageList = GeocellManager.proximitySearch(center, 40, 0.0, 1000.0, YellMessage.class, query, om, GeocellManager.MAX_GEOCELL_RESOLUTION).getResults();

        // Work around for bug in geocells library, remove duplicates and order by date (newest first)
        Set<YellMessage> noDuplicates= new LinkedHashSet<YellMessage>(yellMessageList);
        yellMessageList.clear();
        yellMessageList.addAll(noDuplicates);
        Collections.sort(yellMessageList);

        for (YellMessage t : yellMessageList) {
            logger.info("ID of the message" + t.getUserId());
        }

        return CollectionResponse.<YellMessage>builder().setItems(yellMessageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    /**
     * Inserts a new {@code YellMessage}.
     */
    @ApiMethod(
            name = "insert",
            path = "yellMessage",
            httpMethod = ApiMethod.HttpMethod.POST)
    public YellMessage insert(YellMessage yellMessage) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that yellMessage.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        Point p = new Point(yellMessage.getLocation().getLatitude(), yellMessage.getLocation().getLongitude());
        List<String> cells = GeocellManager.generateGeoCell(p);
        yellMessage.setCells(cells);
        yellMessage.setLatitude(yellMessage.getLocation().getLatitude());
        yellMessage.setLongitude(yellMessage.getLocation().getLongitude());
        yellMessage.setDate(new Date());
        ofy().save().entity(yellMessage).now();
        logger.info("Created YellMessage with ID: " + yellMessage.getId());

        return ofy().load().entity(yellMessage).now();
    }

    /**
     * Updates an existing {@code YellMessage}.
     *
     * @param id          the ID of the entity to be updated
     * @param yellMessage the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code YellMessage}
     */
    @ApiMethod(
            name = "update",
            path = "yellMessage/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public YellMessage update(@Named("id") Long id, YellMessage yellMessage) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(yellMessage).now();
        logger.info("Updated YellMessage: " + yellMessage);
        return ofy().load().entity(yellMessage).now();
    }

    /**
     * Deletes the specified {@code YellMessage}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code YellMessage}
     */
    @ApiMethod(
            name = "remove",
            path = "yellMessage/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(YellMessage.class).id(id).now();
        logger.info("Deleted YellMessage with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "yellMessage",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<YellMessage> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<YellMessage> query = ofy().load().type(YellMessage.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<YellMessage> queryIterator = query.iterator();
        List<YellMessage> yellMessageList = new ArrayList<YellMessage>(limit);
        while (queryIterator.hasNext()) {
            yellMessageList.add(queryIterator.next());
        }
        return CollectionResponse.<YellMessage>builder().setItems(yellMessageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(YellMessage.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find YellMessage with ID: " + id);
        }
    }
}