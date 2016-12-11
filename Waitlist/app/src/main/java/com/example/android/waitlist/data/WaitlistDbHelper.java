/**
 * Copyright (c) 2016 Ivan Magda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.android.waitlist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.waitlist.data.WaitlistContract.WaitlistEntry;

public final class WaitlistDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "waitlist.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL_ATR = " NOT NULL";
    private static final String TIMESTAMP_TYPE = " TIMESTAMP";
    private static final String DEFAULT_ATR = " DEFAULT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " +
            WaitlistEntry.TABLE_NAME + " (" +
            WaitlistEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
            WaitlistEntry.COLUMN_GUEST_NAME + TEXT_TYPE + NOT_NULL_ATR + COMMA_SEP +
            WaitlistEntry.COLUMN_PARTY_SIZE + INTEGER_TYPE + NOT_NULL_ATR + COMMA_SEP +
            WaitlistEntry.COLUMN_TIMESTAMP + TIMESTAMP_TYPE + DEFAULT_ATR + " CURRENT_TIMESTAMP" +
            ");";

    private static final String SQL_DROP_WAITLIST_TABLE = "DROP TABLE IF EXIST " + WaitlistEntry.TABLE_NAME;

    /**
     * Constructs a new instance of {@link SQLiteOpenHelper}.
     *
     * @param context of the app
     */
    public WaitlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_WAITLIST_TABLE);
        onCreate(db);
    }

}
