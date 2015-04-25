package com.example.johnny.myapplication.backend;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

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

}
