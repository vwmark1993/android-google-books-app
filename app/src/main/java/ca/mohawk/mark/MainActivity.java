/**
 * Statement of Authorship:
 *
 * I, Vincent Mark, 000803494 certify that this material is my original work.
 * No other person's work has been used without due acknowledgement.
 *
 * Video link: https://www.youtube.com/watch?v=Wp_fN1zzHnE
 */

package ca.mohawk.mark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Main Activity
 *
 * Calls DownloadBooksAsyncTask to begin the API query.
 * Initializes base variables and event handlers for the search screen.
 * Navigation drawer is used as a menu for configuration settings.
 * Shared preferences are used to store configuration settings across activities.
 * Snack bars for conveying information to the user.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "==MainActivity==";

    private static Activity mainActivity = null;
    private static FragmentManager mainActivityFragmentManager = null;
    private static ArrayList<Book> bookList = new ArrayList<Book>();

    public final String SP_SEARCH = "SEARCH";
    public final String SP_FILTER_SETTING = "FILTER";
    public final String SP_TEXT_SETTING = "TEXT";
    public final String SP_FILE = "shared_prefs_filter.dat";

    private DrawerLayout drawer;
    private Snackbar snackbar;
    private long lastClickTime = 0;

    /**
     * Provides access to the search screen activity to external classes.
     * Needed for setting toast notifications outside of main activity.
     * @return main activity
     */
    public static Activity getMainActivity() {
        return mainActivity;
    }

    /**
     * Provides access to the main activity's fragment manager to external classes.
     * Needed for handling the loading screen fragment outside of main activity.
     * @return support fragment manager of main activity
     */
    public static FragmentManager getMainActivityFragmentManager() {
        return mainActivityFragmentManager;
    }

    /**
     * Provides access to the main activity's list of books.
     * Serves as the master list of queried books throughout the application.
     * @return ArrayList of Book objects
     */
    public static ArrayList<Book> getBookList() {
        return bookList;
    }

    /**
     * Updates the main activity's list of books.
     * Needed whenever a search is initiated and new books are queried.
     * @param newBookList the new list of searched books
     */
    public static void setBookList(ArrayList<Book> newBookList) {
        bookList = newBookList;
    }

    /**
     * onCreate Lifecycle Hook
     *
     * Initializes activity variables, navigation drawer and search filter (top) fragment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_main);

        mainActivity = this;
        mainActivityFragmentManager = getSupportFragmentManager();
        drawer = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(this, drawer, (R.string.open), (R.string.close));
        drawer.addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();

        if (!mainActivityFragmentManager.isDestroyed()) {
            FragmentTransaction fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.topFrame, new SearchFilter());
            fragmentTransaction.commit();
        }
    }

    /**
     * onResume Lifecycle Hook
     *
     * Checks shared preferences for configuration settings.
     * Preserves application state on rotation.
     * Sets navigation drawer menu to match shared preferences across activities.
     * Sets search button click handler.
     * Calls DownloadBooksAsyncTask to begin the API query.
     * The application will automatically run a query on start up, using either default values or the values stored in shared preferences.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        SharedPreferences sharedPreferences = this.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String searchString = sharedPreferences.getString(SP_SEARCH, "");
        String filter = sharedPreferences.getString(SP_FILTER_SETTING, "");
        String textSize = sharedPreferences.getString(SP_TEXT_SETTING, "");
        EditText searchFilter = findViewById(R.id.searchFilter);
        searchFilter.setText(searchString);

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        // Default values.
        if (filter.equals("")) {
            editor.putString(SP_FILTER_SETTING, "intitle:");
            editor.apply();

            filter = "intitle:";
        }

        if (textSize.equals("")) {
            editor.putString(SP_TEXT_SETTING, "small");
            editor.apply();

            textSize = "small";
        }

        if (filter.equals("intitle:")) {
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);
            searchFilter.setHint("Search books by title...");
        } else if (filter.equals("inauthor:")) {
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);
            searchFilter.setHint("Search books by author...");
        } else if (filter.equals("inpublisher:")) {
            navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
            navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
            searchFilter.setHint("Search books by publisher...");
        }

        if (textSize.equals("large")) {
            navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(false);
        } else if (textSize.equals("small")) {
            navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(true);
            navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(false);
        }

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        DownloadBooksAsyncTask downloadBooksAsyncTask = new DownloadBooksAsyncTask();
        downloadBooksAsyncTask.startDownload(this, filter);
    }

    /**
     * Search Button Handler
     *
     * Saves the query string to shared preferences.
     * Calls DownloadBooksAsyncTask to begin the API query.
     * Notifies the user of the query results with a toast.
     * @param view
     */
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");

        // For throttling.
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();

        if (mainActivity != null) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            EditText searchFilter = findViewById(R.id.searchFilter);
            String searchString = searchFilter.getText().toString();
            editor.putString(SP_SEARCH, searchString);
            editor.apply();

            DownloadBooksAsyncTask downloadBooksAsyncTask = new DownloadBooksAsyncTask();

            String filter = sharedPreferences.getString(SP_FILTER_SETTING, "");
            downloadBooksAsyncTask.startDownload(this, filter);
        }
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

        SharedPreferences sharedPreferences = this.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        NavigationView navigationView = findViewById(R.id.navView);
        EditText searchFilter = findViewById(R.id.searchFilter);

        switch (item.getItemId()) {
            case R.id.titleFilterSetting:
                Log.d(TAG, "'Title' search filter selected");
                navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "intitle:");
                editor.apply();

                searchFilter.setHint("Search books by title...");
                searchFilter.setText("");

                Toast.makeText(this, "Selected: 'Title' Search Filter", Toast.LENGTH_SHORT).show();

                break;
            case R.id.authorFilterSetting:
                Log.d(TAG, "'Author' search filter selected");
                navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.publisherFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "inauthor:");
                editor.apply();

                searchFilter.setHint("Search books by author...");
                searchFilter.setText("");

                Toast.makeText(this, "Selected: 'Author' Search Filter", Toast.LENGTH_SHORT).show();

                break;
            case R.id.publisherFilterSetting:
                Log.d(TAG, "'Publisher' search filter selected");
                navigationView.getMenu().findItem(R.id.titleFilterSetting).setChecked(false);
                navigationView.getMenu().findItem(R.id.authorFilterSetting).setChecked(false);

                editor.putString(SP_FILTER_SETTING, "inpublisher:");
                editor.apply();

                searchFilter.setHint("Search books by publisher...");
                searchFilter.setText("");

                Toast.makeText(this, "Selected: 'Publisher' Search Filter", Toast.LENGTH_SHORT).show();

                break;
            case R.id.textSizeLargeSetting:
                Log.d(TAG, "'Large' text size selected");
                navigationView.getMenu().findItem(R.id.textSizeSmallSetting).setChecked(false);

                editor.putString(SP_TEXT_SETTING, "large");
                editor.apply();

                Toast.makeText(this, "Selected: 'Large' Text Size", Toast.LENGTH_SHORT).show();

                break;
            case R.id.textSizeSmallSetting:
                Log.d(TAG, "'Small' text size selected");
                navigationView.getMenu().findItem(R.id.textSizeLargeSetting).setChecked(false);

                editor.putString(SP_TEXT_SETTING, "small");
                editor.apply();

                Toast.makeText(this, "Selected: 'Small' Text Size", Toast.LENGTH_SHORT).show();

                break;
            case R.id.aboutSetting:
                Log.d(TAG, "'About' selected");
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