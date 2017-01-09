package cz.followme.presenters;

import android.os.Bundle;

import javax.inject.Inject;

import cz.followme.App;
import cz.followme.BuildConfig;
import cz.followme.R;
import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.repository.network.NetworkDataRepository;
import cz.followme.usecases.impl.SendCreateSessionUsecase;
import cz.followme.utils.Preferences;
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

    private final String SAVED_SESSION_ID = "saved_session_id";

    @Inject
    protected NetworkDataRepository dataRepository;
    @Inject
    protected Preferences preferences;

    private MainActivityView view;
    private CompositeSubscription compositeSubscription;

    private String sessionID;


    public MainActivityPresenter(App app) {
        compositeSubscription = new CompositeSubscription();
        app.getAppComponent().inject(this);
    }

    @Override
    public void onEnterScope(Bundle savedState) {
        if (savedState == null) return;

        if (savedState.containsKey(SAVED_SESSION_ID)) {
            sessionID = savedState.getString(SAVED_SESSION_ID);
        }
    }

    @Override
    public void onAttach(MainActivityView view) {
        this.view = view;
    }

    @Override
    public void onDetach(MainActivityView view) {
        compositeSubscription.clear();
    }

    @Override
    public void onExitScope(Bundle outState) {
        outState.putString(SAVED_SESSION_ID, sessionID);
    }

    public void sendCreateSessionRequest() {
        view.showLoading(R.string.crating_session_waiting_dialog);
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
                        sessionID = createSessionResponse.getSessionID();
                        view.onSessionCreated(sessionID);
                        preferences.saveSessionID(sessionID);
                    }
                });
        compositeSubscription.add(subscription);
    }

    public String getLastSavedSessionID() {
        return preferences.getSessionID();
    }

    public void restoreLastSavedSessionID() {
        sessionID = getLastSavedSessionID();
    }

    public String createSessionUrl() {
        return String.format("%s/session/%s", BuildConfig.SERVER_ENVIRONMENT, sessionID);
    }

    public String getSessionID() {
        return sessionID;
    }
}
