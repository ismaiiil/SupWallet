package com.example.supwallet.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supwallet.Model.Network.Models.TCPMessage;
import com.example.supwallet.Model.Network.Models.TCPMessageType;
import com.example.supwallet.Model.Network.TCPMessageEmmiter;
import com.example.supwallet.Presenter.Adapters.MyRecyclerViewAdapter;
import com.example.supwallet.R;

import java.util.ArrayList;

public class NetworkSettingsActivity extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    EditText connectIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_settings);

        connectIP = findViewById(R.id.connect_edit_text);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("176.152.147.65");
        animalNames.add("154.357.157.29");
        animalNames.add("46.154.5.454");
        animalNames.add("25.255.15.14");
        animalNames.add("44.544.48.4");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.ip_list_recycler_view);
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
        TCPMessageEmmiter tcpMessageEmmiter = new TCPMessageEmmiter(
                new TCPMessage<>(TCPMessageType.WALLET_CONNECT,false,0,null),connectIP.getText().toString(),8888,3000);
        tcpMessageEmmiter.start();
    }
}
