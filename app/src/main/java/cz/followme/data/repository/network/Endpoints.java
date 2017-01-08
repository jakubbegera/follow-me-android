package cz.followme.data.repository.network;

import cz.followme.data.model.responces.CreateSessionResponse;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public interface Endpoints {

    @GET("create-session")
    Observable<CreateSessionResponse> createSession();
}
