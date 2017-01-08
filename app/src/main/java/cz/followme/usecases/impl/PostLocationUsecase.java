package cz.followme.usecases.impl;

import cz.followme.data.model.requests.PostLocationRequest;
import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.model.responces.EmptyResponse;
import cz.followme.data.repository.DataRepository;
import cz.followme.usecases.Usecase;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class PostLocationUsecase implements Usecase<EmptyResponse> {

    private final DataRepository dataRepository;
    private final PostLocationRequest requestData;

    public PostLocationUsecase(DataRepository dataRepository, PostLocationRequest requestData) {
        this.dataRepository = dataRepository;
        this.requestData = requestData;
    }

    @Override
    public Subscription execute(Observer<EmptyResponse> observer) {
        return dataRepository.createPostLocationObservable(requestData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
