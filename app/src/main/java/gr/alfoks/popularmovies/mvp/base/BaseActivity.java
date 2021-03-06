package gr.alfoks.popularmovies.mvp.base;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.util.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {
    @BindView(R.id.txtNoConnection)
    TextView txtConnection;

    private boolean connectionOn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());

        ButterKnife.bind(this);

        registerConnectivityReceiver();
        connectionOn = NetworkUtils.getInstance().isNetworkAvailable(this);
        showConnectivityIndicator(connectionOn);
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        unregisterConnectivityReceiver();
        super.onDestroy();
    }

    private void registerConnectivityReceiver() {
        registerReceiver(
            connectivityChangeReceiver,
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterConnectivityReceiver() {
        unregisterReceiver(connectivityChangeReceiver);
    }

    /** Get layout resource to be inflated */
    @LayoutRes
    protected abstract int getContentResource();
    protected abstract void init(@Nullable Bundle state);

    private final BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean newConnectionState = NetworkUtils.getInstance().isNetworkAvailable(BaseActivity.this);

            if(connectionOn != newConnectionState) {
                onConnectivityChangedPrivate(newConnectionState);
            }

            connectionOn = newConnectionState;
        }
    };

    private void onConnectivityChangedPrivate(boolean connectionOn) {
        showConnectivityIndicator(connectionOn);
        onConnectivityChanged(connectionOn);
    }

    private void showConnectivityIndicator(boolean connectionOn) {
        if(txtConnection != null) {
            txtConnection.setVisibility(connectionOn ? View.GONE : View.VISIBLE);
        }
    }

    protected abstract void onConnectivityChanged(boolean connectionOn);
}
