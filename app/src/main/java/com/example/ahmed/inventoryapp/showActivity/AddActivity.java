package com.example.ahmed.inventoryapp.showActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahmed.inventoryapp.ProductData.ProductContract;
import com.example.ahmed.inventoryapp.ProductData.ProductsProvider;
import com.example.ahmed.inventoryapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.image_view)
    ImageView image;

    @BindView(R.id.supplier_email)
    EditText email;

    @BindView(R.id.product_title)
    EditText title;

    @BindView(R.id.product_price)
    EditText price;

    @BindView(R.id.product_quantity)
    EditText quantity;

    @BindView(R.id.product_supplier)
    EditText supplier;

    AlertDialog.Builder builder;
    CharSequence way[];
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    Bitmap b;


    String proTitle;
    int proPrice;
    int proQuantity;
    String proSupplier;
    String proEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("New Product");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
    }

    @OnClick(R.id.image_view)
    public void ImageAction(View v) {
        way = new CharSequence[]{"camera", "gallery"};
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select one.");
        builder.setItems(way, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_submit:
                if (validation()) {
                    insert();
                }
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean validation() {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Product Title", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proTitle = title.getText().toString();
        }
        if (price.getText().toString().isEmpty()||(Integer.parseInt(price.getText().toString()))<=0) {
            Toast.makeText(getApplicationContext(), "Please Check Product Price", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proPrice = Integer.parseInt(price.getText().toString());
        }
        if (quantity.getText().toString().isEmpty()||(Integer.parseInt(quantity.getText().toString()))<=0) {
            Toast.makeText(getApplicationContext(), "Please Check Product Quantity", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proQuantity = Integer.parseInt(quantity.getText().toString());
        }
        if (supplier.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Product Supplier", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proSupplier = quantity.getText().toString();
        }
        if (!emailValidation()) {
            return false;
        }
        return true;
    }
    private boolean emailValidation() {
        String Stringemail = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (Stringemail.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Supplier Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Stringemail.matches(emailPattern)) {
            proEmail = Stringemail;
            return true;
        }
        Toast.makeText(getApplicationContext(), "Incorrect Supplier Email", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap b1;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            b = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(b);
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            image.setImageURI(uri);
            try {
                b1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                b = scaleDown(b1, 500, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public void insert() {
        ContentValues values = new ContentValues();
        values.put(ProductContract.DetailsEntity.ProductTitle, proTitle);
        values.put(ProductContract.DetailsEntity.ProductPrice, proPrice);
        values.put(ProductContract.DetailsEntity.Quantity, proQuantity);
        values.put(ProductContract.DetailsEntity.Supplier, proSupplier);
        values.put(ProductContract.DetailsEntity.SupplierEmail, proEmail);
        if (b != null)
            values.put(ProductContract.DetailsEntity.Picture, getBitmapAsByteArray(b));

        getContentResolver().insert(new ProductsProvider().contentUri, values);
        builder.setTitle("Product inserted")
                .setMessage("Are you want to add again ??")
                .setIcon(R.mipmap.confirm)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearActivity();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    private void clearActivity() {
        email.setText("");
        title.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        image.setImageDrawable(getResources().getDrawable(R.mipmap.pic));
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
