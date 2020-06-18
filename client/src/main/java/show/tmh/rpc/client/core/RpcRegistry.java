package show.tmh.rpc.client.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * zk实例
 *
 * @author yuhao
 * @date 2020/6/18 2:16 下午
 */
public class RpcRegistry {

    private CuratorFramework cf;

    private ConcurrentHashMap<String, String> filter = new ConcurrentHashMap<>();

    public RpcRegistry(String connString) {
        cf = CuratorFrameworkFactory.builder()
                .connectString(connString)
                .retryPolicy(new RetryNTimes(3, 1))
                .build();
    }

    public List<String> getProvider(String interfaceName) throws Exception {
        return cf.getChildren().forPath("/tmh-rpc/" + interfaceName);
    }

    public void addProvider(String interfaceName, String providerAddr) throws Exception {
        cf.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(
                "/tmh-rpc/" + interfaceName + "/" + providerAddr);
    }

    public void removeProvider(String interfaceName, String providerAddr) throws Exception {
        cf.delete().forPath("/tmh-rpc/" + interfaceName + "/" + providerAddr);
    }

    public void watchInterface(String interfaceName,
            Consumer<PathChildrenCacheEvent> callback) throws Exception {
        if (filter.putIfAbsent(interfaceName, "") != null) {
            return;
        }
        PathChildrenCache childrenCache = new PathChildrenCache(cf, "/tmh-rpc/" + interfaceName,
                true);
        childrenCache.getListenable()
                .addListener((client, event) -> callback.accept(event));
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }

    public void shutdown() {
        cf.close();
    }

    public void start() {
        cf.start();
    }
}
