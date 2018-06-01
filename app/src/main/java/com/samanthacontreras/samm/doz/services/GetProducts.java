package com.samanthacontreras.samm.doz.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.samanthacontreras.samm.doz.Product;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class GetProducts extends Service {
    Timer timer = new Timer();
    MyTimerTask timerTask;
    ResultReceiver resultReceiver;
    int category;
    Intent i;


    public GetProducts() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        i = intent;
        resultReceiver = intent.getParcelableExtra("receiver");
        category = intent.getIntExtra("category", 2);
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 6000000);
        return  START_STICKY;
    }


    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        GetProducts getService() {
            return GetProducts.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyTimerTask extends TimerTask {
        public  Gson gson = new Gson();

        public ArrayList<Product> lista = new ArrayList<>();
        Bundle b;

        Product product;

        public MyTimerTask(){
            b = new Bundle();
        }

        @Override
        public void run() {
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

            loadProducts(b);

        }
    }

    private void loadProducts(final Bundle b){
        SyncHttpClient client = new SyncHttpClient();
        category = i.getIntExtra("category", 2);
        client.get("http://doz-api.herokuapp.com/categories/" + category + "/products?per_page=500", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String products = new String(responseBody);
                if(products!=null){
                    b.putString("products", products);
                    resultReceiver.send(200, b);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
