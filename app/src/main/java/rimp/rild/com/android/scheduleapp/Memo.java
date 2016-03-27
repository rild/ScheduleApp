package rimp.rild.com.android.scheduleapp;

/**
 * Created by rild on 16/03/27.
 */
public class Memo {
    int month;
    int day;
    String text;
    DayOfTheWeek dayOfTheWeek;

    enum DayOfTheWeek {
        Sunday,
        Monday,
        Tuesday,
        Wednsday,
        Thusday,
        Friday,
        Saturday
        ;
    }
}
