package cz.followme.presenters;

import android.os.Bundle;

import javax.inject.Inject;

import cz.followme.App;
import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.repozitory.DataRepository;
import cz.followme.data.repozitory.network.NetworkDataRepository;
import cz.followme.usecases.impl.SendCreateSessionUsecase;
import cz.followme.view.MainActivityView;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public class MainActivityPresenter implements Presenter<MainActivityView> {

    @Inject
    protected NetworkDataRepository dataRepository;

    private MainActivityView view;
    private CompositeSubscription compositeSubscription;


    public MainActivityPresenter(App app) {
        compositeSubscription = new CompositeSubscription();
        app.getAppComponent().inject(this);
    }

    @Override
    public void onEnterScope(Bundle savedState) {

    }

    @Override
    public void onAttach(MainActivityView view) {
        this.view = view;

    }

    @Override
    public void onDetach(MainActivityView view) {
        view = null;
        compositeSubscription.clear();
    }

    @Override
    public void onExitScope(Bundle outState) {

    }

    public void sendCreateSessionRequest() {
        view.showLoading();
        Subscription subscription = new SendCreateSessionUsecase(dataRepository)
                .execute(new Observer<CreateSessionResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        view.onSessionCreationFail();
                    }

                    @Override
                    public void onNext(CreateSessionResponse createSessionResponse) {
                        Timber.i("New session created. Id = %s", createSessionResponse.getSessionID());
                        view.onSessionCreated(createSessionResponse.getSessionID());
                    }
                });
        compositeSubscription.add(subscription);
    }
}
