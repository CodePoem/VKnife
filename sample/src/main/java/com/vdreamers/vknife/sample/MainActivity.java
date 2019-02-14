package com.vdreamers.vknife.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * MainActivity
 * <p>
 * date 2019/02/14 14:34:53
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Log", "11");
        Log.e("Log", "22");
        Log.d("Log", "baseline");

        LogUtils.d("LogUtils", "111");

        Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT);

        TestModelAttr testModel = new TestModelAttr();
        testModel.content = "1";
        testModel.contentNum = 1;
        testModel.title = "2";
        testModel.useless = 1;
        testModel.changed = true;

        testModel.setAttribute("changed", false);

        TextView textView = findViewById(R.id.tv_test);
        textView.setText(testModel.content + testModel.contentNum + testModel.title + testModel.useless + testModel.changed);
    }
}
