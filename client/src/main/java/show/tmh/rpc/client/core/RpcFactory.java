package show.tmh.rpc.client.core;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author zy-user
 */
@SuppressWarnings("all")
public class RpcFactory {

    public static RegistryCache registryCache;

    public static void init(String zkAddr) {
        RpcRegistry rpcRegistry = new RpcRegistry(zkAddr);
        rpcRegistry.start();
        registryCache = new RegistryCache(rpcRegistry);
    }

    public static <T> T create(Class<T> clazz) {
        return create(clazz, 0, TimeUnit.MILLISECONDS);
    }

    public static <T> T create(Class<T> clazz, long timeout, TimeUnit unit) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new RpcProxy(unit.toMillis(timeout), registryCache));
    }
}
