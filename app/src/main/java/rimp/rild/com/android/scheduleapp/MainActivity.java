package rimp.rild.com.android.scheduleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String FILE_NAME = "schedule_calendar_data";
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edit_field);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.hasFocus()) {
                    mEditText.setCursorVisible(true);
                }
            }
        });


        final CalendarView calendarView = (CalendarView) findViewById(R.id.carendar);
        calendarView.set(2016, 3 - 1); //setで渡す引数の月は 3　を渡したら 4月という意味になる * 0 = 1月のため
        calendarView.setSelectChangeListener(new CalendarView.SelectChangeListener() {
            @Override
            public void update() {
                mEditText.setCursorVisible(false);
                String memo = mEditText.getText().toString();
                Date date = calendarView.getSelectedDate();
                String previousDateKey = date.year +""+ date.month+ "" + date.day;
                Log.d("MainActivity", previousDateKey);
                save(previousDateKey, memo);
            }

            @Override
            public void setStringData(Date date) {
                mEditText.setCursorVisible(false);

                String newDateKey = date.year +""+ date.month + "" + date.day;
                Log.d("MainActivity", newDateKey);
                String memo = load(newDateKey);
                mEditText.setText(memo);
            }
        });


    }

    private void save(String key, String data) {
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, data);
        editor.apply();
    }

    private String load(String key) {
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String data = prefs.getString(key, "");
        return data;
    }
}
