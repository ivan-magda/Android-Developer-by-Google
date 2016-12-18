package com.example.android.sunshine.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.R;
import com.example.android.sunshine.activity.MainActivity;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout;

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickListener {
        /**
         * @param position     Index of the selected item.
         * @param dateInMillis The date of item.
         */
        void onClick(int position, long dateInMillis);
    }

    private Cursor mCursor;
    private ForecastAdapterOnClickListener mClickListener;

    /**
     * Creates a ForecastAdapter.
     *
     * @param context       The context.
     * @param clickListener The on-click handler for this adapter. This single handler is called
     *                      when an item is clicked.
     * @param cursor        The cursor.
     */
    public ForecastAdapter(@NonNull final Context context, Cursor cursor,
                           ForecastAdapterOnClickListener clickListener) {
        this.mCursor = cursor;
        this.mClickListener = clickListener;
        mUseTodayLayout = context.getResources().getBoolean(R.bool.use_today_layout);
    }

    public void setListItemClickListener(ForecastAdapterOnClickListener forecastAdapterOnClickListener) {
        this.mClickListener = forecastAdapterOnClickListener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.forecast_list_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View listItem = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        listItem.setFocusable(true);

        return new ForecastAdapterViewHolder(listItem);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        forecastAdapterViewHolder.bindAtPosition(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        return (mCursor == null ? 0 : mCursor.getCount());
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position. This method is useful when we want to use different layouts for different items
     * depending on their position. In Sunshine, we take advantage of this method to provide a
     * different layout for the "today" layout. The "today" layout is only shown in portrait mode
     * with the first item in the list.
     *
     * @param position index within our RecyclerView and Cursor
     * @return the view type (today or future day)
     */
    @Override
    public int getItemViewType(int position) {
        return (mUseTodayLayout && position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;
        final ImageView iconView;

        private long mNormalizedDate;

        ForecastAdapterViewHolder(View itemView) {
            super(itemView);

            iconView = (ImageView) itemView.findViewById(R.id.weather_icon);
            dateView = (TextView) itemView.findViewById(R.id.date);
            descriptionView = (TextView) itemView.findViewById(R.id.weather_description);
            highTempView = (TextView) itemView.findViewById(R.id.high_temperature);
            lowTempView = (TextView) itemView.findViewById(R.id.low_temperature);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mClickListener != null) mClickListener.onClick(position, mNormalizedDate);
        }

        void bindAtPosition(final int position) {
            mCursor.moveToPosition(position);
            final Context context = itemView.getContext();

            /****************
             * Weather Icon *
             ****************/
            int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);

            int weatherImageId;
            int viewType = ForecastAdapter.this.getItemViewType(position);
            switch (viewType) {
                case VIEW_TYPE_TODAY:
                    weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
                    break;
                case VIEW_TYPE_FUTURE_DAY:
                    weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
            iconView.setImageResource(weatherImageId);

            /****************
             * Weather Date *
             ****************/
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            String dateString = SunshineDateUtils.getFriendlyDateString(context, dateInMillis, false);
            dateView.setText(dateString);

            this.mNormalizedDate = dateInMillis;

            /***********************
             * Weather Description *
             ***********************/
            String description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);
            String descriptionA11y = context.getString(R.string.a11y_forecast, description);

            /* Set the text and content description (for accessibility purposes) */
            descriptionView.setText(description);
            descriptionView.setContentDescription(descriptionA11y);

            /**************************
             * High (max) temperature *
             **************************/
            double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
            /**
             * If the user's preference for weather is fahrenheit, formatTemperature will convert
             * the temperature. This method will also append either °C or °F to the temperature
             * String.
             */
            String highString = SunshineWeatherUtils.formatTemperature(context, highInCelsius);
            String highA11y = context.getString(R.string.a11y_high_temp, highString);

            /** Set the text and content description (for accessibility purposes) */
            highTempView.setText(highString);
            highTempView.setContentDescription(highA11y);

            /*************************
             * Low (min) temperature *
             *************************/
            /** Read low temperature from the cursor (in degrees celsius) */
            double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
            /**
             * If the user's preference for weather is fahrenheit, formatTemperature will convert
             * the temperature. This method will also append either °C or °F to the temperature
             * String.
             */
            String lowString = SunshineWeatherUtils.formatTemperature(context, lowInCelsius);
            String lowA11y = context.getString(R.string.a11y_low_temp, lowString);

            /** Set the text and content description (for accessibility purposes) */
            lowTempView.setText(lowString);
            lowTempView.setContentDescription(lowA11y);
        }
    }

}
