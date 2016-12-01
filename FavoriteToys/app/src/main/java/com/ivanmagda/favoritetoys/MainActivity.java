package com.ivanmagda.favoritetoys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mToysListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configure();
    }

    private void configure() {
        mToysListTextView = (TextView) findViewById(R.id.tv_toy_names);

        String[] names = ToyBox.getToyNames();
        for (String name : names) {
            mToysListTextView.append(name + "\n\n\n");
        }
    }
}
