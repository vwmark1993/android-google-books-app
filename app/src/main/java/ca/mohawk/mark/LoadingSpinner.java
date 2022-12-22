package ca.mohawk.mark;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Loading Spinner Fragment
 *
 * This fragment is programmatically set every time a new API query begins; set by DownloadBooksAsyncTask.
 * Sets the layout view of the main activity's loading spinner view.
 */
public class LoadingSpinner extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_spinner, container, false);

        return view;
    }
}