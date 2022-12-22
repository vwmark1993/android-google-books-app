package ca.mohawk.mark;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Search Filter Fragment
 *
 * Sets the layout view of the main activity's search filter.
 */
public class SearchFilter extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_search_filter, container, false);

        return view;
    }
}