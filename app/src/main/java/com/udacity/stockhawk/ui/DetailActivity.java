package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utils;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_HISTORY;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = DetailActivity.class.getName();
    private static final int STOCK_LOADER = 0;
    private String mSymbol;

    @BindView(R.id.chart1)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSymbol = getIntent().getExtras().getString(Utils.EXTRA_SYMBOL);

        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        // configure the chart
        chart.setDescription(null);
        chart.setDrawBorders(false);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setDrawLabels(false);

        // load the stocks
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI.buildUpon().appendPath(mSymbol).build(),
                Contract.Quote.QUOTE_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // the data
            final String csvData = data.getString(POSITION_HISTORY);
            final List<Entry> entries = new ArrayList<>();
            final LinkedList<StockData> stockData = new LinkedList<>();

            //boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

            // Scan the data
            final Scanner scanner = new Scanner(csvData);
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(",");
                stockData.addFirst(new StockData(Float.valueOf(row[0]), Float.valueOf(row[1])));
            }
            scanner.close();

            // Add the entries
            for (StockData sd : stockData) {
                entries.add(new Entry(sd.getTime(), sd.getValue()));
            }

            // set the data set
            final LineDataSet dataSet = new LineDataSet(entries, null);
            final int color = getResources().getColor(R.color.colorAccent);
            dataSet.setColor(color);
            dataSet.setCircleColor(color);
            dataSet.setValueTextColor(color);
            dataSet.setFillColor(color);
            dataSet.setDrawFilled(true);

            // add the data to the chart
            final LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate(); // refresh
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
