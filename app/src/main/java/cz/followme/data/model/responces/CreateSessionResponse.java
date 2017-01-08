package cz.followme.data.model.responces;

import com.google.gson.annotations.Expose;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class CreateSessionResponse {

    @Expose
    private String sessionID;

    public CreateSessionResponse(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}
