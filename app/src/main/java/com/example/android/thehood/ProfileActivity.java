package com.example.android.thehood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

/**
 * Shows the user profile. This simple activity can function regardless of whether the user
 * is currently logged in.
 */
public class ProfileActivity extends Activity {
    private static final int LOGIN_REQUEST = 0;

    // Log tag(for debugging purposes only) :
    private final String LOG_TAG = "ProfileActivity says: ";

    private TextView titleTextView;
    private TextView emailTextView;
    private TextView nameTextView;
    private Button loginOrLogoutButton;
    private Button launchAppButton;

    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        titleTextView = (TextView) findViewById(R.id.profile_title);
        emailTextView = (TextView) findViewById(R.id.profile_email);
        nameTextView = (TextView) findViewById(R.id.profile_name);
        loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
        launchAppButton = (Button) findViewById(R.id.launch_app_button);
        titleTextView.setText(R.string.profile_title_logged_in);

        loginOrLogoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    // User clicked to log out.
                    ParseUser.logOut();
                    currentUser = null;
                    showProfileLoggedOut();
                } else {
                    // User clicked to log in.
                    ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                            ProfileActivity.this);
                    startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
                }
            }
        });

        launchAppButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "launch app clicked!");

                if(currentUser.getParseGeoPoint("Address") == null) {
                    Log.v(LOG_TAG, "No address");

                    Intent addressInputIntent = new Intent(v.getContext(), UserAddressInput.class);
                    startActivity(addressInputIntent);
                }
                else
                {
                    String pgp = currentUser.getParseGeoPoint("Address").toString();
                    Log.v(LOG_TAG, "GeoPoint: " + pgp);
                    //Launches the main page if we already have an address
                    Intent mainPageIntent = new Intent(v.getContext(),MainPage.class);
                    startActivity(mainPageIntent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            showProfileLoggedIn();
        } else {
            showProfileLoggedOut();
        }
    }

    /**
     * Shows the profile of the given user.
     */
    private void showProfileLoggedIn() {
        titleTextView.setText(R.string.profile_title_logged_in);
        emailTextView.setText(currentUser.getEmail());
        String fullName = currentUser.getString("name");
        if (fullName != null) {
            nameTextView.setText(fullName);
        }
        loginOrLogoutButton.setText(R.string.profile_logout_button_label);
        loginOrLogoutButton.setBackgroundColor(Color.RED);
        launchAppButton.setVisibility(View.VISIBLE);
    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {
        titleTextView.setText(R.string.profile_title_logged_out);
        emailTextView.setText("");
        nameTextView.setText("");
        loginOrLogoutButton.setText(R.string.profile_login_button_label);
        loginOrLogoutButton.setBackgroundColor(Color.GREEN);
        launchAppButton.setVisibility(View.INVISIBLE);
    }
}