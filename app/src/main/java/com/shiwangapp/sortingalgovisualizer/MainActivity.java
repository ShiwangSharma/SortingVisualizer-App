package com.shiwangapp.sortingalgovisualizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    Button btnReset, btnAlgo;
    FloatingActionButton btnStart;
    BarChart barChart;

    String selectedAlgorithm;

    private ArrayList<Integer> data;
    private ArrayList<Boolean> isSorted;

    TextView sortingTV;
    int DELAY_TIME;
    private Spinner spinnerAlgorithm;
    private SeekBar seekBar;
    private String[] algorithmNames;
    private SortTask sortTask;
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DELAY_TIME = 500;
        barChart = findViewById(R.id.chart);
        sortingTV = findViewById(R.id.sortingTV);
        btnReset = findViewById(R.id.btnReset);
        btnAlgo = findViewById(R.id.btnAlgo);
        btnStart = findViewById(R.id.btnStart);

        sortingTV.setText(R.string.select_an_algorithm);

        randomizeArray();

        algorithmNames = getResources().getStringArray(R.array.algorithm_names);

        btnStart.setOnClickListener(v -> {
            if (sortingTV.getText().equals(getString(R.string.select_an_algorithm))) {
                Toast.makeText(this, "Please Select an Algorithm", Toast.LENGTH_SHORT).show();
            } else {
                btnStart.setEnabled(false);
                startSorting();
            }
        });
        btnAlgo.setOnClickListener(v -> {
            getAlgo();
        });
        btnReset.setOnClickListener(v -> {
            randomizeArray();
        });


    }


    private void startSorting() {


        if (executor != null) {
            executor.shutdownNow();
        }

        sortTask = new SortTask();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(sortTask);

    }


    private void randomizeArray() {

        isSorted = new ArrayList<>();
        data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(new Random().nextInt(60));
            isSorted.add(false);
        }
        setUpChart();

    }

    private void setUpChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i)));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Data Set");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);

        barChart.getLegend().setEnabled(false);
        barChart.invalidate();

    }

    private void getAlgo() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialog);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_sheet_ui, findViewById(R.id.bottomSheet1));
        bottomSheetDialog.setContentView(view);

        Button btnDone;
        TextView speedTv;
        btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> {
            sortingTV.setText(spinnerAlgorithm.getSelectedItem().toString());

            btnAlgo.setText(spinnerAlgorithm.getSelectedItem().toString());
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();

        spinnerAlgorithm = view.findViewById(R.id.spinnerAlgorithm);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, algorithmNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlgorithm.setAdapter(adapter);
        selectedAlgorithm = spinnerAlgorithm.getSelectedItem().toString();


        speedTv = view.findViewById(R.id.speedTV);

        seekBar = view.findViewById(R.id.seekBarSpeed);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DELAY_TIME = progress;
                speedTv.setText(DELAY_TIME + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private class SortTask implements Runnable {

        @Override
        public void run() {
            if (sortingTV.getText().equals(getString(R.string.bubble_sort))) {
                BubbleSort(data);
            } else if (sortingTV.getText().equals(getString(R.string.selection_sort))) {
                SelectionSort(data);
            } else if (sortingTV.getText().equals(getString(R.string.insertion_sort))) {
                InsertionSort(data);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateChart(data);
                    btnStart.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Sorted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    private class SortTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (sortingTV.getText().equals(getString(R.string.bubble_sort))) {
//                BubbleSort(data);
//            } else if (sortingTV.getText().equals(getString(R.string.selection_sort))) {
//                SelectionSort(data);
//            } else if (sortingTV.getText().equals(getString(R.string.insertion_sort))) {
//                InsertionSort(data);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            super.onPostExecute(unused);
//            updateChart(data);
//            btnStart.setEnabled(true);
//            Toast.makeText(MainActivity.this, "Sorted", Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            btnStart.setEnabled(false);
//        }
//    }


    private void BubbleSort(ArrayList<Integer> arr) {
        boolean flag;
        for (int i = 0; i < arr.size() - 1; i++) {
            flag = false;
            for (int j = 0; j < arr.size() - i - 1; j++) {


                isSorted.set(j, true);
                isSorted.set(j + 1, true);

                delay();
                int finalJ = j;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateChart(arr);
                        isSorted.set(finalJ, false);
                        isSorted.set(finalJ + 1, false);
                    }
                });


                if (arr.get(j) > arr.get(j + 1)) {

                    Collections.swap(arr, j, j + 1);

                    isSorted.set(j, true);
                    isSorted.set(j + 1, true);

                    delay();


                    // Re-update the chart with the latest data and colors on the UI thread
                    int finalJ1 = j;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateChart(arr);
                            isSorted.set(finalJ1, false);
                            isSorted.set(finalJ1 + 1, false);
                        }
                    });

                    // Reset the color of the sorted bars for the next iteration

                    flag = true;

                }
            }
            if (!flag) break;
        }
    }

    private void updateChart(ArrayList<Integer> arr) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            entries.add(new BarEntry(i, arr.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Data Set");
        dataSet.setColors(getColors(isSorted));


        dataSet.setDrawValues(false);


        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);

        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }

    private ArrayList<Integer> getColors(ArrayList<Boolean> isSorted) {
        ArrayList<Integer> colors = new ArrayList<>();
        for (boolean sorted : isSorted) {
            colors.add(sorted ? getResources().getColor(android.R.color.holo_green_light)
                    : getResources().getColor(android.R.color.holo_red_light));
        }
        return colors;
    }

    private void delay() {
        try {
            Thread.sleep(DELAY_TIME);
        } catch (Exception e) {
        }
    }

    private void InsertionSort(ArrayList<Integer> arr) {
        for (int i = 1; i < arr.size(); i++) {
            int key = arr.get(i);
            int j = i - 1;

            //isSorted.set(i, true);
            isSorted.set(j, true);

            delay();
            //int finalI = i;
            int finalJ = j;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateChart(arr);
                    //isSorted.set(finalI, false);
                    isSorted.set(finalJ, false);
                }
            });
            while (j >= 0 && arr.get(j) > key) {
                // Swap bars (arr[j] and arr[j + 1])
                int temp = arr.get(j);
                arr.set(j, arr.get(j + 1));
                arr.set(j + 1, temp);

                // Mark the swapped bars as "sorted" for visualization
                isSorted.set(i, true);
                isSorted.set(j, true);

                // Introduce delay for visualization
                delay();
                // Re-update the chart with the latest data and colors on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateChart(arr);
                    }
                });

                // Reset the color of the swapped bars for the next iteration
                isSorted.set(i, false);
                isSorted.set(j, false);

                j--;
            }
            arr.set(j + 1, key);
        }

    }

    private void SelectionSort(ArrayList<Integer> arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr.get(j) < arr.get(minIdx)) {
                    minIdx = j;
                }
            }

            int temp = arr.get(minIdx);
            arr.set(minIdx, arr.get(i));
            arr.set(i, temp);

            // Mark the sorted bar as "sorted" for visualization
            isSorted.set(i, true);

            // Introduce delay for visualization
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Re-update the chart with the latest data and colors on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateChart(arr);
                }
            });

            // Reset the color of the sorted bar for the next iteration
            isSorted.set(i, false);
        }
    }

}