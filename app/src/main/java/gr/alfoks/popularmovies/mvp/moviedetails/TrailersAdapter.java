package gr.alfoks.popularmovies.mvp.moviedetails;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gr.alfoks.popularmovies.R;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public final class TrailersAdapter
    extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    @NonNull
    private final Context context;
    @NonNull
    private final MovieDetailsContract.Presenter presenter;

    TrailersAdapter(@NonNull Context context, @NonNull MovieDetailsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_trailer, parent, false);

        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        presenter.onBindTrailerView(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getTrailersCount();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
        implements MovieDetailsContract.TrailerView {
        @BindView(R.id.imgThumbnail)
        ImageView imgThumbnail;
        @BindView(R.id.btnPlay)
        ImageView btnPlay;

        private TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnPlay)
        void playTrailer() {
            presenter.onTrailerClicked(getLayoutPosition());
        }

        @Override
        public void setThumbnail(String thumbnailUrl) {
            Picasso.with(context)
                   .load(R.drawable.placeholder_trailer)
                   .placeholder(R.drawable.anim_loading)
                   .error(R.drawable.placeholder_trailer)
                   .fit()
                   .centerInside()
                   .into(imgThumbnail);
        }
    }
}
