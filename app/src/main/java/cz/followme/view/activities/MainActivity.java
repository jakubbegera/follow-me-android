package cz.followme.view.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.followme.App;
import cz.followme.BuildConfig;
import cz.followme.R;
import cz.followme.presenters.MainActivityPresenter;
import cz.followme.services.LocationService;
import cz.followme.view.MainActivityView;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    private static final int MY_PERMISSIONS_REQUEST_LOC_ON_START = 1001;
    private static final int MY_PERMISSIONS_REQUEST_LOC_ON_CREATE_SESSION = 1002;

    protected MainActivityPresenter presenter;

    @BindView(R.id.btn_start)
    protected Button btnStart;
    @BindView(R.id.map)
    protected MapView mapView;
    @BindView(R.id.session_container)
    protected LinearLayout sessionContainer;
    @BindView(R.id.btn_stop)
    protected Button btnStop;
    @BindView(R.id.txv_session_link)
    protected TextView txvSessionLink;
    @BindView(R.id.btn_share)
    protected ImageButton btnShare;
    @BindView(R.id.btn_copy)
    protected ImageButton btnCopy;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private ProgressDialog progressDialog;

    private boolean isLocationServiceRunning;
    private long lastMapDragTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getApplication()).getAppComponent().inject(this);
        presenter = new MainActivityPresenter((App) getApplication());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter.onEnterScope(savedInstanceState);
        presenter.onAttach(this);

        btnShare.getDrawable().setColorFilter(getResources().getColor(R.color.text_grey), PorterDuff.Mode.SRC_ATOP);
        btnCopy.getDrawable().setColorFilter(getResources().getColor(R.color.text_grey), PorterDuff.Mode.SRC_ATOP);


        isLocationServiceRunning = isMyServiceRunning(LocationService.class);
        if (isLocationServiceRunning && presenter.getSessionID() == null) {
            presenter.restoreLastSavedSessionID();
            updateSessionContainer();
        }
        setSessionContainerVisible(isLocationServiceRunning);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationPermissionGranted()) {
                    presenter.sendCreateSessionRequest();
                } else {
                    requestMapsApiPermissions(false);
                }

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocationServiceRunning = false;
                stopService(new Intent(MainActivity.this, LocationService.class));
                setSessionContainerVisible(false);
            }
        });
        txvSessionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(presenter.createSessionUrl()));
                startActivity(i);
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("followMeSession", presenter.createSessionUrl());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.link_copied, Toast.LENGTH_LONG).show();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, presenter.createSessionUrl());
                startActivity(Intent.createChooser(shareIntent, "Share FollowMe session using"));
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                lastMapDragTimestamp = System.currentTimeMillis();
                return false;
            }
        });

        if (isLocationPermissionGranted()) {
            showMyLocationOnMap();
        } else {
            requestMapsApiPermissions(true);
        }
    }

    private void showMyLocationOnMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.setBuildingsEnabled(true);


                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (System.currentTimeMillis() - lastMapDragTimestamp > 5000) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                        }
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
                };

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 30, locationListener);
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
                }

            }
        });

    }

    private void setSessionContainerVisible(boolean visible) {
        if (visible) {
            sessionContainer.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
        } else {
            sessionContainer.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        }
    }

    private void updateSessionContainer() {
        txvSessionLink.setText(presenter.createSessionUrl());
    }

    private boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMapsApiPermissions(boolean isOnStart) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                isOnStart ? MY_PERMISSIONS_REQUEST_LOC_ON_START : MY_PERMISSIONS_REQUEST_LOC_ON_CREATE_SESSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOC_ON_START
                || requestCode == MY_PERMISSIONS_REQUEST_LOC_ON_CREATE_SESSION) {
            boolean isPermissionGranted = false;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                    break;
                }
            }

            if (isPermissionGranted) {
                showMyLocationOnMap();
                if (requestCode == MY_PERMISSIONS_REQUEST_LOC_ON_CREATE_SESSION) {
                    presenter.sendCreateSessionRequest();
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.onExitScope(outState);
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach(this);
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void showLoading(@StringRes int textResId) {
        progressDialog = ProgressDialog.show(this, "Loading", getString(textResId), true, false);
        progressDialog.show();

    }

    @Override
    public void onSessionCreated(String sessionId) {
        isLocationServiceRunning = true;
        startService(new Intent(MainActivity.this, LocationService.class)
                .putExtra(LocationService.EXTRA_SESSION_ID, sessionId));
        setSessionContainerVisible(true);
        updateSessionContainer();
        progressDialog.dismiss();
    }

    @Override
    public void onSessionCreationFail() {
        progressDialog.dismiss();
        Toast.makeText(this, R.string.session_creation_fail, Toast.LENGTH_LONG).show();
    }
}
