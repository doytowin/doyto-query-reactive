/*
 * Copyright © 2019-2023 Forb Yuan
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

package win.doyto.query.r2dbc.rowmapper;

import io.r2dbc.spi.Row;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * BeanPropertyRowMapper
 *
 * @author f0rb on 2021-10-26
 */
@Slf4j
public class BeanPropertyRowMapper<E> implements RowMapper<E> {
    private final Class<E> mappedClass;
    private final Map<String, PropertyDescriptor> fieldMap = new LinkedHashMap<>();

    @SneakyThrows
    public BeanPropertyRowMapper(Class<E> mappedClass) {
        this.mappedClass = mappedClass;
        BeanInfo beanInfo = Introspector.getBeanInfo(mappedClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Arrays.stream(propertyDescriptors)
              .filter(pd -> pd.getWriteMethod() != null)
              .forEach(pd -> this.fieldMap.put(pd.getName(), pd));
    }

    @Override
    @SneakyThrows
    public E map(Row row, int rn) {
        E entity = mappedClass.getDeclaredConstructor().newInstance();

        Object value;
        for (PropertyDescriptor pd : fieldMap.values()) {
            try {
                value = row.get(pd.getName(), pd.getPropertyType());
            } catch (IllegalArgumentException | NoSuchElementException e) {
                log.error("Fail to get value for [{}]: {}", pd.getName(), e.getMessage());
                continue;
            }
            try {
                pd.getWriteMethod().invoke(entity, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Fail to invoke write method: {}-{}", pd.getWriteMethod().getName(), e.getMessage());
            }
        }
        return entity;
    }

}
