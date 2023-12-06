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
package win.doyto.query.reactive.menory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.IdWrapper;
import win.doyto.query.entity.Persistable;
import win.doyto.query.memory.MemoryDataAccess;
import win.doyto.query.reactive.core.ReactiveDataAccess;

import java.io.Serializable;

/**
 * ReactiveDataAccess
 *
 * @author f0rb on 2021-10-27
 */
public class ReactiveMemoryDataAccess<E extends Persistable<I>, I extends Serializable, Q extends DoytoQuery> implements ReactiveDataAccess<E, I, Q> {

    private final MemoryDataAccess<E, I, Q> delegate;

    public ReactiveMemoryDataAccess(Class<E> entityClass) {
        delegate = new MemoryDataAccess<>(entityClass);
    }

    @Override
    public Mono<E> create(E e) {
        return Mono.fromSupplier(() -> {
            delegate.create(e);
            return e;
        });
    }

    @Override
    public Flux<E> query(Q q) {
        return Flux.fromIterable(delegate.query(q));
    }

    @Override
    public <V> Flux<V> queryColumns(Q q, Class<V> clazz, String... columns) {
        return Flux.fromIterable(delegate.queryColumns(q, clazz, columns));
    }

    @Override
    public Flux<I> queryIds(Q q) {
        return Flux.fromIterable(delegate.queryIds(q));
    }

    @Override
    public Mono<E> get(IdWrapper<I> w) {
        return Mono.fromSupplier(() -> delegate.get(w));
    }

    @Override
    public Mono<Integer> delete(IdWrapper<I> w) {
        return Mono.fromSupplier(() -> delegate.delete(w));
    }

    @Override
    public Mono<Integer> delete(Q query) {
        return null;
    }

    @Override
    public Mono<Integer> update(E e) {
        return Mono.fromSupplier(() -> delegate.update(e));
    }

    @Override
    public Mono<Integer> patch(E e) {
        return Mono.fromSupplier(() -> delegate.patch(e));
    }

    @Override
    public Mono<Integer> patch(E e, Q q) {
        return null;
    }

    @Override
    public Mono<Long> count(Q q) {
        return Mono.fromSupplier(() -> delegate.count(q));
    }

}
