package com.example.android.waitlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.waitlist.data.TestUtil;
import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private GuestListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    private void setup() {
        configureDatabase();
        configureRecyclerView();
    }

    private void configureDatabase() {
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDatabase);
    }

    private void configureRecyclerView() {
        RecyclerView waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = getAllGuests();
        mAdapter = new GuestListAdapter(this, cursor.getCount());
        waitlistRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     * @param view The calling view (button)
     */
    public void addToWaitlist(View view) {

    }

    /**
     * Returns all guests Cursor ordered by timestamp.
     */
    private Cursor getAllGuests() {
        return mDatabase.query(WaitlistContract.WaitlistEntry.TABLE_NAME, null, null, null, null, null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP);
    }

}
