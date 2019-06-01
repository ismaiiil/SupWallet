package com.supinfo.supwallet.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supinfo.supwallet.Model.Network.TCPMessageOperations;
import com.supinfo.supwallet.Presenter.Adapters.IpRowModel;
import com.supinfo.supwallet.Presenter.Adapters.MyRecyclerViewAdapter;
import com.supinfo.supwallet.R;

import java.util.ArrayList;
import java.util.Locale;

public class NetworkSettingsActivity extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    EditText connectIP;
    TextView latencyText;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_settings);

        connectIP = findViewById(R.id.connect_edit_text);
        latencyText = findViewById(R.id.latency_text_view);
        recyclerView = findViewById(R.id.ip_list_recycler_view);

        // data to populate the RecyclerView with
        ArrayList<IpRowModel> animalNames = new ArrayList<>();
        animalNames.add(new IpRowModel("18.139.62.95",10));
        animalNames.add(new IpRowModel("176.22.147.65",10));
        animalNames.add(new IpRowModel("176.2.147.65",10));
        animalNames.add(new IpRowModel("176.4.147.65",10));
        animalNames.add(new IpRowModel("176.5.147.65",10));

        // set up the RecyclerView

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyRecyclerViewAdapter(this, animalNames);
        adapter.setClickListener((view, position) -> {
            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

    }

    public void back_button_network_clicked(View view) {
        finish();
    }

    public void connect_button_clicked(View view) {
        String ipaddress = connectIP.getText().toString();
        TCPMessageOperations.getLatency(ipaddress, (response,error) -> {
            if (error == null) {
                runOnUiThread(() -> {
                    latencyText.setText(String.format(Locale.getDefault(),"%d",response));
                    runOnUiThread(() -> refreshRecyclerView(ipaddress));
                });

            }else{
                runOnUiThread(() -> latencyText.setText(getString(R.string.unreachable)));
                //TODO GOTA FIX DIS
                runOnUiThread(() -> refreshRecyclerView(ipaddress));
            }



        });
    }

    private void refreshRecyclerView(String ipaddress) {


        ImageView animationTarget =  this.findViewById(R.id.reload_button);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reload_animation);
        animationTarget.startAnimation(animation);

        TCPMessageOperations.getIPLatencyList(ipaddress, (response, error) ->{
            if (error == null) {
                runOnUiThread(() -> {

                    MyRecyclerViewAdapter newAdapter = new MyRecyclerViewAdapter(this, response);
                    recyclerView.setAdapter(newAdapter);
                    animationTarget.clearAnimation();

                });
            }else{
                runOnUiThread(animationTarget::clearAnimation);

            }
        });
    }

    public void reload_button_clicked(View view) {



    }
}
