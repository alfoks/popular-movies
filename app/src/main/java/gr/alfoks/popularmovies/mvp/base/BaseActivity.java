package gr.alfoks.popularmovies.mvp.base;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {
    @BindView(R.id.txtNoConnection)
    TextView txtConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());

        ButterKnife.bind(this);

        registerConnectivityReceiver();
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

    /**
     * Get layout resource to be inflated
     */
    @LayoutRes
    protected abstract int getContentResource();
    protected abstract void init(@Nullable Bundle state);

    private final BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int connectionType = getConnectionType(context);
            boolean connectionOn = connectionType == ConnectivityManager.TYPE_WIFI
                || connectionType == ConnectivityManager.TYPE_MOBILE;

            onConnectivityChangedPrivate(connectionOn);
        }
    };

    private int getConnectionType(Context context) {
        NetworkInfo activeNetwork = null;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return activeNetwork.getType();
        }
        return -1;
    }

    private void onConnectivityChangedPrivate(boolean connectionOn) {
        if(txtConnection != null) {
            txtConnection.setVisibility(connectionOn ? View.GONE : View.VISIBLE);
        }
        onConnectivityChanged(connectionOn);
    }

    protected void onConnectivityChanged(boolean connectionOn) {

    }
}
