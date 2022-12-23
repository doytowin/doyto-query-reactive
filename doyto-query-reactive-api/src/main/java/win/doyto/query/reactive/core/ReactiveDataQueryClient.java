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

package win.doyto.query.reactive.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.core.AggregationQuery;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.PageList;
import win.doyto.query.core.RelationalQuery;
import win.doyto.query.entity.Persistable;

import java.io.Serializable;

/**
 * ReactiveDataQueryClient
 *
 * @author f0rb on 2022/12/23
 * @since 1.0.0
 */
public interface ReactiveDataQueryClient {

    <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Flux<V> query(Q query, Class<V> viewClass);

    <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Mono<Long> count(Q query, Class<V> viewClass);

    default <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Mono<PageList<V>> page(Q query, Class<V> viewClass) {
        query.forcePaging();
        return query(query, viewClass)
                .collectList()
                .zipWith(count(query, viewClass))
                .map(t -> new PageList<>(t.getT1(), t.getT2()));
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends RelationalQuery<V, I>>
    Flux<V> query(Q query) {
        return query(query, query.getDomainClass());
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends RelationalQuery<V, I>>
    Mono<Long> count(Q query) {
        return count(query, query.getDomainClass());
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends RelationalQuery<V, I>>
    Mono<PageList<V>> page(Q query) {
        return page(query, query.getDomainClass());
    }

    <V, Q extends AggregationQuery>
    Flux<V> aggregate(Q query, Class<V> viewClass);
}