package win.doyto.query.reactive.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.core.AggregationQuery;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.JoinQuery;
import win.doyto.query.core.PageList;
import win.doyto.query.entity.Persistable;

import java.io.Serializable;

/**
 * ReactiveDataQueryClient
 *
 * @author f0rb on 2022-07-21
 */
public interface ReactiveDataQueryClient {

    <V, Q extends AggregationQuery>
    Flux<V> aggregate(Q query, Class<V> viewClass);

    <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Flux<V> query(Q query, Class<V> viewClass);

    <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Mono<Long> count(Q query, Class<V> viewClass);

    default <V extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
    Mono<PageList<V>> page(Q query, Class<V> viewClass) {
        return query(query, viewClass)
                .collectList()
                .zipWith(count(query, viewClass))
                .map(t -> new PageList<>(t.getT1(), t.getT2()));
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends JoinQuery<V, I>>
    Flux<V> query(Q query) {
        return query(query, query.getDomainClass());
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends JoinQuery<V, I>>
    Mono<Long> count(Q query) {
        return count(query, query.getDomainClass());
    }

    default <V extends Persistable<I>, I extends Serializable, Q extends JoinQuery<V, I>>
    Mono<PageList<V>> page(Q query) {
        return page(query, query.getDomainClass());
    }

}
