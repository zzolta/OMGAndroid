package com.example.zzolta.omgandroid.adapters;

import com.example.zzolta.omgandroid.constants.book.OpenLibraryBookConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zolta.Szekely on 2015-01-18.
 */
public class JSONObjectAdapter {

    JSONObject jsonObject;

    public JSONObjectAdapter(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getAuthors() {
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

    public String getBookTitle() {
        String bookTitle = "";
        if (jsonObject.has(OpenLibraryBookConstants.TITLE)) {
            bookTitle = jsonObject.optString(OpenLibraryBookConstants.TITLE);
        }
        return bookTitle;
    }
}
