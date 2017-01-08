package cz.followme.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import javax.inject.Inject;

import cz.followme.App;
import cz.followme.data.model.requests.PostLocationRequest;
import cz.followme.data.model.responces.EmptyResponse;
import cz.followme.data.repository.network.NetworkDataRepository;
import cz.followme.usecases.impl.PostLocationUsecase;
import rx.Observer;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 07.01.17.
 */
public class LocationService extends Service implements LocationListener {

    public final static String EXTRA_SESSION_ID = "extra_session_id";

    public LocationManager locationManager;

    @Inject
    protected NetworkDataRepository dataRepository;
    private String sessionID;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((App) getApplication()).getAppComponent().inject(this);

        sessionID = intent.getStringExtra(EXTRA_SESSION_ID);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 30, this);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.i("New location - %s", location.toString());
        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();

        final PostLocationRequest request = new PostLocationRequest(
                sessionID,
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude()
        );

        Subscription subscription = new PostLocationUsecase(dataRepository, request)
                .execute(new Observer<EmptyResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Posting location failed. Loc - %s.", request.toString());
                    }

                    @Override
                    public void onNext(EmptyResponse emptyResponse) {
                        Timber.i("Location posted. Loc - %s.", request.toString());
                    }
                });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
