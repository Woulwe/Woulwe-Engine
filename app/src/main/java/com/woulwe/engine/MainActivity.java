package com.woulwe.engine;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.TextView;

public final class MainActivity extends Activity {
    static { System.loadLibrary("woulwe_android"); }
    private static native void nativeCreate();
    private static native void nativeDestroy();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        nativeCreate();
        TextView view = new TextView(this);
        view.setText("Woulwe Engine\nNative C++ core initialized");
        view.setTextColor(Color.WHITE);
        view.setTextSize(24);
        view.setGravity(17);
        view.setBackgroundColor(Color.rgb(18,22,32));
        setContentView(view);
    }
    @Override protected void onDestroy() {
        nativeDestroy();
        super.onDestroy();
    }
}
