package win.doyto.query.jpa2;

import org.springframework.stereotype.Service;
import win.doyto.query.test.user.UserEntity;
import win.doyto.query.test.user.UserQuery;

/**
 * UserService
 *
 * @author f0rb
 */
@Service
public class UserService extends AbstractJpa2Service<UserEntity, Integer, UserQuery> {

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }
}