package ca.mohawk.mark;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * DownloadBooksAsyncTask Class
 *
 * Queries the Google Books API and retrieves the JSON data returned by the API.
 * Parses the JSON into Book objects and retrieves the bitmap thumbnail of each Book.
 * Stores the Books in Main Activity's book list.
 * Updates Main Activity after the API query is completed.
 */
public class DownloadBooksAsyncTask extends AsyncTask<String, Void, String> {
    public static final String TAG = "==DownloadBooksAsyncTask==";
    public static final int HTTP_OK = 200;

    private static String searchString = "";

    public static String getSearchString() {
        return searchString;
    }

    /**
     * Button Click Handler
     *
     * Composes URI and initiates the download task in the background.
     *
     * @param activity activity of the button clicked
     * @param filter API query search filter
     */
    public void startDownload(Activity activity, String filter) {
        Log.d(TAG, "startDownload: ");

        DownloadBooksAsyncTask dl = new DownloadBooksAsyncTask();
        String uri = "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";

        if (!filter.trim().equals("")) {
            uri += filter;
        }

        Log.d(TAG, "API query string: " + uri);

        EditText searchFilter = activity.findViewById(R.id.searchFilter);
        searchString = searchFilter.getText().toString();

        if (!searchString.trim().equals("")) {
            uri += searchString;
        }
        dl.execute(uri);
    }

    /**
     * Runs a non-blocking background thread.
     * Retrieves JSON data from the API query.
     * Parses the JSON data into Book objects and stores them in Main Activity's book list.
     * Retrieves the bitmap thumbnails of each Book and stores it inside the Book object.
     * Initiates loading spinner screen (LoadingSpinner fragment) in Main Activity.
     *
     * @param params passed in from the execute method
     * @return value sent to onPostExecute(), unused
     */
    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground: ");

        FragmentManager mainActivityFragmentManager = MainActivity.getMainActivityFragmentManager();

        if (!mainActivityFragmentManager.isDestroyed()) {
            FragmentTransaction fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.bottomFrame, new LoadingSpinner());
            fragmentTransaction.commit();
        }

        StringBuilder results = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            String line = null;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int statusCode = conn.getResponseCode();

            if (statusCode == HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                while ((line = bufferedReader.readLine()) != null) {
                    results.append(line);
                }
            }
            Log.d(TAG, "Response Code: " + statusCode);
        } catch (IOException ex) {
            Log.d(TAG, "Caught Exception: " + ex);
        }

        String resultString = results.toString();

        ArrayList<Book> bookList = new ArrayList<Book>();

        if (resultString == null) {
            Log.d(TAG, "No results.");

            Toast.makeText(MainActivity.getMainActivity(), "No results found for '" + getSearchString() + "'.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject json = new JSONObject(resultString);

                if (json.has("items")) {
                    JSONArray books = json.getJSONArray("items");

                    // Create a Book object from each item in the array using JSON properties.
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject book = books.getJSONObject(i);

                        String title = "";
                        String description = "";
                        int pages = 0;
                        String publishedDate = "";
                        String publisher = "";
                        String imageURL = "";
                        Bitmap thumbnail = null;

                        if (book.has("volumeInfo")) {

                            if (book.getJSONObject("volumeInfo").has("title")) {
                                title = book.getJSONObject("volumeInfo").getString("title");
                            }

                            if (book.getJSONObject("volumeInfo").has("description")) {
                                description = book.getJSONObject("volumeInfo").getString("description");
                            }

                            if (book.getJSONObject("volumeInfo").has("pageCount")) {
                                pages = book.getJSONObject("volumeInfo").getInt("pageCount");
                            }

                            if (book.getJSONObject("volumeInfo").has("publishedDate")) {
                                publishedDate = book.getJSONObject("volumeInfo").getString("publishedDate");
                            }

                            if (book.getJSONObject("volumeInfo").has("publisher")) {
                                publisher = book.getJSONObject("volumeInfo").getString("publisher");
                            }

                            if (book.getJSONObject("volumeInfo").has("imageLinks") &&
                                    book.getJSONObject("volumeInfo").getJSONObject("imageLinks").has("thumbnail")) {
                                imageURL = book.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");

                                int statusCode = -1;

                                try {
                                    URL url = new URL(imageURL);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    statusCode = conn.getResponseCode();
                                    if (statusCode == HTTP_OK) {
                                        thumbnail = BitmapFactory.decodeStream(conn.getInputStream());
                                    }
                                } catch (MalformedURLException e) {
                                    Log.d(TAG, "Bad URL: " + e);
                                } catch (IOException e) {
                                    Log.d(TAG, "Bad I/O: " + e);
                                }
                            }

                            bookList.add(new Book(title, description, pages, publishedDate, publisher, imageURL, thumbnail));
                        } else {
                            Log.d(TAG, "Book lacks volume info.");
                        }
                    }
                } else {
                    Log.d(TAG, "No books found.");
                }
                MainActivity.setBookList(bookList);
            } catch (Throwable t) {
                Log.e(TAG, "Could not parse JSON.");
                Log.e(TAG, t.toString());
            }
        }
        return resultString;
    }

    /**
     * Updates Main Activity once the download has been completed.
     * Replace loading spinner screen with query results (SearchList fragment) in Main Activity.
     * Notifies the user of the query results with a toast.
     *
     * @param result unused
     */
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: ");

        FragmentManager mainActivityFragmentManager = MainActivity.getMainActivityFragmentManager();
        if (!mainActivityFragmentManager.isDestroyed()) {
            FragmentTransaction fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.bottomFrame, new SearchList());
            fragmentTransaction.commit();
        }

        int numberOfBooks = MainActivity.getBookList().size();

        if (searchString.trim().equals("")) {
            Toast.makeText(MainActivity.getMainActivity(), numberOfBooks + " results found.", Toast.LENGTH_SHORT).show();
        } else if (numberOfBooks > 0) {
            Toast.makeText(MainActivity.getMainActivity(), numberOfBooks + " results found for '" + searchString + "'.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.getMainActivity(), "No results found for '" + searchString + "'.", Toast.LENGTH_SHORT).show();
        }
    }
}
