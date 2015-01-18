package com.example.zzolta.omgandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.zzolta.omgandroid.R;
import com.example.zzolta.omgandroid.constants.book.OpenLibraryBookConstants;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Zolta.Szekely on 2015-01-17.
 */
public class JSONArrayAdapter extends BaseAdapter {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONArrayAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_book, null);

            holder = createViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setData(position, holder);

        return convertView;
    }

    private void setData(int position, ViewHolder holder) {
        final JSONObject jsonObject = (JSONObject) getItem(position);
        if (jsonObject.has(OpenLibraryBookConstants.COVER_I)) {
            final String imageID = jsonObject.optString(OpenLibraryBookConstants.COVER_I);

            final String imageURL = IMAGE_URL_BASE + imageID + "-L.jpg";

            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }

        final JSONObjectAdapter jsonObjectAdapter = new JSONObjectAdapter(jsonObject);
        holder.titleTextView.setText(jsonObjectAdapter.getBookTitle());
        holder.authorTextView.setText(jsonObjectAdapter.getAuthors());
    }

    private ViewHolder createViewHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
        holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
        holder.authorTextView = (TextView) convertView.findViewById(R.id.text_author);
        return holder;
    }

    public void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private ImageView thumbnailImageView;
        private TextView titleTextView;
        private TextView authorTextView;
    }
}
