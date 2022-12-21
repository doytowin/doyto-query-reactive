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

import io.r2dbc.spi.*;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.sql.SqlAndArgs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * R2dbcTemplate
 *
 * @author f0rb on 2021-11-20
 */
@RequiredArgsConstructor
public class R2dbcTemplate implements R2dbcOperations {

    private final ConnectionFactory connectionFactory;

    private Flux<Result> executeSql(SqlAndArgs sqlAndArgs, String... idColumn) {
        return Flux.usingWhen(
                connectionFactory.create(),
                doExecute(sqlAndArgs, idColumn),
                Connection::close
        );
    }

    private Function<Connection, Publisher<? extends Result>> doExecute(SqlAndArgs sqlAndArgs, String... idColumns) {
        return connection -> {
            Statement statement = connection.createStatement(sqlAndArgs.getSql());
            Object[] args = sqlAndArgs.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    statement.bind(i, args[i]);
                } else {
                    statement.bindNull(i, String.class);
                }
            }
            if (idColumns.length > 0) {
                statement = statement.returnGeneratedValues(idColumns);
            }
            return statement.execute();
        };
    }

    @Override
    public <V> Flux<V> query(SqlAndArgs sqlAndArgs, RowMapper<V> rowMapper) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return executeSql(sqlAndArgs)
                .flatMap(result -> result.map(
                        readable -> rowMapper.map((Row) readable, atomicInteger.incrementAndGet())
                ));
    }

    @Override
    public Mono<Long> count(SqlAndArgs sqlAndArgs) {
        return executeSql(sqlAndArgs)
                .flatMap(result -> result.map(row -> row.get(0, Long.class)))
                .single();
    }

    @Override
    public <I> Mono<I> insert(SqlAndArgs sqlAndArgs, String idColumn, Class<I> idClass) {
        return executeSql(sqlAndArgs, idColumn)
                .flatMap(result -> result.map(row -> row.get(idColumn, idClass)))
                .single();
    }

    @Override
    public Mono<Integer> update(SqlAndArgs sqlAndArgs) {
        return executeSql(sqlAndArgs)
                .flatMap(Result::getRowsUpdated)
                .collect(Collectors.summingInt((ToIntFunction<Number>) Number::intValue));
    }
}
