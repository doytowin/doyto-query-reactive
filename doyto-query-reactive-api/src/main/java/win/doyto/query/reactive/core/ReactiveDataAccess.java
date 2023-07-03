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
package win.doyto.query.reactive.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.IdWrapper;
import win.doyto.query.entity.Persistable;

import java.io.Serializable;

/**
 * ReactiveDataAccess
 *
 * @author f0rb on 2021-10-28
 */
public interface ReactiveDataAccess<E extends Persistable<I>, I extends Serializable, Q extends DoytoQuery> {

    Mono<E> create(E e);

    Flux<E> query(Q q);

    <V> Flux<V> queryColumns(Q q, Class<V> clazz, String... columns);

    Flux<I> queryIds(Q query);

    Mono<Long> count(Q q);

    default Mono<E> get(I id) {
        return get(IdWrapper.build(id));
    }

    Mono<E> get(IdWrapper<I> w);

    default Mono<Integer> delete(I id) {
        return delete(IdWrapper.build(id));
    }

    Mono<Integer> delete(IdWrapper<I> w);

    Mono<Integer> delete(Q query);

    Mono<Integer> update(E e);

    Mono<Integer> patch(E e);

    Mono<Integer> patch(E e, Q q);

    default Mono<Long> create(Iterable<E> entities) {
        return Flux.fromIterable(entities).flatMap(this::create).count();
    }

}