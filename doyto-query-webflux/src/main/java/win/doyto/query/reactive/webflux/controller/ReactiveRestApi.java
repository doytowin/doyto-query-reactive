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

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import win.doyto.query.core.DoytoQuery;
import win.doyto.query.core.PageList;
import win.doyto.query.entity.Persistable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * RestApi
 *
 * @author f0rb on 2021-11-13
 */
public interface ReactiveRestApi<E extends Persistable<I>, I extends Serializable, Q extends DoytoQuery> {
    @PostMapping
    Mono<Void> create(@RequestBody List<E> list);

    default Mono<E> create(E e) {
        return create(Arrays.asList(e)).thenReturn(e);
    }

    @GetMapping("/{id}")
    Mono<E> get(@PathVariable I id);

    @DeleteMapping("/{id}")
    Mono<E> delete(@PathVariable I id);

    @PutMapping("/{id}")
    Mono<Void> update(@PathVariable I id, @RequestBody E e);

    @PatchMapping("/{id}")
    Mono<Void> patch(@PathVariable I id, @RequestBody E e);

    @GetMapping("/")
    Mono<PageList<E>> page(Q q);
}
