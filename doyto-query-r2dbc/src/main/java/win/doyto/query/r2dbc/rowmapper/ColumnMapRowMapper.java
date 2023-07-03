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

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ColumnMapRowMapper
 *
 * @author f0rb on 2022/12/23
 * @since 1.0.0
 */
public class ColumnMapRowMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> map(Row row, int rn) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
        RowMetadata rm = row.getMetadata();
        for (ColumnMetadata cm : rm.getColumnMetadatas()) {
            String column = cm.getName();
            ret.put(column, row.get(column));
        }
        return ret;
    }
}
