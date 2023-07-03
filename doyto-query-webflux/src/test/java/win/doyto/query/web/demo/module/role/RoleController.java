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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import win.doyto.query.reactive.webflux.controller.ReactiveEIQController;
import win.doyto.query.web.response.JsonBody;

/**
 * RoleController
 *
 * @author f0rb on 2021-10-26
 */
@JsonBody
@RestController
@RequestMapping("role")
public class RoleController extends ReactiveEIQController<RoleEntity, Integer, RoleQuery> {

    @GetMapping("void")
    public Mono<?> voidType() {
        return Mono.empty();
    }

}
