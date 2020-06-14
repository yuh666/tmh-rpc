package show.tmh.rpc.client.core;

import java.lang.reflect.Proxy;

/**
 * @author zy-user
 */
@SuppressWarnings("all")
public class RpcFactory {

    public static <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new RpcProxy());
    }
}
