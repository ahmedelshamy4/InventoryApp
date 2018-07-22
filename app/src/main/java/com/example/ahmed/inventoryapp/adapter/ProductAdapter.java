package com.example.ahmed.inventoryapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmed.inventoryapp.ProductData.ProductContract;
import com.example.ahmed.inventoryapp.R;
import com.example.ahmed.inventoryapp.showActivity.DetailsActivity;
import com.example.ahmed.inventoryapp.showActivity.MainActivity;

import java.io.ByteArrayInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends CursorAdapter implements View.OnClickListener {

    @BindView(R.id.title_text)
    TextView title;

    @BindView(R.id.price_text)
    TextView price;

    @BindView(R.id.quantity_text)
    TextView quantity;

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.delete_image)
    ImageView deleteImage;

    Context context;
    MainActivity mainActivity;

    public ProductAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        //load data from cursor->
        final int proId = cursor.getInt(cursor.getColumnIndex(ProductContract.DetailsEntity.RecordID));
        final int proPrice = cursor.getInt(cursor.getColumnIndex(ProductContract.DetailsEntity.ProductPrice));
        final int proQuantity = cursor.getInt(cursor.getColumnIndex(ProductContract.DetailsEntity.Quantity));
        final String proTitle = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.DetailsEntity.ProductTitle));
        final String proSupplier = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.DetailsEntity.Supplier));
        final String proSupplierEmail = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.DetailsEntity.SupplierEmail));

        final byte[] imgByte = cursor.getBlob(cursor.getColumnIndex(ProductContract.DetailsEntity.Picture));
        if (imgByte != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imgByte);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(bitmap);
        }
        title.setText(proTitle);
        quantity.setText("Quantity" + proQuantity);
        price.setText("Price" + proPrice);

        //-- for details activity
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainActivity.isInAction) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        //-- for delete action mode
        if (!mainActivity.isInAction) {
            deleteImage.setVisibility(View.GONE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
        }
        view.setOnLongClickListener((View.OnLongClickListener) mainActivity);
        deleteImage.setOnClickListener(this);
        deleteImage.setTag(R.string.item_id, proId);

    }

    @Override
    public void onClick(View view) {
        int ID = (int) view.getTag(R.string.item_id);
        mainActivity.deleteProducts(ID);
    }
}
