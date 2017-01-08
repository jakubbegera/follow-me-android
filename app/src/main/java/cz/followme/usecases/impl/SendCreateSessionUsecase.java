package cz.followme.usecases.impl;

import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.repozitory.DataRepository;
import cz.followme.usecases.Usecase;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class SendCreateSessionUsecase implements Usecase<CreateSessionResponse> {

    private final DataRepository dataRepository;

    public SendCreateSessionUsecase(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Subscription execute(Observer<CreateSessionResponse> observer) {
        return dataRepository.createSessionObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
