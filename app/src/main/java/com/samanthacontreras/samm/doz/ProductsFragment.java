package com.samanthacontreras.samm.doz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.samanthacontreras.samm.doz.services.GetProducts;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    public static Product productDetail;
    public static ArrayList<Product> products = new ArrayList<>();
    public MyResultReceiver mReceiver;
    Intent intent;
    ListView lv;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner categoriesSpinner;
    public static MyAdapter myAdapter;
    Gson gson;

    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoriesSpinner = (Spinner) view.findViewById(R.id.categoriesSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProducts(position+2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        categoriesSpinner.setOnItemSelectedListener(spinnerListener);

        intent = new Intent(getActivity(), GetProducts.class);
        mReceiver = new MyResultReceiver(null);
        intent.putExtra("receiver", mReceiver);
        setHasOptionsMenu(true);
        intent.putExtra("category", categoriesSpinner.getSelectedItemPosition()+2);
        getActivity().startService(intent);
        myAdapter = new MyAdapter();

        lv = (ListView) view.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        lv.setAdapter(myAdapter);

        // ListView Click Listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getBaseContext(), SingleProduct.class);
                productDetail = products.get(position);
                i.putExtra("PRODUCT_POSITION", position);
                getActivity().startActivityForResult(i,1);
            }
        };
        lv.setOnItemClickListener(clickListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts(categoriesSpinner.getSelectedItemPosition()+2);
            }
        });


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println(categoriesSpinner.getSelectedItemPosition());
        outState.putInt("categoriesSpinner", categoriesSpinner.getSelectedItemPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        System.out.println("state restored");
        if(savedInstanceState != null){
            categoriesSpinner.setSelection(savedInstanceState.getInt("categoriesSpinner", 0));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_products_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                loadProducts(categoriesSpinner.getSelectedItemPosition()+2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            loadProducts(categoriesSpinner.getSelectedItemPosition());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(intent);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    // Creates new instance of products fragment
    public static ProductsFragment newInstance(){
        ProductsFragment productsFragment = new ProductsFragment();
        return productsFragment;
    }


    class UpdateUI implements Runnable {

        public UpdateUI(){
        }

        @Override
        public void run() {
            myAdapter.notifyDataSetChanged();
        }
    }

    // Service result receiver
    private class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Product>>(){

            }.getType();

            products = gson.fromJson(resultData.getString("products"), listType);
            getActivity().runOnUiThread(new UpdateUI());

        }
    }


    // List View Adapter
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            try {
                if(convertView == null){
                    listItemView = LayoutInflater.from(getActivity()).inflate(R.layout.item, parent, false);

                }
                Product p = products.get(position);

                TextView name = (TextView) listItemView.findViewById(R.id.lblName);
                TextView price = (TextView) listItemView.findViewById(R.id.price);
                TextView discount = (TextView) listItemView.findViewById(R.id.lblDiscount);

                ImageView image = (ImageView) listItemView.findViewById(R.id.image);
                name.setText(p.getName().toUpperCase());
                price.setText("€" + Float.toString(p.getPrice()));
                if(p.getDiscount()!=0.0f){
                    discount.setVisibility(View.VISIBLE);
                    discount.setText("-"+Float.toString(p.getDiscount())+"%");
                } else {
                    discount.setVisibility(View.GONE);
                }


                Picasso.with(getActivity())
                        .load(p.getUrl())
                        .resize(100,100).noFade().into(image);

            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Error " + e.getMessage());
                return null;
            }

            return listItemView;
        }
    }




    private void loadProducts(Integer category){
        if(category==null){
            category = 2;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://doz-api.herokuapp.com/categories/" + category + "/products?per_page=500", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                gson = new Gson();
                Type listType = new TypeToken<ArrayList<Product>>(){

                }.getType();

                products = gson.fromJson(result, listType);
                getActivity().runOnUiThread(new UpdateUI());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
