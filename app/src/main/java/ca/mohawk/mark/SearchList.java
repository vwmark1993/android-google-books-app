package ca.mohawk.mark;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Search Filter Fragment
 *
 * This fragment is programmatically set every time a new API query is completed; set by DownloadBooksAsyncTask.
 * Sets the layout view of the main activity's query result list view.
 * Re-draws the list view after an API query finishes.
 */
public class SearchList extends Fragment {
    public static final String TAG = "==SearchListFragment==";

    /**
     * onCreateView Lifecycle Hook
     *
     * Inflates the view.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        return view;
    }

    /**
     * onViewCreated Lifecycle Hook
     *
     * Sets the ListView using a CustomAdapter.
     * Passes the title, publisher, published date and thumbnail to each Book into the ListView to be displayed.
     * @param v
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");

        ArrayList<Book> bookList = MainActivity.getBookList();

        ListView listView = getActivity().findViewById(R.id.listView);
        TextView textViewResults = getActivity().findViewById(R.id.textViewResults);

        String[] titleArray = new String[bookList.size()];
        String[] publisherArray = new String[bookList.size()];
        String[] publishedDateArray = new String[bookList.size()];
        Bitmap[] thumbnailArray = new Bitmap[bookList.size()];

        for (int i = 0; i < bookList.size(); i++) {
            titleArray[i] = bookList.get(i).getTitle();
            publisherArray[i] = bookList.get(i).getPublisher();
            publishedDateArray[i] = bookList.get(i).getPublishedDate();
            thumbnailArray[i] = bookList.get(i).getThumbnail();
        }

        if (bookList.size() > 0) {
            Log.d(TAG, "query results returned");
            CustomAdapter customAdapter = new CustomAdapter(MainActivity.getMainActivity(), titleArray, publisherArray, publishedDateArray, thumbnailArray);

            listView.setAdapter(customAdapter);
            textViewResults.setText("Results (" + bookList.size() + "):");
        } else {
            Log.d(TAG, "no query results");
            listView.setAdapter(null);
            textViewResults.setText("No Results");
        }
    }
}