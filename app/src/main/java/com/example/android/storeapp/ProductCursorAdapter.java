package com.example.android.storeapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeapp.data.StoreContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   app context
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // Find the columns of product attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        final long id = cursor.getLong(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        String priceText = String.valueOf(productPrice) + " EGP";
        final int productQuantity = cursor.getInt(quantityColumnIndex);

        // Populate fields with extracted properties
        nameTextView.setText(productName);
        priceTextView.setText(priceText);
        quantityTextView.setText(String.valueOf(productQuantity));

        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saleProduct(context, id, productQuantity);
            }
        });
    }

    private void saleProduct(Context context, long productId, int quantity) {

        if (quantity > 0) {
            // Decrement quantity by one
            quantity -= 1;

            // Construct new uri and content values
            Uri updatedUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
            ContentValues values = new ContentValues();
            // Add the new value to the quantity column
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            // Perform the update on the database and get the number of rows affected which equals one if the sale was successful
            int rowsUpdated = context.getContentResolver().update(updatedUri, values, null, null);
            if (rowsUpdated != 1) {
                Toast.makeText(context, R.string.sale_process_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            //  Notify the user that the product is out of stock
            Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_LONG).show();
        }
    }
}
