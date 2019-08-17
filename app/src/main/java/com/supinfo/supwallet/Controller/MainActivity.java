package com.supinfo.supwallet.Controller;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.supinfo.shared.transaction.TransactionOutput;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Network.TCPMessageOperations;
import com.supinfo.supwallet.Model.Utils.AndroidStringUtil;
import com.supinfo.supwallet.Model.transactionHelpers.TransactionOperations;
import com.supinfo.supwallet.R;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class MainActivity extends BaseActivity {

    private ImageButton settingsBtn;
    private RadioButton isConnectedRadio;
    private TextView publicKeyText;
    private TextView balanceText;
    private Handler handler;
    private Runnable runnableCode;
    private EditText amountEditText;
    private EditText sendToEditText;
    private Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                setConnectedStatus();
                setUTXOS();
                handler.postDelayed(this, 20000);
            }
        };

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_checked},new int[]{android.R.attr.state_checked}},
                new int[]{RED,GREEN}
        );
        isConnectedRadio.setButtonTintList(colorStateList);
        isConnectedRadio.setChecked(false);
        publicKeyText.setText(AndroidStringUtil.getStringFromKey(ENV.wallet.getPublic()));

        sendButton = findViewById(R.id.send_button);

    }

    private void getViews(){
        settingsBtn = findViewById(R.id.settings_button);
        isConnectedRadio = findViewById(R.id.is_connected_radio);
        publicKeyText = findViewById(R.id.public_key_textView);
        balanceText = findViewById(R.id.balance_textView);
        sendToEditText =findViewById(R.id.send_to_edit_text);
        amountEditText = findViewById(R.id.amount_edit_text);

    }



    public void settings_button_clicked(View view) {
        ImageView animationTarget =  this.findViewById(R.id.settings_button);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point_single);
        animationTarget.startAnimation(animation);

        //startActivity(new Intent(MainActivity.this,NetworkSettingsActivity.class));
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, settingsBtn);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.settings_popup, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.network_popup_item:
                    startActivity(new Intent(MainActivity.this,NetworkSettingsActivity.class));
                    return true;
                case R.id.backup_popup_item:
                    return true;
                default:
                    return false;
            }
        });

        popup.show(); //showing popup menu
    }

    public void buy_button_clicked(View view) {
        startActivity(new Intent(MainActivity.this,BuyCoinsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnableCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnableCode);
    }

    private void setUTXOS(){
        TCPMessageOperations.getUTXOS((response, error) -> {
            if(error == null){
                ENV.userUTXOs = (ArrayList<TransactionOutput>) response.clone();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BigDecimal sum = new BigDecimal(0);
                        for (TransactionOutput tout:response) {
                            sum = sum.add(tout.value);
                        }
                        balanceText.setText(sum.toString());
                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        balanceText.setText("Balance Unavailable");
                    }
                });
            }
        });
    }

    private void setConnectedStatus(){
        TCPMessageOperations.getLatency(ENV.connectedIp, (response, error) -> {
            if(error == null){
                runOnUiThread(() -> isConnectedRadio.setChecked(true));
            }else{
                runOnUiThread(() -> isConnectedRadio.setChecked(false));
            }
        });TCPMessageOperations.getLatency(ENV.connectedIp, (response, error) -> {
            if(error == null){
                runOnUiThread(() -> isConnectedRadio.setChecked(true));
            }else{
                runOnUiThread(() -> isConnectedRadio.setChecked(false));
            }
        });
    }

    public void send_coins_clicked(View view) {
        //(sendToEditText!=null&&amountEditText!=null)&&
        if((sendToEditText.getEditableText().toString().isEmpty() || amountEditText.getEditableText().toString().isEmpty())){
            showSnack(view,"Please input valid publicKey and coin amount!");

        }else{
            PublicKey recipientKey = null;
            BigDecimal amount = new BigDecimal(0);
            try{
                recipientKey = AndroidStringUtil.getPublicKeyFromString(sendToEditText.getEditableText().toString());
                amount = new BigDecimal(amountEditText.getEditableText().toString());

            }catch (Exception e){
                showSnack(view,"Please input valid publicKey and coin amount!\"!");
            }
            if(recipientKey != null){
                TransactionOperations.sendCoins(amount, recipientKey, (response, error) -> showSnack(view,response));
            }



        }
    }
}
