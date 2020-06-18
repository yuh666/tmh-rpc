package show.tmh.rpc.demo;

public class UserServiceImpl implements UserService {
    @Override
    public User get(Long id) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return new User(id, "abc", 20);
    }
}
