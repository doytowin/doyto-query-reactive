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

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import win.doyto.query.r2dbc.role.RoleEntity;
import win.doyto.query.r2dbc.rowmapper.BeanPropertyRowMapper;
import win.doyto.query.r2dbc.rowmapper.RowMapper;
import win.doyto.query.sql.SqlAndArgs;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/**
 * R2dbcTemplateTest
 *
 * @author f0rb on 2021-11-20
 */
class R2dbcTemplateTest {

    static Pattern SQL_PTN = Pattern.compile("^\\s*(CREATE|DROP|INSERT|ALTER)\\s.+", Pattern.CASE_INSENSITIVE);
    static AtomicInteger index = new AtomicInteger();
    R2dbcTemplate r2dbc;

    @BeforeEach
    void setUp() {
        r2dbc = createR2dbcTemplate("testdb-" + index.getAndIncrement());
    }

    static R2dbcTemplate createR2dbcTemplate(String databaseName) {
        ConnectionFactory connectionFactory = createConnectionFactory(databaseName);
        initDatabase(connectionFactory);
        return new R2dbcTemplate(connectionFactory);
    }

    private static ConnectionFactory createConnectionFactory(String databaseName) {
        // Creates a ConnectionFactory for the specified DRIVER
        ConnectionFactoryOptions options = ConnectionFactoryOptions
                .builder()
                .option(DRIVER, "h2")
                .option(PROTOCOL, "mem")
                .option(DATABASE, databaseName)
                .build();
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);

        // Create a ConnectionPool for connectionFactory
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration
                .builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .maxSize(10)
                .build();

        return new ConnectionPool(configuration);
    }

    private static void initDatabase(ConnectionFactory connectionFactory) {
        Flux.from(connectionFactory.create())
            .flatMap(
                    c -> {
                        Batch batch = c.createBatch();
                        List<String> statements = new ArrayList<>();
                        statements.addAll(buildSqlStatements("/schema.sql"));
                        statements.addAll(buildSqlStatements("/data.sql"));
                        statements.forEach(batch::add);
                        return Flux.from(batch.execute())
                                   .doFinally(s -> c.close());
                    }
            )
            .log()
            .subscribeOn(Schedulers.boundedElastic())
            .blockLast();
    }

    @SneakyThrows
    private static List<String> buildSqlStatements(String filename) {
        List<String> statements = new ArrayList<>();
        try (InputStream is = R2dbcTemplateTest.class.getResourceAsStream(filename) ) {
            if (is != null) {
                List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
                StringBuilder lastStatement = new StringBuilder();
                for (String line : lines) {
                    if (SQL_PTN.matcher(line).matches()) {
                        if (SQL_PTN.matcher(lastStatement).matches()) {
                            statements.add(lastStatement.toString());
                        }
                        lastStatement = new StringBuilder(line);
                    } else {
                        lastStatement.append(line);
                    }
                }
            }
        }
        return statements;
    }

    @Test
    void count() {
        r2dbc.count(new SqlAndArgs("SELECT count(*) FROM t_role"))
             .as(StepVerifier::create)
             .expectNext(5L)
             .verifyComplete();
    }

    @Test
    void countWithArgs() {
        r2dbc.count(new SqlAndArgs("SELECT count(*) FROM t_role WHERE role_name LIKE ?", "%vip%"))
             .as(StepVerifier::create)
             .expectNext(4L)
             .verifyComplete();
    }

    @Test
    void insert() {
        String sql = "INSERT INTO t_role (role_name, role_code, valid) VALUES (?, ?, ?)";
        Object[] args = new Object[]{"高级3", "VIP3", true};

        r2dbc.insert(new SqlAndArgs(sql, args), "id", Integer.class)
             .as(StepVerifier::create)
             .expectNext(6)
             .verifyComplete();

        String countSql = "SELECT count(*) FROM t_role WHERE role_code LIKE ?";
        r2dbc.count(new SqlAndArgs(countSql, "VIP%"))
             .as(StepVerifier::create)
             .expectNext(5L)
             .verifyComplete();
    }

    @Test
    void update() {
        String sql = "UPDATE t_role SET valid = ? WHERE id > ?";
        Object[] args = new Object[]{false, 1};

        r2dbc.update(sql, args)
             .as(StepVerifier::create)
             .expectNext(4)
             .verifyComplete();

        String countSql = "SELECT count(*) FROM t_role WHERE valid = ?";
        r2dbc.count(new SqlAndArgs(countSql, true))
             .as(StepVerifier::create)
             .expectNext(1L)
             .verifyComplete();
    }

    @Test
    void updateWithNull() {
        String sql = "UPDATE t_role SET valid = ? WHERE id = ?";
        Object[] args = new Object[]{null, 3};

        r2dbc.update(sql, args)
             .as(StepVerifier::create)
             .expectNext(1)
             .verifyComplete();

        r2dbc.count(new SqlAndArgs("SELECT count(*) FROM t_role WHERE valid is null"))
             .as(StepVerifier::create)
             .expectNext(1L)
             .verifyComplete();
    }

    @Test
    void query() {
        SqlAndArgs sqlAndArgs = new SqlAndArgs("SELECT * FROM t_role WHERE role_code LIKE ?", "ADMIN%");
        RowMapper<RoleEntity> rowMapper = (row, rn) -> {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setId(row.get("id", Integer.class));
            roleEntity.setRoleName(row.get("role_name", String.class));
            roleEntity.setRoleCode(row.get("role_code", String.class));
            roleEntity.setValid(row.get("valid", Boolean.class));
            return roleEntity;
        };

        r2dbc.query(sqlAndArgs, rowMapper)
             .as(StepVerifier::create)
             .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                     && roleEntity.getRoleName().equals("admin")
                     && roleEntity.getRoleCode().equals("ADMIN"))
             .verifyComplete();
    }

    @Test
    void queryWithBeanMapper() {
        SqlAndArgs sqlAndArgs = new SqlAndArgs("SELECT id, role_name AS roleName, role_code, valid FROM t_role WHERE role_code LIKE ?", "ADMIN%");
        RowMapper<RoleEntity> rowMapper = new BeanPropertyRowMapper<>(RoleEntity.class);

        r2dbc.query(sqlAndArgs, rowMapper)
             .as(StepVerifier::create)
             .expectNextMatches(roleEntity -> roleEntity.getId() == 1
                     && roleEntity.getRoleName().equals("admin")
                     && roleEntity.getRoleCode() == null
                     && roleEntity.getValid())
             .verifyComplete();
    }

}
