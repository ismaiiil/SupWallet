package com.supinfo.supwallet.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;

import com.supinfo.supwallet.R;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class MainActivity extends AppCompatActivity {

    private ImageButton settingsBtn;
    private RadioButton isConnectedRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{

                        RED
                        , GREEN,
                }
        );
        isConnectedRadio.setButtonTintList(colorStateList);
        isConnectedRadio.setChecked(false);


    }

    private void getViews(){
        settingsBtn = findViewById(R.id.settings_button);
        isConnectedRadio = findViewById(R.id.is_connected_radio);
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
}
