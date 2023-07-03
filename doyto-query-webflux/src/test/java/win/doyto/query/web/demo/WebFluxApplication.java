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

package win.doyto.query.web.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import win.doyto.query.reactive.webflux.config.WebFluxConfigurerAdapter;

/**
 * WebFluxApplication
 *
 * @author f0rb on 2021-10-30
 */
@SpringBootApplication
public class WebFluxApplication extends WebFluxConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class);
    }
}