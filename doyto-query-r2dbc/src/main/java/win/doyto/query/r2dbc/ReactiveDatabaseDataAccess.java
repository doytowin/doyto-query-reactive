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
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.IdWrapper;
import win.doyto.query.entity.Persistable;
import win.doyto.query.reactive.core.ReactiveDataAccess;
import win.doyto.query.sql.SqlAndArgs;
import win.doyto.query.sql.SqlBuilder;
import win.doyto.query.sql.SqlBuilderFactory;
import win.doyto.query.util.BeanUtil;
import win.doyto.query.util.ColumnUtil;

import java.io.Serializable;

/**
 * ReactiveDefaultDataAccess
 *
 * @author f0rb on 2021-11-18
 */
public class ReactiveDatabaseDataAccess<E extends Persistable<I>, I extends Serializable, Q extends DoytoQuery> implements ReactiveDataAccess<E, I, Q> {

    private R2dbcOperations r2dbcOperations;
    private SqlBuilder<E> sqlBuilder;
    private RowMapper<E> rowMapper;
    private String[] selectColumns;
    private Class<I> idClass;

    public ReactiveDatabaseDataAccess(R2dbcOperations r2dbcOperations, Class<E> entityClass) {
        this.r2dbcOperations = r2dbcOperations;
        this.sqlBuilder = SqlBuilderFactory.create(entityClass);
        this.rowMapper = new BeanPropertyRowMapper<>(entityClass);
        this.selectColumns = ColumnUtil.resolveSelectColumns(entityClass);

        this.idClass = BeanUtil.getIdClass(entityClass);
    }

    @Override
    public Mono<E> create(E e) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildCreateAndArgs(e);
        return r2dbcOperations.insert(sqlAndArgs, "id", idClass)
                              .map(id -> {
                                  e.setId(id);
                                  return e;
                              });
    }

    @Override
    public Flux<E> query(Q q) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildSelectColumnsAndArgs(q, selectColumns);
        return r2dbcOperations.query(sqlAndArgs, rowMapper);
    }

    @Override
    public <V> Flux<V> queryColumns(Q q, Class<V> clazz, String... columns) {
        return null;
    }

    @Override
    public Flux<I> queryIds(Q query) {
        return null;
    }

    @Override
    public Mono<Long> count(Q q) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildCountAndArgs(q);
        return r2dbcOperations.count(sqlAndArgs);
    }

    @Override
    public Mono<E> get(IdWrapper<I> w) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildSelectById(w, selectColumns);
        return Mono.from(r2dbcOperations.query(sqlAndArgs, rowMapper));
    }

    @Override
    public Mono<Integer> delete(IdWrapper<I> w) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildDeleteById(w);
        return r2dbcOperations.update(sqlAndArgs);
    }

    @Override
    public Mono<Integer> delete(Q query) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildDeleteAndArgs(query);
        return r2dbcOperations.update(sqlAndArgs);
    }

    @Override
    public Mono<Integer> update(E e) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildUpdateAndArgs(e);
        return r2dbcOperations.update(sqlAndArgs);
    }

    @Override
    public Mono<Integer> patch(E e) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildPatchAndArgsWithId(e);
        return r2dbcOperations.update(sqlAndArgs);
    }

    @Override
    public Mono<Integer> patch(E e, Q q) {
        SqlAndArgs sqlAndArgs = sqlBuilder.buildPatchAndArgs(e, q);
        return r2dbcOperations.update(sqlAndArgs);
    }
}
