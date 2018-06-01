package com.samanthacontreras.samm.doz;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static com.samanthacontreras.samm.doz.ProductsFragment.productDetail;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscountDialogFragment extends DialogFragment {

    TextView discount;
    private OnCompleteListener mListener;

    public DiscountDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog;
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.discount_form, null);
        final EditText discount = (EditText) mView.findViewById(R.id.txtDiscount);
        setupConnectionFactory();
        publishToAMQP();

        builder.setView(mView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!discount.getText().toString().matches("")){
                            publishMessage("{\"product\":" + productDetail.getId() + ", \"discount\":" + discount.getText().toString() + "}");
                            mListener.onComplete(discount.getText().toString()); // Sends discount back to SingleProduct
                        }
                        else {
                            Toast.makeText(getActivity(), "Not valid discount, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DiscountDialogFragment.this.getDialog().cancel();
                    }
                });

        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        return dialog;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static DiscountDialogFragment newInstance(int title){
        DiscountDialogFragment discountDialogFragment = new DiscountDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        discountDialogFragment.setArguments(args);
        return discountDialogFragment;
    }

    Thread publishThread;
    ConnectionFactory factory = new ConnectionFactory();
    private void setupConnectionFactory(){
        String uri = "amqp://ibawrdgw:CvGZrUCmBL8mfTz0PY4MmPHUKDhJFfno@emu.rmq.cloudamqp.com/ibawrdgw";
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
        } catch(KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1){
            e1.printStackTrace();
        }
    }

    private BlockingDeque queue = new LinkedBlockingDeque();
    void publishMessage(String message) {
        try {
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            String message = (String) queue.takeFirst();
                            try{
                                ch.basicPublish("product_discount", "", null, message.getBytes());
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e){
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        publishThread.interrupt();
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }
}
