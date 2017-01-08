package cz.followme.view;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public interface MainActivityView extends View {

    void showLoading();

    void onSessionCreated(String sessionId);

    void onSessionCreationFail();
}
