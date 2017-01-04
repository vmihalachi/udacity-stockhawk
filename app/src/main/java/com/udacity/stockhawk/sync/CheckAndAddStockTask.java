package com.udacity.stockhawk.sync;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class CheckAndAddStockTask extends AsyncTask<Void, Void, Stock> {

    private WeakReference<MainActivity> mainActivity;

    private String mSymbol;
    private static final String TAG = CheckAndAddStockTask.class.getName();

    public CheckAndAddStockTask(MainActivity mainActivity, String symbol) {
        this.mainActivity = new WeakReference<>(mainActivity);
        this.mSymbol = symbol;
    }

    @Override
    protected Stock doInBackground(Void... params) {
        Stock stock = null;
        try {
            stock = YahooFinance.get(mSymbol);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return stock;

    }

    @Override
    protected void onPostExecute(Stock stock) {
        super.onPostExecute(stock);
        if (stock != null && stock.getQuote() != null && stock.getQuote().getPrice() != null && mainActivity.get() != null) {
            PrefUtils.addStock(mainActivity.get(), mSymbol);
            QuoteSyncJob.syncImmediately(mainActivity.get());

            Log.d(TAG, "The stock exists");
            Log.d(TAG, stock.toString());
        } else if (mainActivity.get() != null){
            mainActivity.get().swipeRefreshLayout.setRefreshing(false);
           Toast.makeText(mainActivity.get(), R.string.problem_retrieving_stock_data, Toast.LENGTH_LONG).show();
        }
    }
}
