package win.doyto.query.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import win.doyto.query.jpa2.AbstractJpa2Service;

/**
 * UserService
 *
 * @author f0rb
 * @date 2019-05-12
 */
@Slf4j
@Service
class UserService extends AbstractJpa2Service<UserEntity, Integer, UserQuery> {

    public UserService(UserRepository crudRepository) {
        super(crudRepository);
    }

}