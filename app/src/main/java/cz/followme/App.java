package cz.followme;

import android.app.Application;

import cz.followme.injection.AppComponent;
import cz.followme.injection.AppModule;
import cz.followme.injection.DaggerAppComponent;
import cz.followme.injection.NetworkModule;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 07.01.17.
 */
public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initLoggers();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule("http://followmecz.herokuapp.com/api/v1/"))
                .build();
    }

    private void initLoggers() {
        Timber.plant(new Timber.DebugTree());
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
