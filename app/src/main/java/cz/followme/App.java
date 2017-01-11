package cz.followme;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;

import cz.followme.injection.AppComponent;
import cz.followme.injection.AppModule;
import cz.followme.injection.DaggerAppComponent;
import cz.followme.injection.NetworkModule;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static cz.followme.Settings.DISABLE_CRASHLYTICS_FOR_DEBUG_BUILDS;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 07.01.17.
 */
public class App extends MultiDexApplication {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(
                this,
                new Crashlytics.Builder().core(
                        new CrashlyticsCore.Builder()
                                .disabled(BuildConfig.DEBUG && DISABLE_CRASHLYTICS_FOR_DEBUG_BUILDS)
                                .build()
                ).build(),
                new Answers()
        );
        initLoggers();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule(BuildConfig.SERVER_ENVIRONMENT + "/api/v1/"))
                .build();
    }

    private void initLoggers() {
        Timber.plant(new Timber.DebugTree());
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
