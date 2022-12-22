package ca.mohawk.mark;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * BookViewHolder Helper Class
 *
 * Used to hold the view elements of each ListView item.
 */
public class BookViewHolder {

    private TextView title;
    private TextView publisher;
    private TextView date;
    private ImageView thumbnail;

    /**
     * BookViewHolder Constructor
     *
     * @param v
     */
    public BookViewHolder(View v) {
        title = v.findViewById(R.id.textViewItemTitle);
        publisher = v.findViewById(R.id.textViewItemPublisher);
        date = v.findViewById(R.id.textViewItemDate);
        thumbnail = v.findViewById(R.id.imageViewItemThumbnail);
    }

    /**
     * Retrieves the ListView item's title TextView.
     * @return TextView
     */
    public TextView getTitle() {
        return title;
    }

    /**
     * Retrieves the ListView item's publisher TextView.
     * @return TextView
     */
    public TextView getPublisher() {
        return publisher;
    }

    /**
     * Retrieves the ListView item's publshed date TextView.
     * @return TextView
     */
    public TextView getDate() {
        return date;
    }

    /**
     * Retrieves the ListView item's thumbnail ImageView.
     * @return ImageView
     */
    public ImageView getThumbnail() {
        return thumbnail;
    }
}
