package com.example.johnny.myapplication.backend;

import com.beoui.geocell.model.BoundingBox;
import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.List;

/**
 * Created by johnny on 4/13/15.
 */

@Entity
public class YellMessage {

    @Id
    Long id;

    String userId;
    String message;
    GeoPt location;
    Date date;
    @Index
    Long opId;
    // A list of geohashed cells for better GeoQueries
    @Index
    List<String> cells;

    public YellMessage () {}

    public String getUserId() { return this.userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GeoPt getLocation() {
        return this.location;
    }

    public void setLocation(GeoPt location) {
        this.location = location;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() { return this.date; }

    public void setDate(Date date) { this.date = date; }

    public Long getOpId() { return opId; }

    public void setOp(Long opId) { this.opId = opId; }

    public List<String> getCells() { return cells; }

    public void setCells(List<String> cells) { this.cells = cells; }

    public boolean isIn(BoundingBox bb) {
        return getLocation().getLatitude() < bb.getNorth()
                && getLocation().getLatitude() > bb.getSouth()
                && getLocation().getLongitude() > bb.getWest()
                && getLocation().getLongitude() < bb.getEast();
    }

}
