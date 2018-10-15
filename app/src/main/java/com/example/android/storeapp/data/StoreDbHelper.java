package com.example.android.storeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.storeapp.data.StoreContract.ProductEntry;


public class StoreDbHelper extends SQLiteOpenHelper {

    // If you change the data base schema, you must increment the data base version.
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "store.db";

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // CREATE TABLE products (_id INTEGER, Product Name TEXT, Price INTEGER, Sale Offer INTEGER, Quantity INTEGER,
        //                        Supplier Name TEXT, Supplier Phone Number TEXT);
        // Crate a string to contain the SQL statement to create the products table

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" + ProductEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " + ProductEntry.COLUMN_SALE_OFFER +
                " INTEGER NOT NULL DEFAULT 0, " + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, " + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
