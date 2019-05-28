package com.example.supwallet.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.supwallet.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();

    }

    private void getViews(){
        settingsBtn = findViewById(R.id.settings_button);
    }



    public void settings_button_clicked(View view) {
        ImageView animationTarget =  this.findViewById(R.id.settings_button);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point_single);
        animationTarget.startAnimation(animation);

        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }
}
