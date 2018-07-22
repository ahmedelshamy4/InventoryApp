package com.example.ahmed.inventoryapp.ProductData;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URI;

public class ProductsProvider extends ContentProvider {

    ProductContract.DetailsEntity contract;
    ProductsDB.ProductsDetails details;
    SQLiteDatabase db;

    static final String ProviderName = "com.example.inventory";
    static final String Product = "products";
    static final String Url = "content://" + ProviderName + "/" + Product;
    public static final Uri contentUri = Uri.parse(Url);

    public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ProviderName + "/" + Product;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ProviderName + "/" + Product;

    static final int product = 1;
    static final int products = 2;
    static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ProviderName, "products", products);
        URI_MATCHER.addURI(ProviderName, "products/#", product);
    }


    @Override
    public boolean onCreate() {
        contract = new ProductContract.DetailsEntity();
        details = new ProductsDB.ProductsDetails(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings,
                        @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        db = details.getReadableDatabase();
        Cursor cursor;
        int urMatch = URI_MATCHER.match(uri);
        switch (urMatch) {
            case product:
                s = contract.RecordID + "=?";
                cursor = db.query(contract.table_Name, strings, s, strings1, null,null, s1);

                break;
            case products:
                cursor = db.query(contract.table_Name, strings, s, strings1, null,null, s1);
                break;
            default:
                throw new IllegalArgumentException("Not found" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int urMatch = URI_MATCHER.match(uri);
        switch (urMatch) {
            case product:
                return CONTENT_ITEM_TYPE;
            case products:
                return CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException("Not found" + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        db = details.getWritableDatabase();
        Long rowID = db.insert(contract.table_Name, null, contentValues);
        if (rowID > 0) {
            Uri uri1 = ContentUris.withAppendedId(contentUri, rowID);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;

        }
        throw new SQLException("failed to insert record" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted;
        int urMatch = URI_MATCHER.match(uri);

        switch (urMatch) {
            case product:
                selection = contract.RecordID + "=?";
                deleted = db.delete(contract.table_Name, selection, selectionArgs);
                break;
            case products:
                deleted = db.delete(contract.table_Name, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Not deleted" + uri);
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int Updated;
        int urMatch = URI_MATCHER.match(uri);
        switch (urMatch) {
            case product:
                selection = contract.RecordID + "=?";
                Updated = db.update(contract.table_Name, values, selection, selectionArgs);
                break;
            case products:
                Updated = db.update(contract.table_Name, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Not Updated" + uri);
        }
        if (Updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Updated;
    }

}
