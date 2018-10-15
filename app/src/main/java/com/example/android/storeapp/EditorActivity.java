package com.example.android.storeapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeapp.data.StoreContract.ProductEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /**
     * Whether the product has sale or no. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#NO_SALE}, or {@link ProductEntry#HAS_SALE}.
     */
    int saleOffer = ProductEntry.NO_SALE;
    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri currentProductUri;
    private boolean productHasChanged = false;
    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the productHasChanged boolean to true.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };
    /**
     * EditText field to enter the product's name
     */
    private EditText productNameText;
    /**
     * EditText field to enter the product's price
     */
    private EditText productPriceText;
    /**
     * Spinner field to enter the product's sale offer
     */
    private Spinner saleOfferSpinner;
    /**
     * EditText field to enter the product's quantity
     */
    private TextView productQuantityText;
    /**
     * EditText field to enter the product's supplier name
     */
    private EditText supplierNameText;
    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText supplierPhoneNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        productNameText = findViewById(R.id.edit_product_name);
        productPriceText = findViewById(R.id.edit_product_price);
        saleOfferSpinner = findViewById(R.id.spinner_sale);
        productQuantityText = findViewById(R.id.product_quantity);
        supplierNameText = findViewById(R.id.edit_product_supplier);
        supplierPhoneNumberText = findViewById(R.id.edit_supplier_phone_number);

        ImageView callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierPhoneNumber = supplierPhoneNumberText.getText().toString().trim();
                // Create an intent to handle the call functionality
                Intent call = new Intent(Intent.ACTION_DIAL);
                // Set the number to call on the intent
                call.setData(Uri.parse(getString(R.string.tel) + supplierPhoneNumber));
                // Check whether the user's device has an app that can handle the intent
                if (call.resolveActivity(getPackageManager()) != null) {
                    // Start the intent
                    startActivity(call);
                }
            }
        });

        // Receiving value into activity using intent.
        currentProductUri = getIntent().getData();
        if (currentProductUri == null) {
            setTitle(R.string.editor_activity_title_new_product);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // When the incrementButton is clicked get the value in the productQuantity TextView, convert it into integer,
        // increment it by one and set the new value back on the TextView
        Button incrementButton = findViewById(R.id.increment);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productQuantity = productQuantityText.getText().toString().trim();
                int quantity = Integer.parseInt(productQuantity);
                quantity += 1;
                productQuantityText.setText(String.valueOf(quantity));
            }
        });

        // When the decrementButton is clicked get the value in the productQuantity TextView, convert it into integer,
        // decrement it by one and set the new value back on the TextView
        Button decrementButton = findViewById(R.id.decrement);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productQuantity = productQuantityText.getText().toString().trim();
                int quantity = Integer.parseInt(productQuantity);
                if (quantity > 0) {
                    quantity -= 1;
                    productQuantityText.setText(String.valueOf(quantity));
                }
            }
        });

        productNameText.setOnTouchListener(touchListener);
        productPriceText.setOnTouchListener(touchListener);
        saleOfferSpinner.setOnTouchListener(touchListener);
        incrementButton.setOnTouchListener(touchListener);
        decrementButton.setOnTouchListener(touchListener);
        supplierNameText.setOnTouchListener(touchListener);
        supplierPhoneNumberText.setOnTouchListener(touchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the sale option of the product.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array
        // the spinner will use the default layout
        ArrayAdapter saleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sale_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        saleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        saleOfferSpinner.setAdapter(saleSpinnerAdapter);

        // Set the integer selected to the constant values
        saleOfferSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.no_sale))) {
                        saleOffer = ProductEntry.NO_SALE; // No Sale
                    } else if (selection.equals(getString(R.string.has_sale))) {
                        saleOffer = ProductEntry.HAS_SALE; // Sale Offer
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                saleOffer = ProductEntry.NO_SALE; // No sale
            }
        });
    }

    // Gets user input from the editor and save product to the database
    public void saveProduct() {
        // Gets user input from the editor
        String productName = productNameText.getText().toString().trim();
        String productPrice = productPriceText.getText().toString().trim();
        int price = 0;
        if (!TextUtils.isEmpty(productPrice)) {
            price = Integer.parseInt(productPrice);
        }

        String productQuantity = productQuantityText.getText().toString().trim();
        int quantity = Integer.parseInt(productQuantity);

        String supplierName = supplierNameText.getText().toString().trim();
        String supplierPhoneNumber = supplierPhoneNumberText.getText().toString().trim();

        // If the product hasn't changed and productName field (As the cursor automatically points to it
        // and the user doesn't have to touch the view to change it)
        // If so return early
        if (!productHasChanged && TextUtils.isEmpty(productName)) {
            // Exit activity
            finish();
            return;
        }

        // If the product is missing one of those values notify the user to supply them before saving
        else if ((TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice)
                || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhoneNumber))) {
            Toast.makeText(this, getString(R.string.editor_insert_all_product_info),
                    Toast.LENGTH_SHORT).show();
        }

        // If user entered a valid product info continue with adding or updating the product into the database
        else {
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(ProductEntry.COLUMN_SALE_OFFER, saleOffer);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierName);
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

            if (currentProductUri == null) {
                // Insert a new product into the provider, returning the content URI for the new product.
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Perform the update on the database and get the number of rows affected
                int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
                // Show a toast message depending on whether or not the updating was successful
                if (rowsAffected == 0) {
                    // If the rowsAffected = 0 then there is no product updated
                    Toast.makeText(this, getString(R.string.editor_update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // Gets user input from the editor
        String productName = productNameText.getText().toString().trim();
        // If the product hasn't changed and productName field (As the cursor automatically points to it
        // and the user doesn't have to touch the view to change it)
        // If so continue with handling back button press
        if (!productHasChanged && TextUtils.isEmpty(productName)) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save a new product
                saveProduct();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Gets user input from the editor
                String productName = productNameText.getText().toString().trim();
                // If the product hasn't changed and productName field (As the cursor automatically points to it
                // and the user doesn't have to touch the view to change it)
                // If so continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!productHasChanged && TextUtils.isEmpty(productName)) {
                    // Navigate back to parent activity (MainActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show dialog that there are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_SALE_OFFER,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int saleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SALE_OFFER);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int saleOffer = cursor.getInt(saleColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            productNameText.setText(name);
            productPriceText.setText(String.valueOf(price));
            productQuantityText.setText(String.valueOf(quantity));
            supplierNameText.setText(supplier);
            supplierPhoneNumberText.setText(supplierPhone);

            // Sale offer is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is no sale, 1 is has sale).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (saleOffer) {
                case ProductEntry.HAS_SALE:
                    saleOfferSpinner.setSelection(1);
                    break;
                default:
                    saleOfferSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        productNameText.setText("");
        productPriceText.setText("");
        productQuantityText.setText("");
        supplierNameText.setText("");
        supplierPhoneNumberText.setText("");
        saleOfferSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_dialog_msg);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            // Show a toast message depending on whether or not the deletion was successful
            if (rowsDeleted == 0) {
                // If the rowsDeleted = 0 then there is no product deleted
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
