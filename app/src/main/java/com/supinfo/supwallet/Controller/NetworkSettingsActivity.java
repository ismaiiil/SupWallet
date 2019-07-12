package com.supinfo.supwallet.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.supinfo.shared.Utils.StringUtil;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Network.TCPMessageOperations;
import com.supinfo.supwallet.Presenter.Adapters.MyRecyclerViewAdapter;
import com.supinfo.supwallet.R;

import java.util.ArrayList;
import java.util.Locale;

public class NetworkSettingsActivity extends BaseActivity {

    MyRecyclerViewAdapter adapter;
    EditText connectIP;
    TextView connectedToIpTextView;
    TextView latencyText;
    RecyclerView recyclerView;
    TextView bootnodeTextview;
    TextView bootnodeLatencyTextView;
    Boolean shouldAnimateReload = true;
    TextView emptyView;
    RelativeLayout bootnodeLayout;
    Button connectButton;
    ImageButton reloadButton;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_settings);

        connectIP = findViewById(R.id.connect_edit_text);
        latencyText = findViewById(R.id.latency_text_view);
        recyclerView = findViewById(R.id.ip_list_recycler_view);
        bootnodeTextview = findViewById(R.id.ip_address_column);
        bootnodeLatencyTextView = findViewById(R.id.latency_column);
        connectedToIpTextView = findViewById(R.id.ip_connected_text_view);
        emptyView = findViewById(R.id.empty_view);
        bootnodeLayout = findViewById(R.id.row_parent);
        connectButton = findViewById(R.id.connect_button);
        reloadButton = findViewById(R.id.reload_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        //setup refresh layout
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshRecyclerView(connectedToIpTextView.getText().toString());
            reloadButton.setClickable(false);
            connectButton.setEnabled(false);
        });

        // set up the RecyclerView

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        MyRecyclerViewAdapter newAdapter = new MyRecyclerViewAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(newAdapter);

        refreshWholeView();

        bootnodeLayout.setOnClickListener(v -> {
            //showSnack(v,"TEST");
            connectIP.setText(bootnodeTextview.getText());
        });



    }

    public void refreshWholeView(){
        startAnimateReload();
        //set the bootnode text to bootnode value, ping the bootnode to get the ping value
        bootnodeTextview.setText(ENV.defaultBootNode);
        bootnodeLatencyTextView.setText(R.string.waiting);

        TCPMessageOperations.getLatency(ENV.defaultBootNode, (response, error) -> {
            if(error == null){
                runOnUiThread(() -> bootnodeLatencyTextView.setText(response.toString() + " ms"));
            }else{
                runOnUiThread(() -> bootnodeLatencyTextView.setText("Unreachable"));
            }
        });

        //set the connected to text to not available, and ping to unreachable
        connectedToIpTextView.setText(getString(R.string.waiting));
        latencyText.setText(R.string.waiting);

        //get the stored connectIp, if it is null get the default bootnode which is Env.defaultbootnode, set connected to textview to the ip
        SharedPreferences ref = getApplicationContext().getSharedPreferences(ENV.GROUP_PREF, Context.MODE_PRIVATE);
        String storedIP = ref.getString(ENV.PREF_CONNECTED_IP, ENV.defaultBootNode);
        connectedToIpTextView.setText(storedIP);
        ENV.connectedIp = storedIP;

        TCPMessageOperations.getLatency(storedIP, (response, error) -> {
            if (error == null) {
                runOnUiThread (() -> latencyText.setText(response.toString() + " ms"));
            }else{
                runOnUiThread (() -> latencyText.setText(R.string.unreachable));
            }

        });
        //set it in the textview, refresh the recycler view with that bootnode
        refreshRecyclerView(storedIP);
    }

    public void back_button_clicked(View view) {
        finish();
    }

    public void connect_button_clicked(View view) {
        String ipaddress = connectIP.getText().toString();

        //check ipaddress, make sure its not empty if its an invalid ip, show error correspondingly
        if(!StringUtil.isValidIP(ipaddress)){
            showSnack(view,"Please input a proper Ip address");
        }else{

            new AlertDialog.Builder(this)
                    .setTitle("Warning!")
                    .setMessage("Do you really want to try connecting to this node? (advised only for advanced users, try reverting to the bootnode if this one fails)")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        //if its right prompt user confirmation for connection
                        SharedPreferences ref = getApplicationContext().getSharedPreferences(ENV.GROUP_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = ref.edit();
                        editor.putString(ENV.PREF_CONNECTED_IP, ipaddress);
                        editor.apply();
                        connectedToIpTextView.setText(ipaddress);
                        ENV.connectedIp = ipaddress;
                        latencyText.setText(R.string.waiting);
                        startAnimateReload();
                        TCPMessageOperations.getLatency(ipaddress, (response,error) -> {
                            if (error == null) {
                                runOnUiThread(() -> {
                                    latencyText.setText(String.format(Locale.getDefault(), "%d", response));
                                    refreshRecyclerView(ipaddress);
                                });


                            }else{
                                runOnUiThread(() -> {
                                    latencyText.setText(getString(R.string.unreachable));
                                    clearAnimateReload();
                                    showSnack(view,"Error occurred while connecting to the node");
                                });

                            }
                        });


                    })
                    .setNegativeButton(android.R.string.no, null).show();


        }


    }

    private void refreshRecyclerView(String ipaddress) {

        TCPMessageOperations.getIPLatencyList(ipaddress, (response, error) ->{
            if (error == null) {
                runOnUiThread(() -> {
                    clearAnimateReload();
                    MyRecyclerViewAdapter newAdapter = new MyRecyclerViewAdapter(this, response);
                    newAdapter.setClickListener((view, position) -> {
                        connectIP.setText(newAdapter.getItem(position).getIp());
                    });
                    recyclerView.setAdapter(newAdapter);
                });
            }else{
                runOnUiThread(() -> {
                    clearAnimateReload();
                    showSnack(recyclerView,"An error occurred while reaching the node " + ipaddress);
                });

            }

            if(response == null || response.size() < 1){
                runOnUiThread(() -> emptyView.setVisibility(View.VISIBLE));
            }else{
                runOnUiThread(() ->emptyView.setVisibility(View.INVISIBLE));
            }


        });
    }

    public void reload_button_clicked(View view) {
        refreshWholeView();
    }

    public void startAnimateReload(){
        //connectButton.setClickable(false);
        swipeRefreshLayout.setRefreshing(true);
        connectButton.setEnabled(false);
        reloadButton.setClickable(false);
        shouldAnimateReload = true;
        ImageView animationTarget =  this.findViewById(R.id.reload_button);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reload_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(shouldAnimateReload){
                animationTarget.startAnimation(animation);
                }else{
                    shouldAnimateReload = true;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationTarget.startAnimation(animation);
    }

    public void clearAnimateReload(){
        ImageView animationTarget =  this.findViewById(R.id.reload_button);
        shouldAnimateReload = false;
        swipeRefreshLayout.setRefreshing(false);
        reloadButton.setClickable(true);
        connectButton.setEnabled(true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
