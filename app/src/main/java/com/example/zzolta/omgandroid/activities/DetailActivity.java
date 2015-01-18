package com.example.zzolta.omgandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzolta.omgandroid.R;
import com.example.zzolta.omgandroid.constants.book.IntentExtraDataBookConstants;
import com.squareup.picasso.Picasso;

/**
 * Created by Zolta.Szekely on 2015-01-17.
 */
public class DetailActivity extends ActionBarActivity {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    String mImageURL;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

        String coverID = this.getIntent().getExtras().getString(IntentExtraDataBookConstants.COVER_ID);

        if (coverID.length() > 0) {
            mImageURL = IMAGE_URL_BASE + coverID + "-L.jpg";
        }

        Picasso.with(this).load(mImageURL).placeholder(R.drawable.img_books_loading).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.text_title);
        titleView.setText(this.getIntent().getExtras().getString(IntentExtraDataBookConstants.TITLE));

        TextView authors = (TextView) findViewById(R.id.text_author);
        authors.setText(this.getIntent().getExtras().getString(IntentExtraDataBookConstants.AUTHORS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        setShareIntent();

        return true;
    }

    private void setShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mImageURL);

        mShareActionProvider.setShareIntent(shareIntent);
    }
}
