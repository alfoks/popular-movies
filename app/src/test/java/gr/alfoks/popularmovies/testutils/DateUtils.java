package gr.alfoks.popularmovies.testutils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


import static org.junit.Assert.assertEquals;

public class DateUtils {
    public static void assertDateEquals(Date date, int day, int month, int year) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(day, localDate.getDayOfMonth());
        assertEquals(month, localDate.getMonthValue());
        assertEquals(year, localDate.getYear());
    }
}
