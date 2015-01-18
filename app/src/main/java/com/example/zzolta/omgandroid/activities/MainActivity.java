package com.example.zzolta.omgandroid.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.zzolta.omgandroid.R;
import com.example.zzolta.omgandroid.adapters.JSONArrayAdapter;
import com.example.zzolta.omgandroid.adapters.JSONObjectAdapter;
import com.example.zzolta.omgandroid.constants.book.IntentExtraDataBookConstants;
import com.example.zzolta.omgandroid.constants.book.OpenLibraryBookConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";

    private static final String BOOK_QUERY_URL = "http://openlibrary.org/search.json?q=";

    private TextView mainTextView;
    private EditText mainEditText;
    private JSONArrayAdapter jsonArrayAdapter;
    private ShareActionProvider shareActionProvider;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayWelcome();

        mainTextView = (TextView) findViewById(R.id.main_textview);

        final Button mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        mainEditText = (EditText) findViewById(R.id.main_edittext);
        mainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MainActivity.this.onClick(null);
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return false;
            }
        });

        jsonArrayAdapter = new JSONArrayAdapter(this, getLayoutInflater());
        final ListView mainListView = (ListView) findViewById(R.id.main_listview);
        mainListView.setOnItemClickListener(this);
        mainListView.setAdapter(jsonArrayAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.search_in_progress));
        progressDialog.setCancelable(false);
    }

    private void displayWelcome() {
        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        final String name = sharedPreferences.getString(PREF_NAME, "");

        if (name.length() > 0) {
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {
            displayUserDialog();
        }
    }

    private void displayUserDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.greeting));
        alert.setMessage(getString(R.string.what_is_your_name));

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String inputName = input.getText().toString();
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_NAME, inputName);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if (shareItem != null) {
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        setShareIntent();

        return true;
    }

    private void setShareIntent() {
        if (shareActionProvider != null) {
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View v) {
        queryBooks(mainEditText.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final JSONObject jsonObject = (JSONObject) jsonArrayAdapter.getItem(position);
        final String coverID = jsonObject.optString(OpenLibraryBookConstants.COVER_I, "");
        final JSONObjectAdapter jsonObjectAdapter = new JSONObjectAdapter(jsonObject);
        final String title = jsonObjectAdapter.getBookTitle();
        final String authors = jsonObjectAdapter.getAuthors();

        final Intent detailIntent = new Intent(this, DetailActivity.class);

        detailIntent.putExtra(IntentExtraDataBookConstants.COVER_ID, coverID);
        detailIntent.putExtra(IntentExtraDataBookConstants.TITLE, title);
        detailIntent.putExtra(IntentExtraDataBookConstants.AUTHORS, authors);

        startActivity(detailIntent);
    }

    private void queryBooks(String searchString) {
        progressDialog.show();

        new AsyncHttpClient().get(BOOK_QUERY_URL + encodeString(searchString), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_LONG).show();

                jsonArrayAdapter.updateData(response.optJSONArray("docs"));
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    private String encodeString(String searchString) {
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return urlString;
    }
}
