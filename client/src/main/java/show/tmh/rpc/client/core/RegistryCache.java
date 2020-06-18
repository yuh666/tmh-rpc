package show.tmh.rpc.client.core;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

    public RegistryCache(RpcRegistry registry) {
        this.registry = registry;
    }


    public String chooseAddr(String interfaceName) {
        List<String> addrList = cacheMap.get(interfaceName);
        if (addrList == null || addrList.isEmpty()) {
            synchronized (interfaceName.intern()) {
                addrList = cacheMap.get(interfaceName);
                if (addrList == null || addrList.isEmpty()) {
                    try {
                        List<String> provider = registry.getProvider(interfaceName);
                        if (provider == null || provider.isEmpty()) {
                            throw new RuntimeException("provider list is empty");
                        }
                        addrList = new CopyOnWriteArrayList<>(provider);
                        cacheMap.put(interfaceName, addrList);
                        registry.watchInterface(interfaceName, event -> {
                            try {
                                cacheMap.put(interfaceName, registry.getProvider(interfaceName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
        }
        //先将lb写死在这里
        int index = ThreadLocalRandom.current().nextInt(0, addrList.size());
        return addrList.get(index);
    }


    public void removeProvider(String interfaceName, String providerAddr) {
        cacheMap.get(interfaceName).remove(providerAddr);
    }

}
