package ca.mohawk.mark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * CustomAdapter Class
 *
 * Used to draw a custom ListView in Main Activity.
 * Source used: https://www.youtube.com/watch?v=zEU7lpAjaGo&ab_channel=SandipBhattacharya
 */
public class CustomAdapter extends ArrayAdapter<String> {
    public static final String TAG = "==CustomAdapter==";

    private Context context;
    private String[] titles;
    private String[] publishers;
    private String[] dates;
    private Bitmap[] thumbnails;

    /**
     * CustomAdapter Constructor
     *
     * Each item in the custom ListView will contain a: title, publisher, published date and thumbnail.
     * @param context listview context (main activity)
     * @param titles book titles
     * @param publishers book publishers
     * @param dates book dates
     * @param thumbnails book bitmap thumbnails
     */
    public CustomAdapter(Context context, String[] titles, String[] publishers, String[] dates, Bitmap[] thumbnails) {
        super(context, R.layout.listview_item_layout, R.id.textViewItemTitle, titles);

        this.context = context;
        this.titles = titles;
        this.publishers = publishers;
        this.dates = dates;
        this.thumbnails = thumbnails;
    }

    /**
     * Called whenever the ListView is scrolled.
     * Inflates the ListView items sequentially as you scroll up/down the ListView.
     * Used to set the view of each ListView item using a BookViewHolder helper class.
     * Re-uses existing ListView items as the ListView is scrolled through (if view is not null).
     * Sets a click listener to each ListView item to start the Second Activity.
     * @param position the index of each ListView item
     * @param convertView the recycled view used for drawing a new ListView item, saves performance since the view doesn't need to be completely re-inflated
     * @param parent the ListView's parent view
     * @return ListView row item view
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        BookViewHolder holder = null;

        if (listViewItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = layoutInflater.inflate(R.layout.listview_item_layout, parent, false);
            holder = new BookViewHolder(listViewItem);
            listViewItem.setTag(holder);
        }
        else {
            holder = (BookViewHolder) listViewItem.getTag();
        }

        holder.getTitle().setText(titles[position]);
        holder.getPublisher().setText(publishers[position]);
        holder.getDate().setText(dates[position]);

        if (thumbnails[position] != null) {
            holder.getThumbnail().setImageBitmap(thumbnails[position]);
        } else {
            holder.getThumbnail().setImageResource(R.drawable.outline_image_not_supported_24);
        }

        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Item clicked: " + position);

                Intent switchToSecondActivity = new Intent(context, SecondActivity.class);
                switchToSecondActivity.putExtra("bookIndex", position);
                context.startActivity(switchToSecondActivity);
            }
        });

        return listViewItem;
    }
}
