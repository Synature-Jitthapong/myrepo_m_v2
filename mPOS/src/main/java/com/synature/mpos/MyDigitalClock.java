package com.synature.mpos;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyDigitalClock extends TextView {
    private Calendar mCalendar;
    private Runnable mTicker;
    private Handler mHandler;
    private boolean mTickerStopped = false;
    private String mFormat = "d/MM/yyyy hh:mm:ss";

    public MyDigitalClock(Context context) {
        super(context);
        initClock();
    }

    public MyDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock();
    }

    private void initClock() {
    	mCalendar = Utils.getCalendar();
    }
    
    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar.getTime()));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }
}