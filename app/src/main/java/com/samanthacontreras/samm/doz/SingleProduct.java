package com.samanthacontreras.samm.doz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.samanthacontreras.samm.doz.ProductsFragment.productDetail;
import static com.samanthacontreras.samm.doz.ProductsFragment.products;

public class SingleProduct extends AppCompatActivity implements DiscountDialogFragment.OnCompleteListener {

    ImageView productImage;
    TextView productName, productPrice, productOldPrice;
    Float productUpdatedPrice;
    Button btnDiscount;
    public static TextView productDiscount;
    public static int productPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);
        productPosition = getIntent().getIntExtra("PRODUCT_POSITION", 0);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        findViewById(R.id.logo).setVisibility(View.GONE);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(productDetail.getName().toUpperCase());
        ab.setDisplayHomeAsUpEnabled(true);



        // Views
        productImage = (ImageView) findViewById(R.id.productImage);
        productName = (TextView) findViewById(R.id.productName);
        productPrice = (TextView) findViewById(R.id.productPrice);
        productOldPrice = (TextView) findViewById(R.id.productOldPrice);
        productDiscount = (TextView) findViewById(R.id.productDiscount);
        btnDiscount = (Button) findViewById(R.id.btnDiscount);

        productUpdatedPrice = productDetail.getPrice() - (productDetail.getPrice() * (productDetail.getDiscount()/100));

        // Set product data
        Picasso.with(this)
                .load(productDetail.getUrl()).fit().centerCrop().noFade().into(productImage);

        productName.setText(productDetail.getName().toUpperCase());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.format(productUpdatedPrice);

        changeDiscountButton(productDetail.getDiscount());
        setupPubButton();

    }

      void setupPubButton() {
        Button button = (Button) findViewById(R.id.btnDiscount);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DiscountDialogFragment discountDialog = DiscountDialogFragment.newInstance(1);
                discountDialog.show(getFragmentManager(), "dialog");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(String time) {
        // Gets result from discount dialog and sets it
        productDetail.setDiscount(Float.parseFloat(time));
        changeDiscountButton(productDetail.getDiscount());
        products.get(productPosition).setDiscount(Float.parseFloat(time));
        Intent resultIntent = new Intent();
        resultIntent.putExtra("reloadListView", true);
        setResult(Activity.RESULT_OK, resultIntent);
        // Also add old price and new price
    }

    private void changeDiscountButton(float discount) {
        if(discount!=0) { // If product has discount
            productOldPrice.setVisibility(View.VISIBLE);
            productDiscount.setTextColor(getResources().getColor(R.color.darkRed));
            productDiscount.setText("-" + productDetail.getDiscount() + "% discount");

            productUpdatedPrice = productDetail.getPrice() - (productDetail.getPrice() * (productDetail.getDiscount()/100));
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            productPrice.setText("€" + df.format(productUpdatedPrice));

            productOldPrice.setText("€" + Float.toString(productDetail.getPrice()));
            productOldPrice.setPaintFlags(productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            btnDiscount.setBackgroundColor(getResources().getColor(R.color.darkRed));
            btnDiscount.setText("Update discount");
        }
        else {
            productDiscount.setText("0% discount");
            productDiscount.setTextColor(getResources().getColor(R.color.holoGreen));

            productOldPrice.setVisibility(View.GONE);
            productPrice.setText("€" + Float.toString(productDetail.getPrice()));
            btnDiscount.setBackgroundColor(getResources().getColor(R.color.holoGreen));
            btnDiscount.setText("Set discount");
        }
    }
}
