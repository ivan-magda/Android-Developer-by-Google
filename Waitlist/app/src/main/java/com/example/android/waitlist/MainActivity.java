package com.example.android.waitlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.android.waitlist.data.TestUtil;
import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private GuestListAdapter mAdapter;

    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    private void setup() {
        configureDatabase();
        configureRecyclerView();

        mNewGuestNameEditText = (EditText) findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = (EditText) findViewById(R.id.party_count_edit_text);
    }

    private void configureDatabase() {
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDatabase);
    }

    private void configureRecyclerView() {
        RecyclerView waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new GuestListAdapter(this, getAllGuests());
        waitlistRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                GuestListAdapter.GuestViewHolder vh = (GuestListAdapter.GuestViewHolder) viewHolder;
                long id = vh.getGuestId();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
            }
        }).attachToRecyclerView(waitlistRecyclerView);
    }

    /**
     * Returns all guests Cursor ordered by timestamp.
     */
    private Cursor getAllGuests() {
        return mDatabase.query(WaitlistContract.WaitlistEntry.TABLE_NAME, null, null, null, null, null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP);
    }

    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     * @param view The calling view (button)
     */
    public void addToWaitlist(View view) {
        if (TextUtils.isEmpty(mNewGuestNameEditText.getText().toString()) ||
                TextUtils.isEmpty(mNewPartySizeEditText.getText().toString())) return;
        String name = mNewGuestNameEditText.getText().toString();

        int partySize = 1;
        partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());

        addNewGuest(name, partySize);
        mAdapter.swapCursor(getAllGuests());
    }

    private long addNewGuest(String name, int partySize) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        contentValues.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return mDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, contentValues);
    }

    private boolean removeGuest(long id) {
        int affected = mDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME,
                WaitlistContract.WaitlistEntry._ID + "=" + id, null);
        return affected > 0;
    }

}
