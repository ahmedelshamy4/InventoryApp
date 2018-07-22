package com.example.ahmed.inventoryapp.showActivity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.inventoryapp.ProductData.ProductContract;
import com.example.ahmed.inventoryapp.ProductData.ProductsProvider;
import com.example.ahmed.inventoryapp.R;

import java.io.ByteArrayInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.tv_title)
    TextView title;

    @BindView(R.id.tv_price)
    TextView price;

    @BindView(R.id.tv_Quantity)
    TextView quantity;

    @BindView(R.id.tv_Supplier)
    TextView supplier;

    String proTitle;
    int proPrice;
    int proQuantity;
    String proSupplier;
    String proEmail;
    byte[] imgByte;

    int id;
    ProductContract.DetailsEntity contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        contract = new ProductContract.DetailsEntity();

        getLoaderManager().initLoader(6, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_edit:
                update();
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("email", proEmail);
        intent.putExtra("price", proPrice);
        intent.putExtra("quantity", proQuantity);
        intent.putExtra("supplier", proSupplier);
        intent.putExtra("title", proTitle);
        intent.putExtra("image", imgByte);
        startActivity(intent);

    }

    @OnClick(R.id.btn_decrease)
    public void decrease(View view) {
        String[] ids = new String[]{String.valueOf(id)};
        if (proQuantity > 0) {
            ContentValues values = new ContentValues();
            values.put(ProductContract.DetailsEntity.ProductTitle, proTitle);
            values.put(ProductContract.DetailsEntity.ProductPrice, proPrice);
            values.put(ProductContract.DetailsEntity.Quantity, proQuantity - 1);
            values.put(ProductContract.DetailsEntity.SupplierEmail, proEmail);
            values.put(ProductContract.DetailsEntity.Supplier, proSupplier);
            values.put(ProductContract.DetailsEntity.Picture, imgByte);
            String url = ProductsProvider.contentUri.toString() + "/1";
            getContentResolver().update(Uri.parse(url), values, null, ids);
        } else {
            Toast.makeText(this, "sorry this product not available", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_increase)
    public void increase(View view) {
        String[] ids = new String[]{String.valueOf(id)};

        ContentValues values = new ContentValues();
        values.put(ProductContract.DetailsEntity.ProductTitle, proTitle);
        values.put(ProductContract.DetailsEntity.ProductPrice, proPrice);
        values.put(ProductContract.DetailsEntity.Quantity, proQuantity +1);
        values.put(ProductContract.DetailsEntity.SupplierEmail, proEmail);
        values.put(ProductContract.DetailsEntity.Supplier, proSupplier);
        values.put(ProductContract.DetailsEntity.Picture, imgByte);
        String url = ProductsProvider.contentUri.toString() + "/1";
        getContentResolver().update(Uri.parse(url), values, null, ids);
    }

    @OnClick(R.id.btn_make_order)
    public void order(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, proEmail);
        i.putExtra(Intent.EXTRA_TEXT, " we want more from " + proTitle);
        i.putExtra(Intent.EXTRA_SUBJECT, "product order");
        startActivity(Intent.createChooser(i, "Send Email"));
    }

    @OnClick(R.id.btn_delete)
    public void delete(View view) {
        final String[] ids = new String[]{String.valueOf(id)};
        final String url = ProductsProvider.contentUri.toString() + "/1";
        int deleted = getContentResolver().delete(Uri.parse(url), null, ids);
        if (deleted > 0) {
            Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);

        } else {
            Toast.makeText(getBaseContext(), "Sorry Can't Delete this Products", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] ids = new String[]{String.valueOf(id)};
        String[] projection = {
                contract.RecordID, contract.ProductTitle,
                contract.Quantity, contract.ProductPrice, contract.Picture, contract.SupplierEmail, contract.Supplier};
        String url = ProductsProvider.contentUri.toString() + "/1";
        return new CursorLoader(this, Uri.parse(url), projection, null, ids, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToNext()) {
            proTitle = data.getString(data.getColumnIndexOrThrow(ProductContract.DetailsEntity.ProductTitle));
            proEmail = data.getString(data.getColumnIndexOrThrow(ProductContract.DetailsEntity.SupplierEmail));
            proPrice = data.getInt(data.getColumnIndex(ProductContract.DetailsEntity.ProductPrice));
            proQuantity = data.getInt(data.getColumnIndex(ProductContract.DetailsEntity.Quantity));
            proSupplier = data.getString(data.getColumnIndexOrThrow(ProductContract.DetailsEntity.Supplier));
            imgByte = data.getBlob(data.getColumnIndex(ProductContract.DetailsEntity.Picture));

            if (imgByte != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imgByte);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            }
            title.setText(proTitle);
            price.setText("Price : " + String.valueOf(proPrice) + " $");
            quantity.setText("Quantity : " + String.valueOf(proQuantity));
            supplier.setText("Supplier : " + proSupplier);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        title.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.product));
    }

}
