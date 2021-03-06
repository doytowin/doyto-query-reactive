/*
 * Copyright © 2019-2021 Forb Yuan
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

package win.doyto.query.reactive.mongodb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import win.doyto.query.reactive.mongodb.test.inventory.InventoryEntity;
import win.doyto.query.reactive.mongodb.test.inventory.InventoryQuery;
import win.doyto.query.reactive.mongodb.test.inventory.SizeQuery;
import win.doyto.query.util.BeanUtil;

import java.io.IOException;
import java.util.List;

/**
 * ReactiveMongoDataAccessTest
 *
 * @author f0rb on 2021-12-26
 */
@ActiveProfiles("test")
@DataMongoTest(properties = {"spring.mongodb.embedded.version=3.5.5"})
@SpringBootApplication
@Import(EmbeddedMongoAutoConfiguration.class)
class ReactiveMongoDataAccessTest {

    private ReactiveMongoDataAccess<InventoryEntity, String, InventoryQuery> mongoDataAccess;

    @BeforeEach
    void setUp(@Autowired MongoClient mongoClient) throws IOException {
        this.mongoDataAccess = new ReactiveMongoDataAccess<>(mongoClient, InventoryEntity.class);

        List<? extends Document> data = BeanUtil.loadJsonData("test/inventory/inventory.json", new TypeReference<List<? extends Document>>() {});
        StepVerifier.create(mongoDataAccess.getCollection().insertMany(data)).expectNextCount(1).verifyComplete();
    }

    @Test
    void count_by_size$h_lt_10_and_status_eq_A() {
        SizeQuery sizeQuery = SizeQuery.builder().hLt(10).build();
        InventoryQuery query = InventoryQuery.builder().size(sizeQuery).status("A").build();
        mongoDataAccess.count(query)
                       .as(StepVerifier::create)
                       .expectNext(1L)
                       .verifyComplete();
    }

}