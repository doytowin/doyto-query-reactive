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

package win.doyto.query.mongodb.reactive;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import win.doyto.query.config.GlobalConfiguration;
import win.doyto.query.core.UniqueKey;
import win.doyto.query.reactive.core.ReactiveAssociationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ReactiveMongoAssociationService
 *
 * @author f0rb on 2022-06-17
 */
public class ReactiveMongoAssociationService implements ReactiveAssociationService<ObjectId, ObjectId> {
    private final MongoCollection<Document> collection;
    private final ReactiveSessionSupplier reactiveSessionSupplier;
    private final String domainId1;
    private final String domainId2;

    public ReactiveMongoAssociationService(MongoClient mongoClient, String database, String domain1, String domain2) {
        this(ReactiveSessionThreadLocalSupplier.create(mongoClient), database, domain1, domain2);
    }

    public ReactiveMongoAssociationService(
            ReactiveSessionSupplier reactiveSessionSupplier, String database, String domain1, String domain2) {
        String joinTable = String.format(GlobalConfiguration.JOIN_TABLE_FORMAT, domain1, domain2);
        this.collection = reactiveSessionSupplier.getMongoClient().getDatabase(database).getCollection(joinTable);
        this.reactiveSessionSupplier = reactiveSessionSupplier;
        this.domainId1 = String.format(GlobalConfiguration.JOIN_ID_FORMAT, domain1);
        this.domainId2 = String.format(GlobalConfiguration.JOIN_ID_FORMAT, domain2);
    }

    @Override
    public Mono<Integer> associate(Set<UniqueKey<ObjectId, ObjectId>> uniqueKeys) {
        if (uniqueKeys.isEmpty()) {
            return Mono.just(0);
        }
        List<Document> list = new ArrayList<>(uniqueKeys.size());
        for (UniqueKey<ObjectId, ObjectId> uniqueKey : uniqueKeys) {
            Document doc = new Document(domainId1, uniqueKey.getK1()).append(domainId2, uniqueKey.getK2());
            list.add(doc);
        }
        return Mono.from(collection.insertMany(reactiveSessionSupplier.get(), list))
                   .map(insertManyResult -> insertManyResult.getInsertedIds().size());
    }

    @Override
    public Mono<Long> dissociate(Set<UniqueKey<ObjectId, ObjectId>> uniqueKeys) {
        Bson filter = buildKeysFilter(uniqueKeys);
        return Mono.from(collection.deleteMany(reactiveSessionSupplier.get(), filter))
                   .map(DeleteResult::getDeletedCount);
    }

    private Bson buildKeysFilter(Set<UniqueKey<ObjectId, ObjectId>> uniqueKeys) {
        List<Bson> filters = new ArrayList<>(uniqueKeys.size());
        for (UniqueKey<ObjectId, ObjectId> uniqueKey : uniqueKeys) {
            Document doc = new Document(domainId1, uniqueKey.getK1()).append(domainId2, uniqueKey.getK2());
            filters.add(doc);
        }
        return Filters.or(filters);
    }

    @Override
    public Flux<ObjectId> queryK1ByK2(ObjectId k2) {
        return Flux.from(collection.find(reactiveSessionSupplier.get(), new Document(domainId2, k2)))
                   .map(document -> document.getObjectId(domainId1));
    }

    @Override
    public Flux<ObjectId> queryK2ByK1(ObjectId k1) {
        return Flux.from(collection.find(reactiveSessionSupplier.get(), new Document(domainId1, k1)))
                   .map(document -> document.getObjectId(domainId2));
    }

    @Override
    public Mono<Long> deleteByK1(ObjectId k1) {
        return Mono.from(collection.deleteMany(reactiveSessionSupplier.get(), new Document(domainId1, k1)))
                   .map(DeleteResult::getDeletedCount);
    }

    @Override
    public Mono<Long> deleteByK2(ObjectId k2) {
        return Mono.from(collection.deleteMany(reactiveSessionSupplier.get(), new Document(domainId2, k2)))
                   .map(DeleteResult::getDeletedCount);
    }

    @Override
    public Mono<Long> count(Set<UniqueKey<ObjectId, ObjectId>> uniqueKeys) {
        Bson filter = buildKeysFilter(uniqueKeys);
        return Mono.from(collection.countDocuments(reactiveSessionSupplier.get(), filter));
    }
}
