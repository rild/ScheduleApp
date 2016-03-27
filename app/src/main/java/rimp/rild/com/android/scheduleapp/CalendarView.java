package rimp.rild.com.android.scheduleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rild on 16/03/27.
 */
public class CalendarView extends LinearLayout {
    @SuppressWarnings("unused")
    private static final String TAG = CalendarView.class.getSimpleName();

    private static final int WEEKDAYS = 7;
    private static final int MAX_WEEK = 6;

    private int mSelectedDay = 0;
    private int mSelectedMonth = 0;
    private int mSelectedYear = 0;

    private SelectChangeListener mSelectChangeListener;

    // 週の始まりの曜日を保持する
    private static final int BIGINNING_DAY_OF_WEEK = Calendar.SUNDAY;
    // 今日のフォント色
    private static final int TODAY_COLOR = Color.RED;
    // 通常のフォント色
    private static final int DEFAULT_COLOR = Color.DKGRAY;
    // 今週の背景色
    private static final int TODAY_BACKGROUND_COLOR = Color.LTGRAY;
    // 通常の背景色
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
//    選択状態の背景色
    private static final int SELECTED_BACKGROUND_COLOR = R.drawable.item_selected;

    private LinearLayout mTitleLayout;
    // 年月表示部分
    private TextView mTitleView;

    // 週のレイアウト
    private LinearLayout mWeekLayout;
    private ArrayList<LinearLayout> mWeeks = new ArrayList<LinearLayout>();

    /**
     * コンストラクタ
     *
     * @param context context
     */
    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * コンストラクタ
     *
     * @param context context
     * @param attrs   attributeset
     */
    @SuppressLint("SimpleDateFormat")
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(VERTICAL);

