package ca.mohawk.mark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Second Activity
 *
 * Displays details about the selected Book object.
 * Initializes base variables and event handlers for the details screen.
 * Navigation drawer is used as a menu for configuration settings.
 * Shared preferences are used to store configuration settings across activities.
 * Snack bars for conveying information to the user.
 */
public class SecondActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "==SecondActivity==";

    public final String SP_SEARCH = "SEARCH";
    public final String SP_FILTER_SETTING = "FILTER";
    public final String SP_TEXT_SETTING = "TEXT";
    public final String SP_FILE = "shared_prefs_filter.dat";

    private DrawerLayout drawer;
    private Snackbar snackbar;

    /**
     * onCreate Lifecycle Hook
     *
     * Initializes activity variables and navigation drawer.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_second);
        drawer = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(this, drawer, (R.string.open), (R.string.close));
        drawer.addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();
    }

    /**
     * onStart Lifecycle Hook
     *
     * Retrieves the selected Book object from Main Activity's book list using the book index obtained through intent.
     * Sets the details of the selected Book object to the view.
     * Sends notification of thumbnail is unavailable.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        Intent intent = getIntent();
        int bookIndex = intent.getIntExtra("bookIndex", 0);

        Book selectedBook = null;

        if (MainActivity.getBookList().get(bookIndex) != null) {
            selectedBook = MainActivity.getBookList().get(bookIndex);
        }

        String bookTitle = selectedBook.getTitle();
        String bookDescription = selectedBook.getDescription();
        String bookPages = String.valueOf(selectedBook.getPages());
        String bookPublishedDate = selectedBook.getPublishedDate();
        String bookPublisher = selectedBook.getPublisher();

        TextView title = findViewById(R.id.textViewDetailsTitle);
        TextView description = findViewById(R.id.textViewDetailsDescription);
        TextView pages = findViewById(R.id.textViewDetailsPages);
        TextView publishedDate = findViewById(R.id.textViewDetailsPublishedYear);
        TextView publisher = findViewById(R.id.textViewDetailsPublisher);

        if (bookDescription.trim().equals("")) {
            bookDescription = "No description found.";
        }

        if (!bookPages.trim().equals("0")) {
            bookPages = bookPages + " pages";
        } else {
            bookPages = "";
        }

        title.setText(bookTitle);
        description.setText(bookDescription);
        pages.setText(bookPages);
        publishedDate.setText(bookPublishedDate);
        publisher.setText(bookPublisher);

        description.setMovementMethod(new ScrollingMovementMethod());

        ImageView imageView = findViewById(R.id.imageViewDetailsThumbnail);

        if (selectedBook.getThumbnail() != null) {
            imageView.setImageBitmap(selectedBook.getThumbnail());
        } else {
            Toast.makeText(this, "Thumbnail unavailable.", Toast.LENGTH_SHORT).show();
        }

        ImageButton closeButton = findViewById(R.id.exitSecondActivityButton);
        closeButton.setOnClickListener(this);
    }

    /**
     * onResume Lifecycle Hook
     *
     * Checks shared preferences for configuration settings.
     * Preserves application state on rotation.
     * Sets navigation drawer menu to match shared preferences across activities.
     * Sets the text size of view elements based on navigation drawer menu settings.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        SharedPreferences sharedPreferences = this.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        String filter = sharedPreferences.getString(SP_FILTER_SETTING, "");
        String textSize = sharedPreferences.getString(SP_TEXT_SETTING, "");

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        TextView title = findViewById(R.id.textViewDetailsTitle);
        TextView publisher = findViewById(R.id.textViewDetailsPublisher);
        TextView date = findViewById(R.id.textViewDetailsPublishedYear);
        TextView pages = findViewById(R.id.textViewDetailsPages);
        TextView description = findViewById(R.id.textViewDetailsDescription);

        if (filter.equals("intitle:")) {
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);
        } else if (filter.equals("inauthor:")) {
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);
        } else if (filter.equals("inpublisher:")) {
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
        }

        if (textSize.equals("large")) {
            navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(false);

            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            publisher.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            pages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);

        } else if (textSize.equals("small")) {
            navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(false);

            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            publisher.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            pages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        }
    }

    /**
     * Return Button Handler
     *
     * Sends the user back to the search screen.
     * @param view view of the clicked button
     */
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");

        Intent switchToMainActivity = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    /**
     * Navigation Drawer Menu Handler
     *
     * The navigation drawer menu allows the user to configure the main activity's search filter and the second activity's text size.
     * Provides help information to the user using snack bars.
     * Saves selected option to shared preferences.
     * @param item selected menu item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");

        item.setChecked(true);
        NavigationView navigationView = findViewById(R.id.navView);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextView title = findViewById(R.id.textViewDetailsTitle);
        TextView publisher = findViewById(R.id.textViewDetailsPublisher);
        TextView date = findViewById(R.id.textViewDetailsPublishedYear);
        TextView pages = findViewById(R.id.textViewDetailsPages);
        TextView description = findViewById(R.id.textViewDetailsDescription);

        switch (item.getItemId()) {
            case R.id.titleFilterSetting:
                navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "intitle:");
                editor.putString(SP_SEARCH, "");
                editor.apply();

                Toast.makeText(this, "Author Filter Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.authorFilterSetting:
                navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "inauthor:");
                editor.putString(SP_SEARCH, "");
                editor.apply();

                Toast.makeText(this, "Author Filter Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.publisherFilterSetting:
                navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "inpublisher:");
                editor.putString(SP_SEARCH, "");
                editor.apply();

                Toast.makeText(this, "Publisher Filter Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textSizeLargeSetting:
                navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(false);

                editor.putString(SP_TEXT_SETTING, "large");
                editor.apply();

                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                publisher.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                pages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);

                Toast.makeText(this, "Large Text Size Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textSizeSmallSetting:
                navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(false);

                editor.putString(SP_TEXT_SETTING, "small");
                editor.apply();

                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                publisher.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                pages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

                Toast.makeText(this, "Small Text Size Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.aboutSetting:
                snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
                snackbar.setBackgroundTint(Color.rgb(224,232,231));

                LayoutInflater inflater = getLayoutInflater();
                View snackbarView = inflater.inflate(R.layout.snackbar_layout_1, null);
                ViewGroup snackBarLayout = (ViewGroup) snackbar.getView();

                snackBarLayout.addView(snackbarView, 0);

                Button nextButton = snackbarView.findViewById(R.id.nextSnackbarButton);
                nextButton.setOnClickListener(this::snackBarHandler);
                Button closeButton = snackbarView.findViewById(R.id.closeSnackbarButton);
                closeButton.setOnClickListener(this::snackBarHandler);
                snackbar.show();
                break;
        }
        return false;
    }

    /**
     * Snack Bar Handler
     *
     * Handles the click events for the snack bars.
     * The user may page flip between two different snack bars which contain information about the application.
     * @param button view of the clicked button
     */
    public void snackBarHandler(View button) {
        Log.d(TAG, "snackBarHandler: ");

        LayoutInflater inflater = getLayoutInflater();

        if (button.getId() == R.id.nextSnackbarButton) {
            Log.d(TAG, "'Next' snack bar button clicked");

            snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
            snackbar.setBackgroundTint(Color.rgb(224,232,231));

            View snackbarView = inflater.inflate(R.layout.snackbar_layout_2, null);
            ViewGroup snackBarLayout = (ViewGroup) snackbar.getView();

            Button backButton = snackbarView.findViewById(R.id.backSnackbarButton);
            backButton.setOnClickListener(this::snackBarHandler);
            Button closeButton = snackbarView.findViewById(R.id.closeSnackbarButton);
            closeButton.setOnClickListener(this::snackBarHandler);

            snackBarLayout.addView(snackbarView, 0);
            snackbar.show();
        } else if (button.getId() == R.id.backSnackbarButton) {
            Log.d(TAG, "'Back' snack bar button clicked");

            snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
            snackbar.setBackgroundTint(Color.rgb(224,232,231));

            View snackbarView = inflater.inflate(R.layout.snackbar_layout_1, null);
            ViewGroup snackBarLayout = (ViewGroup) snackbar.getView();

            Button closeButton = snackbarView.findViewById(R.id.closeSnackbarButton);
            closeButton.setOnClickListener(this::snackBarHandler);
            Button nextButton = snackbarView.findViewById(R.id.nextSnackbarButton);
            nextButton.setOnClickListener(this::snackBarHandler);

            snackBarLayout.addView(snackbarView, 0);
            snackbar.show();
        } else if (button.getId() == R.id.closeSnackbarButton) {
            Log.d(TAG, "'Close' snack bar button clicked");

            NavigationView navigationView = findViewById(R.id.navView);
            navigationView.getMenu().findItem(R.id.aboutSetting).setChecked(false);

            snackbar.dismiss();
        }
    }

    /**
     * Navigation Drawer Display Handler
     *
     * Handles the display state of the navigation drawer.
     * @param item navigation drawer icon
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");

        boolean isOpen = drawer.isDrawerOpen(GravityCompat.START);
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isOpen == true) {
                    Log.d(TAG, "drawer closed");
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    Log.d(TAG, "drawer opened");
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}