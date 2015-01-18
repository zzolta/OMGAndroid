package com.example.zzolta.omgandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzolta.omgandroid.constants.book.OpenLibraryBookConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zolta.Szekely on 2015-01-17.
 */
public class JSONAdapter extends BaseAdapter {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
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
        ViewHolder holder;

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
        JSONObject jsonObject = (JSONObject) getItem(position);
        if (jsonObject.has(OpenLibraryBookConstants.COVER_I)) {
            String imageID = jsonObject.optString(OpenLibraryBookConstants.COVER_I);

            String imageURL = IMAGE_URL_BASE + imageID + "-L.jpg";

            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }

        holder.titleTextView.setText(getBookTitle(jsonObject));
        holder.authorTextView.setText(getAuthors(jsonObject));
    }

    private String getAuthors(JSONObject jsonObject) {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        if (jsonObject.has(OpenLibraryBookConstants.AUTHOR_NAME)) {
            JSONArray jsonArray = jsonObject.optJSONArray(OpenLibraryBookConstants.AUTHOR_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (i != 0) {
                        sb.append(delimiter);
                    }
                    sb.append(jsonArray.get(i).toString());
                } catch (JSONException ignored) {
                }
            }
        }
        return sb.toString();
    }

    private String getBookTitle(JSONObject jsonObject) {
        String bookTitle = "";
        if (jsonObject.has(OpenLibraryBookConstants.TITLE)) {
            bookTitle = jsonObject.optString(OpenLibraryBookConstants.TITLE);
        }
        return bookTitle;
    }

    private ViewHolder createViewHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
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
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }
}
