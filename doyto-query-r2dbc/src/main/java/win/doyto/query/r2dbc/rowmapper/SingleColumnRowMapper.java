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

/**
 * SingleColumnRowMapper
 *
 * @author f0rb on 2022/12/23
 * @since 1.0.0
 */
public class SingleColumnRowMapper<V> implements RowMapper<V> {
    private final Class<V> clazz;

    public SingleColumnRowMapper(Class<V> clazz) {
        this.clazz = clazz;
    }

    @Override
    public V map(Row row, int rn) {
        return row.get(0, clazz);
    }
}
