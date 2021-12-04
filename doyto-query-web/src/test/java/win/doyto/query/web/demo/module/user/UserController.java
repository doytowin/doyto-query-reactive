package win.doyto.query.web.demo.module.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import win.doyto.query.web.controller.AbstractRestController;
import win.doyto.query.web.response.ErrorCode;
import win.doyto.query.web.response.JsonBody;
import win.doyto.query.web.response.JsonResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 * UserController
 *
 * @author f0rb on 2020-04-01
 */
@Slf4j
@RestController
@RequestMapping("user")
@JsonBody
@Validated
public class UserController
        extends AbstractRestController<UserEntity, Long, UserQuery, UserRequest, UserResponse>
        implements UserApi {

    UserService userService;

    UserDetailService userDetailService;

    public UserController(UserService userService, UserDetailService userDetailService) {
        super(userService);
        this.userService = userService;
        this.userDetailService = userDetailService;
    }

    @GetMapping("/username")
    public UserResponse getByUsername(@Size(min = 4, max = 20) String username) {
        UserEntity userEntity = service.get(UserQuery.builder().username(username).build());
        return buildResponse(userEntity);
    }

    @GetMapping("/email")
    public JsonResponse<UserResponse> getByEmail(@Valid UserEmailRequest userEmailRequest) {
        UserEntity userEntity = service.get(UserQuery.builder().email(userEmailRequest.getEmail()).build());
        return ErrorCode.build(buildResponse(userEntity));
    }

    @Override
    public UserResponse get(@PathVariable Long id) {
        UserResponse userResponse = super.get(id);
        UserDetailEntity userDetailEntity = userDetailService.get(id);
        if (userDetailEntity != null) {
            userResponse.setAddress(userDetailEntity.getAddress());
        }
        return userResponse;
    }

    @Override
    public void create(List<UserRequest> requests) {
        listValidator.validateList(requests);
        for (UserRequest request : requests) {
            UserEntity userEntity = userService.save(buildEntity(request));
            userDetailService.save(UserDetailEntity.build(userEntity.getId(), request));
        }
    }

    @Override
    public List<UserResponse> query(UserQuery q) {
        return userService.queryColumns(q, UserResponse.class, "id", "username", "mobile", "email", "nickname", "valid", "user_level", "memo");
    }

    @GetMapping("column/{column:\\w+}")
    public List<String> listColumn(UserQuery q, @PathVariable String column) {
        return userService.queryColumns(q, String.class, column);
    }

    @GetMapping("columns/{columns:[\\w,]+}")
    public List<Map> listColumns(UserQuery q, @PathVariable String columns) {
        return userService.queryColumns(q, Map.class, columns);
    }

    @Override
    public UserResponse auth(String account, String password) {
        UserEntity userEntity = userService.get(UserQuery.builder().usernameOrEmailOrMobile(account).build());
        ErrorCode.assertNotNull(userEntity, ErrorCode.build("账号不存在"));
        ErrorCode.assertTrue(userEntity.getValid(), ErrorCode.build("账号被禁用"));
        ErrorCode.assertTrue(Objects.equals(userEntity.getPassword(), password), ErrorCode.build("密码错误"));
        return buildResponse(userEntity);
    }

    @PostMapping("memo")
    public void updateMemo(@RequestBody UserRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setMemo(request.getMemo());
        UserQuery byEmail = UserQuery.builder().emailLike(request.getEmail()).memoNull(true).build();
        userService.patch(userEntity, byEmail);
    }
}