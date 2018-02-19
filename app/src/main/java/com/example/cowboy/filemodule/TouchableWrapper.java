package com.example.cowboy.filemodule;

/**
 * Created by Cowboy on 07.02.2018.
 */
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {
    private Context context;
    private GoJobSupportMapFragment fragment;

    public TouchableWrapper(Context context, GoJobSupportMapFragment fragment) {
        super(context);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (fragment.getPauseStatus()) {
                    Log.d("TOUCH", " MotionEvent.ACTION_DOWN TRUE");
                } else {
                    Log.d("TOUCH", " MotionEvent.ACTION_DOWN FALSE");
                }
                break;

            case MotionEvent.ACTION_UP:
                if (fragment.getPauseStatus()) {
                    Log.d("TOUCH", " MotionEvent.ACTION_UP TRUE");
                } else {
                    Log.d("TOUCH", " MotionEvent.ACTION_UP FALSE");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fragment.getPauseStatus()) {
                    Log.d("TOUCH", " MotionEvent.ACTION_MOVE TRUE");
                } else {
                    Log.d("TOUCH", " MotionEvent.ACTION_MOVE FALSE");

                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if (fragment.getPauseStatus()) {
                    Log.d("TOUCH", " MotionEvent.ACTION_CANCEL TRUE");
                } else {
                    Log.d("TOUCH", " MotionEvent.ACTION_CANCEL FALSE");
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}