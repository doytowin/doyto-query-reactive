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

package win.doyto.query.reactive.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.PageList;
import win.doyto.query.entity.Persistable;
import win.doyto.query.reactive.core.ReactiveDataAccess;
import win.doyto.query.reactive.menory.ReactiveMemoryDataAccess;
import win.doyto.query.util.BeanUtil;
import win.doyto.query.web.response.ErrorCode;
import win.doyto.query.web.response.JsonBody;
import win.doyto.query.web.response.PresetErrorCode;

import java.io.Serializable;
import java.util.List;

/**
 * ReactiveEIQController
 *
 * @author f0rb on 2021-10-26
 */
@Slf4j
@JsonBody
public abstract class ReactiveEIQController<E extends Persistable<I>, I extends Serializable, Q extends DoytoQuery>
        implements ReactiveRestApi<E, I, Q> {

    private ReactiveDataAccess<E, I, Q> reactiveDataAccess;
    private Class<E> entityClass;

    @SuppressWarnings("unchecked")
    protected ReactiveEIQController() {
        this.entityClass = (Class<E>) BeanUtil.getActualTypeArguments(getClass())[0];
        this.reactiveDataAccess = new ReactiveMemoryDataAccess<>(entityClass);
    }

    private void assertNotNull(E e, I id) {
        ErrorCode.assertNotNull(e, PresetErrorCode.ENTITY_NOT_FOUND, entityClass.getSimpleName() + ":" + id);
    }

    @Override
    @PostMapping
    public Mono<Void> create(@RequestBody List<E> list) {
        return reactiveDataAccess.create(list).then();
    }

    public Flux<E> query(Q query) {
        return reactiveDataAccess.query(query);
    }

    @Override
    @GetMapping("/{id}")
    public Mono<E> get(@PathVariable I id) {
        return reactiveDataAccess.get(id).doOnSuccess(e -> assertNotNull(e, id));
    }

    @Override
    @DeleteMapping("/{id}")
    public Mono<E> delete(@PathVariable I id) {
        return get(id).flatMap(
                e -> reactiveDataAccess.delete(e.getId()).thenReturn(e)
        ).doOnSuccess(e -> assertNotNull(e, id));
    }

    @Override
    @PutMapping("/{id}")
    public Mono<Void> update(@PathVariable I id, @RequestBody E e) {
        e.setId(id);
        return update(e).then();
    }

    public Mono<Integer> update(E e) {
        return get(e.getId()).flatMap(
                old -> reactiveDataAccess.update(e)
        );
    }

    @Override
    @PatchMapping("/{id}")
    public Mono<Void> patch(@PathVariable I id, @RequestBody E e) {
        e.setId(id);
        return patch(e).then();
    }

    public Mono<Integer> patch(E e) {
        return get(e.getId()).flatMap(
                old -> reactiveDataAccess.patch(e)
        );
    }

    public Mono<Long> count(Q q) {
        return reactiveDataAccess.count(q);
    }

    @Override
    @GetMapping("/")
    public Mono<PageList<E>> page(Q q) {
        q.forcePaging();
        return query(q)
                .collectList()
                .zipWith(count(q))
                .map(t -> new PageList<>(t.getT1(), t.getT2()));
    }
}
