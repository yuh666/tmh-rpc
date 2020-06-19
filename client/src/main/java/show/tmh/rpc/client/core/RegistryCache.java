package show.tmh.rpc.client.core;

import show.tmh.rpc.client.core.latency.LatencyFaultTolerance;
import show.tmh.rpc.client.core.latency.LatencyFaultToleranceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO:DOCUMENT ME!
 *
 * @author yuhao
 * @date 2020/6/18 3:01 下午
 */
public class RegistryCache {

    private RpcRegistry registry;

    private HashMap<String, List<String>> cacheMap = new HashMap<>();

    private LatencyFaultTolerance latencyFaultTolerance = new LatencyFaultToleranceImpl();

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
        for (int i = 0; i < addrList.size(); i++) {
            int pos = (index++) % addrList.size();

            String instanceAddr = addrList.get(pos);
            if (latencyFaultTolerance.isAvailable(instanceAddr)) {
                return instanceAddr;
            }
            return latencyFaultTolerance.pickOneAtLeast();
        }
        return null;
    }


    public void removeProvider(String interfaceName, String providerAddr) {
        cacheMap.get(interfaceName).remove(providerAddr);
    }

    /**
     * 更新服务实例延时信息
     *
     * @param instanceAddr   实例地址
     * @param currentLatency 当前延时
     */
    public void updateFaultItem(final String instanceAddr, final long currentLatency) {
        latencyFaultTolerance.updateFaultItem(instanceAddr, currentLatency);
    }

    /**
     * 移除服务实例
     *
     * @param instanceAddr 服务实例地址
     */
    public void remove(final String instanceAddr) {
        latencyFaultTolerance.remove(instanceAddr);
    }

    /**
     * 根据code编码延时时间
     *
     * @param instanceAddr 服务实例地址
     * @param code         编码
     */
    public void updateLatencyByCode(final String instanceAddr, final String code) {
        latencyFaultTolerance.updateLatencyByCode(instanceAddr, code);
    }

}
