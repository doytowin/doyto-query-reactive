package win.doyto.query.r2dbc;

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

    Mono<E> get(I id);

    Mono<E> get(IdWrapper<I> w);

    Mono<Integer> delete(I id);

    Mono<Integer> delete(IdWrapper<I> w);

    Mono<Integer> delete(Q query);

    Mono<Integer> update(E e);

    Mono<Integer> patch(E e);

    Mono<Integer> patch(E e, Q q);

    default Mono<Long> create(Iterable<E> entities) {
        return Flux.fromIterable(entities).flatMap(this::create).count();
    }

}