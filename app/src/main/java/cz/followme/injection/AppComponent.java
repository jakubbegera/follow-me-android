package cz.followme.injection;

import javax.inject.Singleton;

import cz.followme.presenters.MainActivityPresenter;
import cz.followme.services.LocationService;
import cz.followme.view.activities.MainActivity;
import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        NetworkModule.class
})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(LocationService locationService);

    void inject(MainActivityPresenter mainActivityPresenter);
}
