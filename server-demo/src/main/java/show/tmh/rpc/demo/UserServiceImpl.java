package show.tmh.rpc.demo;

public class UserServiceImpl implements UserService {
    @Override
    public User get(Long id) {
        return new User(id, "abc", 20);
    }
}
