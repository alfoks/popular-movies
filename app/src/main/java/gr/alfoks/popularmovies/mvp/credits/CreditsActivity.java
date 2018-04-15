package gr.alfoks.popularmovies.mvp.credits;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gr.alfoks.popularmovies.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public final class CreditsActivity extends AppCompatActivity {
    @BindView(R.id.tlbMain)
    Toolbar tlbMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        ButterKnife.bind(this);
        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(tlbMain);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_movie);
        }
    }

    @OnClick(R.id.imgTmdbLogo)
    void openTheMovieDbSite() {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.msg_tmdb_link)));
        startActivity(browser);
    }
}
