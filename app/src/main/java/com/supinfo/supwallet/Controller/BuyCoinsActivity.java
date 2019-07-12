package com.supinfo.supwallet.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.supinfo.supwallet.Model.transactionHelpers.TransactionOperations;
import com.supinfo.supwallet.R;

public class BuyCoinsActivity extends BaseActivity {

    TextView availableCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        generateDummyGraph();
        availableCoins = findViewById(R.id.available_coins_text);
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
        showProgressDialog("Sending Request to Get some coins!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAvailableCoins();
    }
}
