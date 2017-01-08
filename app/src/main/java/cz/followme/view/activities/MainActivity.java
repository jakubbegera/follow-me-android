package cz.followme.view.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.followme.App;
import cz.followme.R;
import cz.followme.data.repozitory.DataRepository;
import cz.followme.data.repozitory.network.NetworkDataRepository;
import cz.followme.injection.NetworkModule;
import cz.followme.presenters.MainActivityPresenter;
import cz.followme.services.LocationService;
import cz.followme.view.MainActivityView;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    protected MainActivityPresenter presenter;

    @BindView(R.id.btn_start_stop)
    protected Button btnStartStop;

    private boolean isLocationServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getApplication()).getAppComponent().inject(this);
        presenter = new MainActivityPresenter((App) getApplication());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter.onExitScope(savedInstanceState);
        presenter.onAttach(this);

        if (isMyServiceRunning(LocationService.class)) {
            isLocationServiceRunning = true;
            btnStartStop.setText("Stop");
        } else {
            isLocationServiceRunning = false;
            btnStartStop.setText("Start");
        }

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationServiceRunning) {
                    isLocationServiceRunning = false;
                    btnStartStop.setText("Start");
                    stopService(new Intent(MainActivity.this, LocationService.class));
                } else {
                    presenter.sendCreateSessionRequest();
//                    isLocationServiceRunning = true;
//                    btnStartStop.setText("Stop");
//                    startService(new Intent(MainActivity.this, LocationService.class));
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.onExitScope(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach(this);
        super.onDestroy();
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
    public void showLoading() {

    }

    @Override
    public void onSessionCreated(String sessionId) {

    }

    @Override
    public void onSessionCreationFail() {

    }
}
