package cz.followme.injection;

import android.app.Application;

import javax.inject.Singleton;

import cz.followme.data.repozitory.DataRepository;
import cz.followme.presenters.MainActivityPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }


}
