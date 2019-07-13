package com.supinfo.supwallet.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.transactionHelpers.TransactionOperations;
import com.supinfo.supwallet.R;

import java.math.BigDecimal;

public class BuyCoinsActivity extends BaseActivity {

    TextView availableCoins;
    EditText amountToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        generateDummyGraph();
        availableCoins = findViewById(R.id.available_coins_text);
        amountToBuy = findViewById(R.id.amount_to_buy_edit_text);
        setAvailableCoins();
    }

    private void setAvailableCoins() {
        TransactionOperations.getAvailableNodeCoins((response, error) -> {
            if(error == null){
                availableCoins.setText(response.toString());
            }else{
                availableCoins.setText(getString(R.string.nil));
            }
        });
    }

    private void generateDummyGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, 1),
                new DataPoint(2, 5),
                new DataPoint(3, 3),
                new DataPoint(4, 2),
                new DataPoint(5, 6)
        });
        graph.addSeries(series);
    }

    public void back_button_clicked(View view) {
        finish();
    }

    public void change_node_button_clicked(View view) {
        startActivity(new Intent(BuyCoinsActivity.this,NetworkSettingsActivity.class));
    }

    public void buy_coins_button_clicked(View view) {
        try{
            BigDecimal _coins =new BigDecimal(amountToBuy.getText().toString());
            showProgressDialog("Sending Request to Get some coins!");
            TransactionOperations.buyCoins(_coins, (response, error) -> {
                if(error == null){
                    if(response!=null){
                        ENV.pendingIncomingTransactions.add(response);
                        Log.d("BUY-COINS", response.toString());
                        showSnack(amountToBuy,"Successfully bought some Coins, Please wait until Your transaction is validated to buy any more!");
                        setAvailableCoins();

                    }else{
                        showSnack(amountToBuy,"The node may not have enough coins, or its funds are locked!, Try Again later!");
                        setAvailableCoins();
                    }
                    hideProgressDialog();
                }else{
                    hideProgressDialog();
                    showSnack(amountToBuy,"Some error occcured while sending your request, Try again later!");
                    setAvailableCoins();

                }

            });
        }catch (Exception e){
            showSnack(amountToBuy,"Please input a valid amount of coins!");
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        setAvailableCoins();
    }
}
