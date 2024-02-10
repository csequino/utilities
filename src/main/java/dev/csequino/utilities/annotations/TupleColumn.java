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

package dev.csequino.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark fields in a class as columns for tuple conversion.
 * The annotation specifies the index or position of the corresponding value in an array.
 * <p>
 * This annotation should be applied to fields within a class where each field
 * represents a column or value in a tuple or array.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TupleColumn {

    /**
     * Specifies the index or position of the corresponding value in an array.
     *
     * @return the index or position of the annotated field in the array.
     */
    int value();
}
