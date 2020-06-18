package show.tmh.rpc.client.core;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * TODO:DOCUMENT ME!
 *
 * @author yuhao
 * @date 2020/6/18 3:01 下午
 */
public class RegistryCache {

    private RpcRegistry registry;

    private HashMap<String, List<String>> cacheMap = new HashMap<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public RegistryCache(RpcRegistry registry) {
        this.registry = registry;
    }


    public String chooseAddr(String interfaceName) {
        rwLock.writeLock().lock();
        try {
            List<String> addrList = cacheMap.get(interfaceName);
            if (addrList == null || addrList.isEmpty()) {
                List<String> provider = registry.getProvider(interfaceName);
                if (provider == null || provider.isEmpty()) {
                    throw new RuntimeException("provider list is empty");
                }
                addrList = provider;
                cacheMap.put(interfaceName, provider);
                registry.watchInterface(interfaceName, event -> {
                    rwLock.writeLock().lock();
                    try {
                        try {
                            cacheMap.put(interfaceName, registry.getProvider(interfaceName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } finally {
                        rwLock.writeLock().unlock();
                    }
                });
            }
            //先将lb写死在这里
            int index = ThreadLocalRandom.current().nextInt(0, addrList.size());
            return addrList.get(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void removeProvider(String interfaceName, String providerAddr) {
        rwLock.writeLock().lock();
        try {
            cacheMap.get(interfaceName).remove(providerAddr);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        rwLock.readLock().lock();
        rwLock.readLock().unlock();
        rwLock.writeLock().lock();
    }
}
