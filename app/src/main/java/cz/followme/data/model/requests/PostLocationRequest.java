package cz.followme.data.model.requests;

import com.google.gson.annotations.Expose;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class PostLocationRequest {

    @Expose
    private String sessionId;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private double altitude;

    public String getSessionId() {
        return sessionId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

}
