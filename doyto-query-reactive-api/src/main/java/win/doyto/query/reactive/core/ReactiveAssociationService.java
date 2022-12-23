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
import win.doyto.query.core.UniqueKey;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ReactiveAssociationService
 *
 * @author f0rb on 2022/12/23
 * @since 1.0.0
 */
public interface ReactiveAssociationService<K1, K2> {

    default Set<UniqueKey<K1, K2>> buildUniqueKeys(K1 k1, List<K2> collection) {
        return collection.stream().map(k2 -> new UniqueKey<>(k1, k2)).collect(Collectors.toSet());
    }

    default Set<UniqueKey<K1, K2>> buildUniqueKeys(List<K1> list, K2 k2) {
        return list.stream().map(k1 -> new UniqueKey<>(k1, k2)).collect(Collectors.toSet());
    }

    default Mono<Integer> associate(K1 k1, K2 k2) {
        return associate(Collections.singleton(new UniqueKey<>(k1, k2)));
    }

    Mono<Integer> associate(Set<UniqueKey<K1, K2>> uniqueKeys);

    default Mono<Integer> dissociate(K1 k1, K2 k2) {
        return dissociate(Collections.singleton(new UniqueKey<>(k1, k2)));
    }

    Mono<Integer> dissociate(Set<UniqueKey<K1, K2>> uniqueKeys);

    Flux<K1> queryK1ByK2(K2 k2);

    Flux<K2> queryK2ByK1(K1 k1);

    Mono<Integer> deleteByK1(K1 k1);

    Mono<Integer> deleteByK2(K2 k2);

    default Mono<Integer> reassociateForK1(K1 k1, List<K2> list) {
        return deleteByK1(k1).publish(i -> associate(buildUniqueKeys(k1, list)));
    }

    default Mono<Integer> reassociateForK2(K2 k2, List<K1> list) {
        return deleteByK2(k2).publish(i -> associate(buildUniqueKeys(list, k2)));
    }

    Mono<Long> count(Set<UniqueKey<K1, K2>> set);

    default Mono<Boolean> exists(K1 k1, K2 k2) {
        return exists(Collections.singleton(new UniqueKey<>(k1, k2)));
    }

    default Mono<Boolean> exists(Set<UniqueKey<K1, K2>> uniqueKeys) {
        return count(uniqueKeys).map(cnt -> cnt > 0);
    }

    default Mono<Boolean> existsExactly(Set<UniqueKey<K1, K2>> uniqueKeys) {
        return count(uniqueKeys).map(cnt -> cnt == uniqueKeys.size());
    }
}
