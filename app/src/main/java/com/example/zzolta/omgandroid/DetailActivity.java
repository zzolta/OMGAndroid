package com.example.zzolta.omgandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Zolta.Szekely on 2015-01-17.
 */
public class DetailActivity extends ActionBarActivity {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    String mImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

        String coverID = this.getIntent().getExtras().getString("coverID");

        if (coverID.length() > 0) {
            mImageURL = IMAGE_URL_BASE + coverID + "-L.jpg";
        }

        Picasso.with(this).load(mImageURL).placeholder(R.drawable.img_books_loading).into(imageView);
    }
}
