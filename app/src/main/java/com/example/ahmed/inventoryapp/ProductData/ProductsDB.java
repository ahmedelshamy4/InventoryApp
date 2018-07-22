package com.example.ahmed.inventoryapp.ProductData;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductsDB {
    public static final int database_Version = 1;
    public static final String database_Name = "inventory";


    public static class ProductsDetails extends SQLiteOpenHelper {


        public ProductsDetails(Context context) {
            super(context, database_Name, null, database_Version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {

                db.execSQL(ProductContract.DetailsEntity.CreateTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            try {

                db.execSQL(ProductContract.DetailsEntity.DropTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