        createCalendar(context);
    }

    private void createCalendar(Context context) {
        createTitleView(context);
        createStroke(context);
        createWeekViews(context);
        createStroke(context);
        createDayViews(context);
    }

    private void createStroke(Context context) {
        View view = inflate(context, R.layout.stroke_black, null);
        addView(view);
    }

    /**
     * 年月日表示用のタイトルを生成する
     *
     * @param context context
     */
    private void createTitleView(final Context context) {
        float scaleDensity = context.getResources().getDisplayMetrics().density;

        mTitleLayout = (LinearLayout) inflate(context, R.layout.calendar_top_layout, null);

        mTitleView = (TextView) mTitleLayout.findViewById(R.id.title_textview);
        mTitleView.setGravity(Gravity.CENTER_HORIZONTAL); // 中央に表示
        mTitleView.setTextSize((int) (scaleDensity * 10));
        mTitleView.setTypeface(null, Typeface.BOLD); // 太字
        mTitleView.setPadding(0, 0, 0, (int) (scaleDensity * 16));

        ImageButton leftButton = (ImageButton) mTitleLayout.findViewById(R.id.left_button);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onLeftButtonClicked();
            }
        });
        ImageButton rightButton = (ImageButton) mTitleLayout.findViewById(R.id.right_button);
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onRightButtonClicked();
            }
        });

        addView(mTitleLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * 曜日表示用のビューを生成する
     *
     * @param context context
     */
    private void createWeekViews(Context context) {
        float scaleDensity = context.getResources().getDisplayMetrics().density;
        // 週表示レイアウト
        mWeekLayout = new LinearLayout(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK); // 週の頭をセット

        for (int i = 0; i < WEEKDAYS; i++) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.RIGHT); // 中央に表示
            textView.setPadding(0, 0, (int) (scaleDensity * 3), 0);

            LinearLayout.LayoutParams llp =
                    new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
            llp.weight = 1;

            mWeekLayout.addView(textView, llp);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        addView(mWeekLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }


    /**
     * 日付表示用のビューを生成する
     *
     * @param context context
     */
    private void createDayViews(Context context) {
        float scaleDensity = context.getResources().getDisplayMetrics().density;

        // カレンダー部 最大6行必要
        for (int i = 0; i < MAX_WEEK; i++) {
            LinearLayout weekLine = new LinearLayout(context);
            mWeeks.add(weekLine);

            // 1週間分の日付ビュー作成
            for (int j = 0; j < WEEKDAYS; j++) {
                TextView dayView = new TextView(context);
                dayView.setGravity(Gravity.TOP | Gravity.RIGHT);
                dayView.setPadding(0, (int) (scaleDensity * 4), (int) (scaleDensity * 4), 0);
                dayView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDayClicked(view);
                    }
                });
                LinearLayout.LayoutParams llp =
                        new LinearLayout.LayoutParams(0, (int) (scaleDensity * 36));
                llp.weight = 1;
                weekLine.addView(dayView, llp);
            }

            this.addView(weekLine, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }



    public void onDayClicked(View view) {
        if (view.getTag() == null) return;
        int newSelectedDay = (int) view.getTag();
        Date date = new Date(mSelectedYear, mSelectedMonth, newSelectedDay);
        mSelectChangeListener.update(); //ここでgetSelectedDate グローバルなDateはまだ更新されていない
        mSelectChangeListener.setStringData(date);

        Log.d("Calender", "onclick, selectedday:" + mSelectedDay);
        TextView textView = (TextView) findViewWithTag(mSelectedDay);
        if (mSelectedDay != 0) textView.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        mSelectedDay = newSelectedDay;
        view.setBackgroundResource(SELECTED_BACKGROUND_COLOR);
    }

    public void onLeftButtonClicked() {
        TextView textView = (TextView) findViewWithTag(mSelectedDay);
        if (textView != null) textView.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);

        mSelectedDay = 0; //月を移動した時は日付の選択は外れる

        int newSelectedMonth = mSelectedMonth - 1;
        int newSelectedYear = mSelectedYear;
        if (newSelectedMonth < 0) {
            newSelectedMonth = 11; //12月より先に行くと1月になる
            newSelectedYear = mSelectedYear - 1;
        }

        Date date = new Date(newSelectedYear, newSelectedMonth, mSelectedDay);
        mSelectChangeListener.update(); //ここでgetSelectedDate グローバルなDateはまだ更新されていない
        mSelectChangeListener.setStringData(date);

        mSelectedMonth = newSelectedMonth;
        mSelectedYear = newSelectedYear;

        setTitle(mSelectedYear, mSelectedMonth);
        setDays(mSelectedYear, mSelectedMonth);
    }

    public void onRightButtonClicked() {
        TextView textView = (TextView) findViewWithTag(mSelectedDay);
        if (textView != null) textView.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);

        mSelectedDay = 0; //月を移動した時は日付の選択は外れる

        int newSelectedMonth = mSelectedMonth + 1;
        int newSelectedYear = mSelectedYear;
        if (newSelectedMonth > 11) {
            newSelectedMonth = 0; //12月より先に行くと1月になる
            newSelectedYear = mSelectedYear + 1;
        }

        Date date = new Date(newSelectedYear, newSelectedMonth, mSelectedDay);
        mSelectChangeListener.update(); //ここでgetSelectedDate グローバルなDateはまだ更新されていない
        mSelectChangeListener.setStringData(date);

        mSelectedMonth = newSelectedMonth;
        mSelectedYear = newSelectedYear;

        setTitle(mSelectedYear, mSelectedMonth);
        setDays(mSelectedYear, mSelectedMonth);
    }

    /**
     * 年と月を指定して、カレンダーの表示を初期化する
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    public void set(int year, int month) {
        mSelectedYear = year;
        mSelectedMonth = month;
        setTitle(year, month);
        setWeeks();
        setDays(year, month);
    }

    /**
     * 指定した年月日をタイトルに設定する
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    @SuppressLint("SimpleDateFormat")
    private void setTitle(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, month);

        // 年月フォーマット文字列
        String formatString = getContext().getString(R.string.format_month_year);
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        mTitleView.setText(formatter.format(targetCalendar.getTime()));
    }

    /**
     * 曜日を設定する
     */
    @SuppressLint("SimpleDateFormat")
    private void setWeeks() {
        Calendar week = Calendar.getInstance();
        week.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK); // 週の頭をセット
        SimpleDateFormat weekFormatter = new SimpleDateFormat("E"); // 曜日を取得するフォーマッタ
        for (int i = 0; i < WEEKDAYS; i++) {
            TextView textView = (TextView) mWeekLayout.getChildAt(i);
            textView.setText(weekFormatter.format(week.getTime())); // テキストに曜日を表示
            week.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /**
     * 日付を設定していくメソッド
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    private void setDays(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, month);

        int skipCount = getSkipCount(targetCalendar);
        int lastDay = targetCalendar.getActualMaximum(Calendar.DATE);
        int dayCounter = 1;

        Calendar todayCalendar = Calendar.getInstance();
        int todayYear = todayCalendar.get(Calendar.YEAR);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < MAX_WEEK; i++) {
            LinearLayout weekLayout = mWeeks.get(i);
            weekLayout.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            for (int j = 0; j < WEEKDAYS; j++) {
                TextView dayView = (TextView) weekLayout.getChildAt(j);
                dayView.setClickable(true);

                // 第一週かつskipCountが残っていれば
                if (i == 0 && skipCount > 0) {
                    dayView.setText(" ");
                    dayView.setClickable(false);
                    dayView.setBackgroundColor(DEFAULT_BACKGROUND_COLOR); // 週の背景グレー
                    dayView.setTag(0);
                    Log.d("Calendar", "tag:" + dayView.getTag()); //タグでViewをとってこれるようにタグをつける
                    skipCount--;
                    continue;
                }

                // 最終日より大きければ
                if (lastDay < dayCounter) {
                    dayView.setText(" ");
                    dayView.setClickable(false);
                    dayView.setTag(0);
                    Log.d("Calendar", "tag:" + dayView.getTag()); //タグでViewをとってこれるようにタグをつける
                    continue;
                }

                // 日付を設定
                dayView.setText(String.valueOf(dayCounter));
                // 日付をタグとして設定
                dayView.setTag(dayCounter);
                Log.d("Calendar", "tag:" + dayView.getTag()); //タグでViewをとってこれるようにタグをつける

                boolean isToday = todayYear == year &&
                        todayMonth == month &&
                        todayDay == dayCounter;

                if (isToday) {
                    dayView.setTextColor(TODAY_COLOR); // 赤文字
                    dayView.setTypeface(null, Typeface.BOLD); // 太字
                    weekLayout.setBackgroundColor(TODAY_BACKGROUND_COLOR); // 週の背景グレー
                } else {
                    dayView.setTextColor(DEFAULT_COLOR);
                    dayView.setTypeface(null, Typeface.NORMAL);
                }
                dayCounter++;
            }
        }
    }

    /**
     * カレンダーの最初の空白の個数を求める
     *
     * @param targetCalendar 指定した月のCalendarのInstance
     * @return skipCount
     */
    private int getSkipCount(Calendar targetCalendar) {
        int skipCount; // 空白の個数
        int firstDayOfWeekOfMonth = targetCalendar.get(Calendar.DAY_OF_WEEK); // 1日の曜日
        if (BIGINNING_DAY_OF_WEEK > firstDayOfWeekOfMonth) {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK + WEEKDAYS;
        } else {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK;
        }
        return skipCount;
    }

    private Calendar getTargetCalendar(int year, int month) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.clear(); // カレンダー情報の初期化
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return targetCalendar;
    }

    public Date getSelectedDate () {
        Date date = new Date(mSelectedYear, mSelectedMonth, mSelectedDay);
        return date;
    }

    public void setSelectChangeListener (SelectChangeListener listener) {
        mSelectChangeListener = listener;
    }

    public interface SelectChangeListener {
        public void update();
        public void setStringData(Date date);
    }
}
