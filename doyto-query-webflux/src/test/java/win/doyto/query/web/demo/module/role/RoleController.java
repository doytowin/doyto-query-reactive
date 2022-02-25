package win.doyto.query.web.demo.module.role;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.doyto.query.reactive.webflux.controller.ReactiveEIQController;
import win.doyto.query.test.role.RoleEntity;
import win.doyto.query.test.role.RoleQuery;

/**
 * RoleController
 *
 * @author f0rb on 2021-10-26
 */
@RestController
@RequestMapping("role")
public class RoleController extends ReactiveEIQController<RoleEntity, Integer, RoleQuery> {
}
