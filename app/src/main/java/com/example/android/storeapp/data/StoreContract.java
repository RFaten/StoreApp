package com.example.android.storeapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the store app.
 */
public class StoreContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.storeapp";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.storeapp/products/ is a valid path for
     * looking at product data. content://com.example.android.storeapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StoreContract() {
    }

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single product.
     */
    public static abstract class ProductEntry implements BaseColumns {

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Name of database table for products
         */
        public static final String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "Product";

        /**
         * Price of the product.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_PRICE = "Price";

        /**
         * Available quantity of the product.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";

        /**
         * Show whether there is a sale on the product or not.
         * <p>
         * The only possible values are {@link #NO_SALE}, {@link #HAS_SALE}.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_SALE_OFFER = "Sale";

        /**
         * Supplier name.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_SUPPLIER = "Supplier";

        /**
         * Supplier phone number.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone";

        /**
         * Possible values for the sale offer.
         */
        public static final int NO_SALE = 0;
        public static final int HAS_SALE = 1;

        /**
         * Returns whether or not the given sale is {@link #NO_SALE} or {@link #HAS_SALE}.
         */
        public static boolean isValidSale(int sale) {
            if (sale == NO_SALE || sale == HAS_SALE) {
                return true;
            }
            return false;
        }
    }
}
