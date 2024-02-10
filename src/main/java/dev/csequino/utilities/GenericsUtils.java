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
import java.util.ArrayList;
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
     * Converts an array of objects into an instance of the specified class by mapping
     * array elements to fields annotated with {@code TupleColumn}.
     * If specified class is a nested class the class access modifier must be public.
     *
     * @param a an array of objects containing values to be assigned to the object.
     * @param c the class of the target object.
     * @param <T> the type of the object.
     * @return an instance of the object populated with values from the array.
     * @throws NoSuchMethodException if a default constructor is not found in the specified class.
     * @throws IllegalAccessException if access to the fields of the object is denied.
     * @throws InstantiationException if an instance of the object cannot be created.
     * @throws InvocationTargetException if an error occurs during the invocation of the object's constructor.
     * @throws IllegalArgumentException if the type of array element does not match the type of the corresponding field.
     */
    public static <T> T fromArrayToObject(Object[] a, Class<T> c)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Create an instance of the object using the default constructor
        Object o = c.getDeclaredConstructor().newInstance();
        // Iterate over the fields of the object to map array elements to annotated fields
        for (Field field : c.getDeclaredFields()) {
            TupleColumn rf = field.getAnnotation(TupleColumn.class);
            // Check if field is annotated
            if (rf != null) {
                // Check if the types match before setting the field value
                if (rf.value() < a.length && a[rf.value()] != null && a[rf.value()].getClass().equals(field.getType())) {
                    field.setAccessible(true);
                    field.set(o, a[rf.value()]);
                }
            }
        }
        // Cast the object to type T (the object type)
        return c.cast(o);
    }
}
