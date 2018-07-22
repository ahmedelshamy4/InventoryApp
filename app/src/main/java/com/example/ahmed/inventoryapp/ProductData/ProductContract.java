package com.example.ahmed.inventoryapp.ProductData;

import android.provider.BaseColumns;

public class ProductContract implements BaseColumns {

    public static class DetailsEntity {
        public static final String table_Name = "details";
        public static final String RecordID = BaseColumns._ID;

        public static final String ProductTitle = "title";
        public static final String ProductPrice = "price";
        public static final String Quantity = "quantity";
        public static final String Picture = "picture";
        public static final String Supplier = "supplier";
        public static final String SupplierEmail = "supp_email";

        public static final String DropTable = "DROP tABLE IF EXISTS " + table_Name;

        public static final String CreateTable = "CREATE TABLE "
                + table_Name
                + " (" + RecordID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductTitle + " " + "VARCHAR(255), "
                + ProductPrice + " " + "INTEGER, "
                + Quantity + " " + "INTEGER, "
                + Picture + " " + "BLOB," + " "
                + Supplier + " " + "VARCHAR(255), "
                + SupplierEmail + " " + "VARCHAR(255));";
    }
}
