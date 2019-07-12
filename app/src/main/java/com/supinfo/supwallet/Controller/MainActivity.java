package com.supinfo.supwallet.Controller;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Network.TCPMessageOperations;
import com.supinfo.supwallet.Model.Utils.AndroidStringUtil;
import com.supinfo.supwallet.R;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class MainActivity extends BaseActivity {

    private ImageButton settingsBtn;
    private RadioButton isConnectedRadio;
    private TextView publicKeyText;
    private Handler handler;
    private Runnable runnableCode;


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

    }

    private void getViews(){
        settingsBtn = findViewById(R.id.settings_button);
        isConnectedRadio = findViewById(R.id.is_connected_radio);
        publicKeyText = findViewById(R.id.public_key_textView);
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
}
