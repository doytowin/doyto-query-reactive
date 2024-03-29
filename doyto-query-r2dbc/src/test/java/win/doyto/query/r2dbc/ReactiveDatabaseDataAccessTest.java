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

package win.doyto.query.r2dbc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import win.doyto.query.config.GlobalConfiguration;
import win.doyto.query.r2dbc.role.RoleEntity;
import win.doyto.query.r2dbc.role.RoleQuery;
import win.doyto.query.r2dbc.role.RoleView;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ReactiveDefaultDataAccessTest
 *
 * @author f0rb on 2021-11-18
 */
class ReactiveDatabaseDataAccessTest {
    static AtomicInteger index = new AtomicInteger();

    ReactiveDatabaseDataAccess<RoleEntity, Integer, RoleQuery> reactiveDataAccess;

    @BeforeAll
    static void beforeAll() {
        GlobalConfiguration.instance().setMapCamelCaseToUnderscore(true);
    }

    @BeforeEach
    void setUp() {
        R2dbcTemplate r2dbcTemplate = R2dbcTemplateTest.createR2dbcTemplate("db-role-" + index.getAndIncrement());
        reactiveDataAccess = new ReactiveDatabaseDataAccess<>(r2dbcTemplate, RoleEntity.class);
    }

    @AfterAll
    static void afterAll() {
        GlobalConfiguration.instance().setMapCamelCaseToUnderscore(false);
    }

    @Test
    void count() {
        reactiveDataAccess.count(RoleQuery.builder().build())
                          .as(StepVerifier::create)
                          .expectNext(5L)
                          .verifyComplete();
    }

    @Test
    void query() {
        reactiveDataAccess.query(RoleQuery.builder().build())
                          .as(StepVerifier::create)
                          .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                                  && roleEntity.getRoleName().equals("admin")
                                  && roleEntity.getRoleCode().equals("ADMIN")
                                  && roleEntity.getValid())
                          .expectNextCount(4)
                          .verifyComplete();
    }

    @Test
    void get() {
        reactiveDataAccess.get(1)
                          .as(StepVerifier::create)
                          .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                                  && roleEntity.getRoleName().equals("admin")
                                  && roleEntity.getRoleCode().equals("ADMIN")
                                  && roleEntity.getValid())
                          .verifyComplete();
    }

    @Test
    void delete() {
        reactiveDataAccess.delete(1)
                          .as(StepVerifier::create)
                          .expectNextMatches(cnt -> cnt == 1)
                          .verifyComplete();
    }

    @Test
    void deleteByQuery() {
        reactiveDataAccess.delete(RoleQuery.builder().roleNameLike("vip").build())
                          .as(StepVerifier::create)
                          .expectNextMatches(cnt -> cnt == 4)
                          .verifyComplete();
    }

    @Test
    void update() {
        RoleEntity adminRole = reactiveDataAccess.get(1).block();
        adminRole.setRoleName("superadmin");
        adminRole.setValid(null);
        reactiveDataAccess.update(adminRole)
                          .as(StepVerifier::create)
                          .expectNextMatches(cnt -> cnt == 1)
                          .verifyComplete();

        reactiveDataAccess.get(1)
                          .as(StepVerifier::create)
                          .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                                  && roleEntity.getRoleName().equals("superadmin")
                                  && roleEntity.getRoleCode().equals("ADMIN")
                                  && roleEntity.getValid() == null)
                          .verifyComplete();
    }

    @Test
    void patch() {
        RoleEntity adminRole = new RoleEntity();
        adminRole.setId(1);
        adminRole.setRoleName("superadmin");
        reactiveDataAccess.patch(adminRole)
                          .as(StepVerifier::create)
                          .expectNextMatches(cnt -> cnt == 1)
                          .verifyComplete();

        reactiveDataAccess.get(1)
                          .as(StepVerifier::create)
                          .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                                  && roleEntity.getRoleName().equals("superadmin")
                                  && roleEntity.getRoleCode().equals("ADMIN")
                                  && roleEntity.getValid())
                          .verifyComplete();
    }

    @Test
    void patchByQuery() {
        RoleEntity patchRole = new RoleEntity();
        patchRole.setValid(false);

        RoleQuery queryVip = RoleQuery.builder().roleNameLike("vip").build();
        reactiveDataAccess.patch(patchRole, queryVip)
                          .as(StepVerifier::create)
                          .expectNextMatches(cnt -> cnt == 4)
                          .verifyComplete();
    }

    @Test
    void create() {
        RoleEntity vip3Role = new RoleEntity();
        vip3Role.setRoleName("vip3");
        vip3Role.setRoleCode("VIP3");
        vip3Role.setValid(false);

        reactiveDataAccess.create(vip3Role)
                          .as(StepVerifier::create)
                          .expectNextMatches(roleEntity -> roleEntity.getId() == 6
                                  && roleEntity.getRoleName().equals("vip3"))
                          .verifyComplete();
    }

    @Test
    void queryColumns() {
        RoleQuery roleQuery = RoleQuery.builder().build();
        reactiveDataAccess.queryColumns(roleQuery, RoleView.class, "id", "role_name AS roleName", "role_code AS roleCode")
                          .as(StepVerifier::create)
                          .expectNextMatches(roleView -> roleView.getId() == 1
                                  && roleView.getRoleName().equals("admin")
                                  && roleView.getRoleCode().equals("ADMIN")
                          ).expectNextCount(4)
                          .verifyComplete();
    }

    @Test
    void queryIds() {
        RoleQuery roleQuery = RoleQuery.builder().build();
        reactiveDataAccess.queryIds(roleQuery)
                          .as(StepVerifier::create)
                          .expectNext(1, 2, 3, 4, 5)
                          .verifyComplete();
    }

    @Test
    void queryColumnsForSingleColumn() {
        RoleQuery roleQuery = RoleQuery.builder().build();
        reactiveDataAccess.queryColumns(roleQuery, String.class, "role_name")
                          .as(StepVerifier::create)
                          .expectNext("admin", "vip", "vip2", "vip3", "vip4")
                          .verifyComplete();
    }

    @Test
    void queryColumnsForMap() {
        RoleQuery roleQuery = RoleQuery.builder().build();
        reactiveDataAccess.queryColumns(roleQuery, Map.class, "id", "role_name", "valid")
                          .as(StepVerifier::create)
                          .expectNextMatches(roleMap -> roleMap.entrySet().size() == 3
                                  && roleMap.get("ID").equals(1)
                                  && roleMap.get("ROLE_NAME").equals("admin")
                                  && roleMap.get("VALID").equals(true)
                          ).expectNextCount(4)
                          .verifyComplete();
    }
}