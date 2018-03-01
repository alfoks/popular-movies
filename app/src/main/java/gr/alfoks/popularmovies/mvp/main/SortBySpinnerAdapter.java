package gr.alfoks.popularmovies.mvp.main;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.SortBy;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SortBySpinnerAdapter extends ArrayAdapter<SortBy> {
    private final SortBy[] sortOptions;
    private final LayoutInflater inflater;
    @LayoutRes
    private int viewResourceId;

    SortBySpinnerAdapter(Context context, int viewResourceId, SortBy[] sortOptions) {
        super(context, viewResourceId, sortOptions);

        this.sortOptions = sortOptions;
        this.inflater = LayoutInflater.from(context);
        this.viewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return sortOptions.length;
    }

    @Override
    public SortBy getItem(int position) {
        return sortOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getDisplayNameView(convertView, position, viewResourceId);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getDisplayNameView(convertView, position, viewResourceId);
    }

    private View getDisplayNameView(View convertView, int position, @LayoutRes int viewResourceId) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(viewResourceId, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        SortBy sortBy = getItem(position);
        if(sortBy != null) {
            viewHolder.txtSortBy.setText(sortBy.getDisplayName());
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.txtSortBy)
        TextView txtSortBy;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
