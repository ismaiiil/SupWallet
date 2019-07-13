package com.supinfo.supwallet.Controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.supinfo.supwallet.R;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    ConstraintLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initProgressDialog();
        // Get your layout set up, this is just an example
        mainLayout = findViewById(R.id.base_activity_layout);


    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    public void showProgressDialog(String withText) {
        if(progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(withText);
            progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showSnack(View view, String text){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Log.d("KEYBOARD", "onCreate: No Keyboards to minimize!");
        }
        Snackbar.make(view,text, Snackbar.LENGTH_LONG).show();
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        hideProgressDialog();
//    }
}
