package com.example.android.sunshine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickListener {
        public void onClick(int selectedIndex, String selectedWeather);
    }

    private String[] mWeatherData;
    private ForecastAdapterOnClickListener mClickListener;

    /**
     * Creates a ForecastAdapter.
     */
    public ForecastAdapter() {
        this.mWeatherData = null;
        this.mClickListener = null;
    }

    /**
     * Creates a ForecastAdapter.
     *
     * @param clickListener The on-click handler for this adapter. This single handler is called
     *                      when an item is clicked.
     * @param weatherData   The data source.
     */
    public ForecastAdapter(String[] weatherData, ForecastAdapterOnClickListener clickListener) {
        this.mWeatherData = weatherData;
        this.mClickListener = clickListener;
    }

    public void setListItemClickListener(ForecastAdapterOnClickListener forecastAdapterOnClickListener) {
        this.mClickListener = forecastAdapterOnClickListener;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mWeatherTextView;

        ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mClickListener != null) mClickListener.onClick(position, mWeatherData[position]);
        }
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
        View listItem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.forecast_list_item, viewGroup, false);
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
        String weatherData = mWeatherData[position];
        forecastAdapterViewHolder.mWeatherTextView.setText(weatherData);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        return (mWeatherData == null ? 0 : mWeatherData.length);
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param newWeatherData The new weather data to be displayed.
     */
    public void updateWithNewData(String[] newWeatherData) {
        mWeatherData = newWeatherData;
        notifyDataSetChanged();
    }
}
