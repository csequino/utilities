package dev.csequino.utilities;

import dev.csequino.utilities.annotations.TupleColumn;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class GenericsUtilsTest {

    public static class TestClass {
        @TupleColumn(0)
        String aString;
        @TupleColumn(1)
        Integer aInteger;
        @TupleColumn(2)
        Double aDouble;
        @TupleColumn(3)
        Long aLong;
        @TupleColumn(4)
        BigInteger aBigInteger;
        @TupleColumn(5)
        BigDecimal aBigDecimal;
        @TupleColumn(6)
        Date aDate;
        @TupleColumn(7)
        java.sql.Date aSqlDate;
        @TupleColumn(8)
        java.sql.Time aSqlTime;
        @TupleColumn(9)
        Timestamp aTimestamp;
        @TupleColumn(10)
        LocalDateTime aLocalDateTime;
        @TupleColumn(11)
        LocalDate aLocalDate;
        @TupleColumn(12)
        LocalTime aLocalTime;
        @TupleColumn(13)
        Number aNumber;
    }

    @Test
    public void fromArrayToObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LocalDate localDate = LocalDate.of(2024, 1, 1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        LocalTime localTime = localDateTime.toLocalTime();
        Date date = new Date();
        checkConversion(new Object[] { "String", Integer.valueOf("1"), Double.valueOf("1.0"), Long.valueOf("1"),
                BigInteger.valueOf(1), BigDecimal.valueOf(1.0), date, java.sql.Date.valueOf(localDate),
                java.sql.Time.valueOf(localTime), Timestamp.valueOf(localDateTime), localDateTime, localDate, localTime,
                Integer.valueOf("1") }, date, localDate, localTime, localDateTime, Timestamp.valueOf(localDateTime),
                java.sql.Date.valueOf(localDate));
        checkConversion(new Object[] { "String", Integer.valueOf("1"), Double.valueOf("1.0"), Long.valueOf("1"),
                BigInteger.valueOf(1), BigDecimal.valueOf(1.0), date, java.sql.Date.valueOf(localDate),
                java.sql.Time.valueOf(localTime), Timestamp.valueOf(localDateTime), date, java.sql.Date.valueOf(localDate),
                java.sql.Time.valueOf(localTime), Double.valueOf("1.0") }, date, localDate, localTime,
                new Timestamp(date.getTime()).toLocalDateTime(), Timestamp.valueOf(localDateTime), java.sql.Date.valueOf(localDate));
        checkConversion(new Object[] { "String", Integer.valueOf("1"), Double.valueOf("1.0"), Long.valueOf("1"),
                BigInteger.valueOf(1), BigDecimal.valueOf(1.0), date, java.sql.Date.valueOf(localDate),
                java.sql.Time.valueOf(localTime), Timestamp.valueOf(localDateTime), java.sql.Date.valueOf(localDate),
                date, Timestamp.valueOf(localDateTime), new BigDecimal("1.0") }, date, new java.sql.Date(date.getTime()).toLocalDate(),
                localTime, localDateTime.withHour(0).withMinute(0).withSecond(0), Timestamp.valueOf(localDateTime),
                java.sql.Date.valueOf(localDate));
    }

    private static void checkConversion(Object[] objects, Date date, LocalDate localDate, LocalTime localTime,
                                        LocalDateTime localDateTime, Timestamp timestamp, java.sql.Date sqlDate)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TestClass testClass = GenericsUtils.fromArrayToObject(objects, TestClass.class);
        assert testClass.aString.equals("String");
        assert testClass.aInteger.equals(Integer.valueOf("1"));
        assert testClass.aDouble.equals(Double.valueOf("1.0"));
        assert testClass.aLong.equals(Long.valueOf("1"));
        assert testClass.aBigInteger.equals(BigInteger.valueOf(1));
        assert testClass.aBigDecimal.equals(BigDecimal.valueOf(1.0));
        assert testClass.aDate.equals(date);
        assert testClass.aSqlDate.equals(sqlDate);
        assert testClass.aSqlTime.equals(java.sql.Time.valueOf(localTime));
        assert testClass.aTimestamp.equals(timestamp);
        assert testClass.aLocalDate.equals(localDate);
        assert testClass.aLocalTime.equals(localTime);
        assert testClass.aLocalDateTime.equals(localDateTime);
        assert testClass.aNumber.intValue() == 1;
    }
}