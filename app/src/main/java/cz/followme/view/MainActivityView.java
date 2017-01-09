package cz.followme.view;

import android.support.annotation.StringRes;

/**
 * Created by Jakub Begera (jakub.begera@gmail.com) on 07.01.17.
 * (C) Copyright 2017
 */

public interface MainActivityView extends View {

    void showLoading(@StringRes int textResId);

    void onSessionCreated(String sessionId);

    void onSessionCreationFail();
}
