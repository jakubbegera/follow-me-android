package cz.followme.data.repository.network;

import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.repository.DataRepository;
import rx.Observable;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class NetworkDataRepository implements DataRepository {

    protected Endpoints endpoints;

    public NetworkDataRepository(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public Observable<CreateSessionResponse> createSessionObservable() {
        return endpoints.createSession();
    }
}
