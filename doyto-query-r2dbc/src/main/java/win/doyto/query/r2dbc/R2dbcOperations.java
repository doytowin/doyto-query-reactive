/*
 * Copyright © 2019-2022 Forb Yuan
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

package win.doyto.query.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.sql.SqlAndArgs;

/**
 * ReactiveDatabaseOptions
 *
 * @author f0rb on 2021-11-18
 */
public interface R2dbcOperations {

    <V> Flux<V> query(SqlAndArgs sqlAndArgs, RowMapper<V> rowMapper);

    Mono<Long> count(SqlAndArgs sqlAndArgs);

    <I> Mono<I> insert(SqlAndArgs sqlAndArgs, String idColumn, Class<I> idClass);

    Mono<Integer> update(SqlAndArgs sqlAndArgs);

    default Mono<Integer> update(String sql, Object... args) {
        return update(new SqlAndArgs(sql, args));
    }

}

