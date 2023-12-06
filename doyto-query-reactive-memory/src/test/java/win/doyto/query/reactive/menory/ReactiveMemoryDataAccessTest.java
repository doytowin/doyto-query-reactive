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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import win.doyto.query.reactive.core.ReactiveDataAccess;
import win.doyto.query.test.TestEntity;
import win.doyto.query.test.TestQuery;

import java.util.Arrays;

import static win.doyto.query.test.TestEntity.initUserEntities;

/**
 * ReactiveMemoryDataAccessTest
 *
 * @author f0rb on 2023/12/6
 * @since 1.0.2
 */
class ReactiveMemoryDataAccessTest {

    ReactiveDataAccess<TestEntity, Integer, TestQuery> testMemoryDataAccess;

    @BeforeEach
    void setUp() {
        testMemoryDataAccess = new ReactiveMemoryDataAccess<>(TestEntity.class);
        testMemoryDataAccess.create(initUserEntities()).block();
    }

    @Test
    void queryIds() {
        TestQuery testQuery = TestQuery.builder().idIn(Arrays.asList(1, 3, 10)).build();
        testMemoryDataAccess.queryIds(testQuery)
                            .as(StepVerifier::create)
                            .expectNextMatches(i -> i == 1)
                            .expectNextMatches(i -> i == 3)
                            .verifyComplete();
    }

    @Test
    void queryColumns() {
        TestQuery testQuery = TestQuery.builder().idIn(Arrays.asList(1, 3, 10)).build();
        testMemoryDataAccess.queryColumns(testQuery, TestEntity.class, "id", "username")
                            .as(StepVerifier::create)
                            .expectNextMatches(e -> e.getId() == 1 && e.getUsername().equals("username1"))
                            .expectNextMatches(e -> e.getId() == 3 && e.getUsername().equals("username3"))
                            .verifyComplete();
    }

    @Test
    void deleteByQuery() {
        TestQuery testQuery = TestQuery.builder().idIn(Arrays.asList(1, 3, 10)).build();
        testMemoryDataAccess.delete(testQuery)
                            .as(StepVerifier::create)
                            .expectNextMatches(i -> i == 2)
                            .verifyComplete();
    }

}