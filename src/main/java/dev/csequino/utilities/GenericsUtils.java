/*
 * Copyright 2024 Carlo Sequino <info@csequino.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.csequino.utilities;

import dev.csequino.utilities.annotations.TupleColumn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utilities related to generics.
 */
@SuppressWarnings("unused")
public final class GenericsUtils {

    private GenericsUtils() { }

    /**
     * Converts an array of objects into an object of the specified record type.
     * Public access modifier is required for record type.
     *
     * @param a an array of objects containing values to be assigned to the record.
     * @param c the class of the target record.
     * @param <T> the type of the record.
     * @return an instance of the record populated with values from the array.
     * @throws NoSuchMethodException if a matching constructor is not found in the specified class.
     * @throws IllegalAccessException if access to the constructor or record fields is denied.
     * @throws InstantiationException if an instance of the record cannot be created.
     * @throws InvocationTargetException if an error occurs during the invocation of the record's constructor.
     * @throws IllegalArgumentException if the number of elements in the array does not match the number of annotated
     * fields in the record.
     * @throws ArrayIndexOutOfBoundsException if annotated array position is greater than the array size.
     */
    public static <T> T fromArrayToRecord(Object[] a, Class<T> c)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> types = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        // Iterate over the record fields to obtain types and values to be assigned
        for (Field f : c.getDeclaredFields()) {
            TupleColumn tp = f.getAnnotation(TupleColumn.class);
            if (tp != null) {
                types.add(f.getType());
                values.add(a[tp.value()]);
            }
        }
        // Get the record constructor with the field types
        Constructor<?> cons = c.getDeclaredConstructor(types.toArray(new Class<?>[0]));
        // Create an instance of the record with values from the array
        Object o = cons.newInstance(values.toArray());
        // Cast the object to type T (the record type)
        return c.cast(o);
    }

    /**
     * Converts an array of objects into an instance of a specified class,
     * mapping array elements to the class fields based on the {@link TupleColumn} annotation.
     * If specified class is a nested class the class access modifier must be public.
     *
     * @param <T> the type of the object to be created
     * @param a the array of objects to be mapped to the fields of the class
     * @param c the class of the object to be created
     * @return an instance of the specified class with fields populated from the array
     * @throws NoSuchMethodException if the specified class does not have a no-argument constructor
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws InstantiationException if the specified class is an abstract class
     * @throws IllegalAccessException if the constructor or field is not accessible
     */
    public static <T> T fromArrayToObject(Object[] a, Class<T> c)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Create a new instance of class T
        Object o = c.getDeclaredConstructor().newInstance();
        // Iterate over all declared fields in class T
        for (Field field : c.getDeclaredFields()) {
            // Check if the field has the TupleColumn annotation
            TupleColumn rf = field.getAnnotation(TupleColumn.class);
            if (rf != null) {
                // Check if the index specified by the annotation is valid and if the corresponding array element is not null
                if (rf.value() < a.length && a[rf.value()] != null) {
                    // If the type of the array element matches the type of the field
                    if (a[rf.value()].getClass().equals(field.getType())) {
                        field.setAccessible(true);
                        field.set(o, a[rf.value()]); // Set the field value
                    }
                    // Conversion from Date or java.sql.Date to LocalDate
                    else if (field.getType().equals(LocalDate.class) && (a[rf.value()] instanceof Date || a[rf.value()] instanceof java.sql.Date)) {
                        field.setAccessible(true);
                        if (a[rf.value()] instanceof java.sql.Date) {
                            field.set(o, ((java.sql.Date) a[rf.value()]).toLocalDate());
                        } else {
                            field.set(o, new java.sql.Date(((Date) a[rf.value()]).getTime()).toLocalDate());
                        }
                    }
                    // Conversion from Date to LocalDateTime
                    else if (field.getType().equals(LocalDateTime.class) && (a[rf.value()] instanceof Date || a[rf.value()] instanceof java.sql.Date)) {
                        field.setAccessible(true);
                        field.set(o, new Timestamp(((Date) a[rf.value()]).getTime()).toLocalDateTime());
                    }
                    // Conversion from Timestamp or Time to LocalTime
                    else if (field.getType().equals(LocalTime.class) && (a[rf.value()] instanceof Timestamp || a[rf.value()] instanceof Time)) {
                        field.setAccessible(true);
                        if (a[rf.value()] instanceof Time) {
                            field.set(o, ((Time) a[rf.value()]).toLocalTime());
                        } else {
                            field.set(o, ((Timestamp) a[rf.value()]).toLocalDateTime().toLocalTime());
                        }
                    }
                }
            }
        }
        return c.cast(o); // Return the object cast to type T
    }
}
