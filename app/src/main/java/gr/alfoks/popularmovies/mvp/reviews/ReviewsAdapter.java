package gr.alfoks.popularmovies.mvp.reviews;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Review;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class ReviewsAdapter
    extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    @NonNull
    private final Context context;
    @NonNull
    private final ReviewsContract.ListPresenter presenter;

    public ReviewsAdapter(@NonNull Context context, @NonNull ReviewsContract.ListPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_review, parent, false);

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        presenter.onBindReviewView(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getReviewsCount();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder
        implements ReviewsContract.ListItemView {
        @BindView(R.id.txtAuthor)
        TextView txtAuthor;
        @BindView(R.id.txtContent)
        TextView txtContent;
        @BindView(R.id.txtMore)
        TextView txtMore;

        private int defaultHeight;
        private boolean expanded = true;
        private boolean expandable = true;

        private ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            defaultHeight = (int)context.getResources().getDimension(R.dimen.review_default_height);
            txtContent.getViewTreeObserver().addOnGlobalLayoutListener(createGlobalLayoutListener());
        }

        @NonNull
        private ViewTreeObserver.OnGlobalLayoutListener createGlobalLayoutListener() {
            return new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = txtContent.getMeasuredHeight();
                    if(height > defaultHeight) {
                        collapse();
                    } else {
                        expandable = false;
                        txtMore.setVisibility(View.GONE);
                    }
                    txtContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            };
        }

        @OnClick(R.id.pnlReview)
        void onReviewClicked() {
            if(!expandable) return;

            if(!expanded) {
                expand();
            } else {
                collapse();
            }
        }

        private void expand() {
            setHeight(WRAP_CONTENT);
        }

        private void collapse() {
            setHeight(defaultHeight);
        }

        private void setHeight(int height) {
            txtContent.getLayoutParams().height = height;
            txtContent.requestLayout();

            expanded = height == WRAP_CONTENT;
            txtMore.setVisibility(expanded ? View.GONE : View.VISIBLE);
        }

        @Override
        public void bindData(Review review) {
            txtAuthor.setText(review.author);
            txtContent.setText(review.content);
        }
    }
}
