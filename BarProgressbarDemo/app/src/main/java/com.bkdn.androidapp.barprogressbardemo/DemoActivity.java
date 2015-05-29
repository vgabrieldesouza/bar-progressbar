package com.bkdn.androidapp.barprogressbardemo;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final com.bkdn.androidapp.barprogressbardemo.BarProgressBar progressbar =
                (com.bkdn.androidapp.barprogressbardemo.BarProgressBar) findViewById(R.id.progressbar);

        int fgColor = Color.LTGRAY;
        int bgColor = Color.DKGRAY;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorAccent});
            fgColor = a.getColor(0, 0);
            a.recycle();
            progressbar.setColors(fgColor, bgColor);
        }


        ((CheckBox) findViewById(R.id.cb_indeterminate)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        progressbar.setIndeterminate(isChecked);
                    }
                });

        ((CheckBox) findViewById(R.id.cb_show_progress)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        progressbar.setShowProgress(isChecked);
                    }
                });

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressbar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not implemented
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not implemented
            }
        });

        final SeekBar barCount = (SeekBar) findViewById(R.id.barcount);
        barCount.setMax(40);
        barCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressbar.setBarCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not implemented
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not implemented
            }
        });
    }
}
