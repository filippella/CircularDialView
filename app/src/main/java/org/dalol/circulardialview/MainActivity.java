package org.dalol.circulardialview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.dalol.oldcirculardialview.OldCircularDialView;

public class MainActivity extends AppCompatActivity {

    private OldCircularDialView dialView;
    private TextView numba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numba = (TextView) findViewById(R.id.numba);
        dialView = (OldCircularDialView) findViewById(R.id.dialView);

        dialView.setListener(new OldCircularDialView.OnDialTouchListener() {
            @Override
            public void onTouch(String number) {
                numba.setText(number);
            }
        });
    }
}
