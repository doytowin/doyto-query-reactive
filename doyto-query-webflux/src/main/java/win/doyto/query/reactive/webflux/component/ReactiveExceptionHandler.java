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

package win.doyto.query.reactive.webflux.component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import win.doyto.query.web.component.ErrorCodeI18nService;
import win.doyto.query.web.response.ErrorCode;

import static win.doyto.query.web.response.PresetErrorCode.HTTP_METHOD_NOT_SUPPORTED;

/**
 * ReactiveExceptionHandler
 *
 * @author f0rb on 2022-04-16
 */
@Slf4j
@AllArgsConstructor
@ControllerAdvice
@ResponseBody
class ReactiveExceptionHandler {

    private ErrorCodeI18nService errorCodeI18nService;

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorCode httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: " + e.getMessage(), e.getCause());
        return errorCodeI18nService.buildErrorCode(HTTP_METHOD_NOT_SUPPORTED, e.getMethod());
    }

}
