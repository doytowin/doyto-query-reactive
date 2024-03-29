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

package win.doyto.query.web.demo.module.role;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import win.doyto.query.util.BeanUtil;
import win.doyto.query.web.demo.WebFluxApplication;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.containsInRelativeOrder;

/**
 * RoleWebfluxTest
 *
 * @author f0rb on 2021-10-30
 */
@SpringBootTest(classes = WebFluxApplication.class)
@AutoConfigureWebTestClient
class RoleWebfluxTest {

    @Resource
    protected WebTestClient webTestClient;

    private Consumer<EntityExchangeResult<byte[]>> log() {
        return entityExchangeResult -> System.out.println(entityExchangeResult.toString());
    }

    @BeforeAll
    static void beforeAll() {
        LocaleContextHolder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    }

    @BeforeEach
    void setUp() throws IOException {
        List<RoleEntity> roleEntities = BeanUtil.loadJsonData("/role.json", new TypeReference<List<RoleEntity>>() {});
        webTestClient.post().uri("/role/")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(roleEntities))
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void getById() {
        webTestClient.get().uri("/role/1")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(true)
                     .jsonPath("$.data.id").isEqualTo("1")
                     .jsonPath("$.data.roleName").isEqualTo("admin");
    }

    @Test
    void deleteById() {
        webTestClient.delete().uri("/role/1")
                     .exchange()
                     .expectBody()
                     .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void should_return_code_9_when_get_nonexistent_entity() {
        webTestClient.get().uri("/role/-1")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(false)
                     .jsonPath("$.code").isEqualTo(9)
                     .jsonPath("$.message").isEqualTo("查询记录不存在");
    }

    @Test
    void should_return_two_records_when_query_by_role_name_like_vip() {
        webTestClient.get().uri("/role/?roleNameLike=vip")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(true)
                     .jsonPath("$.data.total").isEqualTo(2)
                     .jsonPath("$.data.list[*].id").value(containsInRelativeOrder(2, 3));
    }

    @Test
    void updateRole() {
        webTestClient.put().uri("/role/2")
                     .contentType(MediaType.APPLICATION_JSON)
                     .bodyValue("{\"id\":2,\"roleName\":\"vip0\",\"roleCode\":\"VIP0\"}")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(true);

        webTestClient.get().uri("/role/2")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.data.roleName").isEqualTo("vip0")
                     .jsonPath("$.data.roleCode").isEqualTo("VIP0")
                     .jsonPath("$.data.valid").doesNotExist();
    }

    @Test
    void patchRole() {
        webTestClient.patch().uri("/role/2")
                     .contentType(MediaType.APPLICATION_JSON)
                     .bodyValue("{\"id\":2,\"roleName\":\"vip0\",\"roleCode\":\"VIP0\"}")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(true);

        webTestClient.get().uri("/role/2")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.data.roleName").isEqualTo("vip0")
                     .jsonPath("$.data.roleCode").isEqualTo("VIP0")
                     .jsonPath("$.data.valid").isEqualTo(true);
    }

    @Test
    void shouldReturnEmptyData() {
        webTestClient.get().uri("/role/void")
                     .exchange()
                     .expectBody()
                     .consumeWith(log())
                     .jsonPath("$.success").isEqualTo(true)
                     .jsonPath("$.data").doesNotExist();
    }
}
