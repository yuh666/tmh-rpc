package show.tmh.rpc.client.core.latency;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:zhouzhihui@zhangyue.com">wisdom</a>
 * Date: 2020-06-18 18:30
 * version: 1.0
 * Description:服务延时故障容忍性接口实现组件
 **/
public class LatencyFaultToleranceImpl implements LatencyFaultTolerance {

    private final ConcurrentHashMap<String, FaultItem> faultItemTable = new ConcurrentHashMap<String, FaultItem>(16);

    private long[] latencyMax = {50L, 100L, 550L, 1000L, 2000L, 3000L, 15000L};

    private long[] notAvailableDuration = {0L, 0L, 30000L, 60000L, 120000L, 180000L, 600000L};

    private final Map<String, Long> latencyCodeMap = new HashMap<>(1);

    public LatencyFaultToleranceImpl() {
        latencyCodeMap.put("S001", 20000L);
    }

    /**
     * 根据延时计算不可用时间
     *
     * @param currentLatency 当前延时
     * @return 服务实现不可用时间
     */
    private long calculateNotAvailableDuration(final long currentLatency) {
        for (int i = latencyMax.length - 1; i >= 0; i--) {
            if (currentLatency >= latencyMax[i])
                return this.notAvailableDuration[i];
        }
        return 0;
    }

    @Override
    public void updateFaultItem(String instanceAddr, long currentLatency) {
        long nextAvailableTimestamp = System.currentTimeMillis() + this.calculateNotAvailableDuration(currentLatency);

        FaultItem old = this.faultItemTable.get(instanceAddr);
        if (Objects.isNull(old)) {
            FaultItem faultItem = new FaultItem(instanceAddr);
            faultItem.setCurrentLatency(currentLatency);
            faultItem.setStartTimestamp(nextAvailableTimestamp);
            old = this.faultItemTable.putIfAbsent(instanceAddr, faultItem);
            if (old != null) {
                old.setCurrentLatency(currentLatency);
                old.setStartTimestamp(nextAvailableTimestamp);
            }
        } else {
            old.setCurrentLatency(currentLatency);
            old.setStartTimestamp(nextAvailableTimestamp);
        }

    }

    @Override
    public boolean isAvailable(String instanceAddr) {
        FaultItem faultItem = faultItemTable.get(instanceAddr);
        if (!Objects.isNull(faultItem)) {
            faultItem.isAvailable();
        }
        return true;
    }

    @Override
    public void remove(String instanceAddr) {
        faultItemTable.remove(instanceAddr);
    }

    @Override
    public String pickOneAtLeast() {
        List<FaultItem> tempList = new ArrayList<>(faultItemTable.values());
        if (!tempList.isEmpty()) {
            Collections.sort(tempList);
            return tempList.get(0).getInstanceAddr();
        }
        return null;
    }

    @Override
    public void updateLatencyByCode(String instanceAddr, String code) {
        Long currentLatency = latencyCodeMap.get(code);
        if (!Objects.isNull(currentLatency)) {
            this.updateFaultItem(instanceAddr, currentLatency);
        }
    }

    /**
     * 服务实例容错信息
     */
    class FaultItem implements Comparable<FaultItem> {
        /**
         * 实例地址
         */
        private final String instanceAddr;

        /**
         * 当前延时
         */
        private volatile long currentLatency;

        /**
         * 下次可用时间
         */
        private volatile long startTimestamp;

        public FaultItem(final String instanceAddr) {
            this.instanceAddr = instanceAddr;
        }

        @Override
        public int compareTo(final FaultItem other) {
            if (this.isAvailable() != other.isAvailable()) {
                if (this.isAvailable()) {
                    return -1;
                }
                if (other.isAvailable()) {
                    return 1;
                }
            }

            if (this.currentLatency < other.currentLatency) {
                return -1;
            } else if (this.currentLatency > other.currentLatency) {
                return 1;
            }

            if (this.startTimestamp < other.startTimestamp) {
                return -1;
            } else if (this.startTimestamp > other.startTimestamp) {
                return 1;
            }

            return 0;
        }

        public boolean isAvailable() {
            return (System.currentTimeMillis() - startTimestamp) >= 0;
        }

        @Override
        public int hashCode() {
            int result = getInstanceAddr() != null ? getInstanceAddr().hashCode() : 0;
            result = 31 * result + (int) (getCurrentLatency() ^ (getCurrentLatency() >>> 32));
            result = 31 * result + (int) (getStartTimestamp() ^ (getStartTimestamp() >>> 32));
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof FaultItem))
                return false;

            final FaultItem faultItem = (FaultItem) o;

            if (getCurrentLatency() != faultItem.getCurrentLatency())
                return false;
            if (getStartTimestamp() != faultItem.getStartTimestamp())
                return false;
            return getInstanceAddr() != null ? getInstanceAddr().equals(faultItem.getInstanceAddr()) : faultItem.getInstanceAddr() == null;

        }

        @Override
        public String toString() {
            return "FaultItem{" +
                    "name='" + instanceAddr + '\'' +
                    ", currentLatency=" + currentLatency +
                    ", startTimestamp=" + startTimestamp +
                    '}';
        }

        public String getInstanceAddr() {
            return instanceAddr;
        }

        public long getCurrentLatency() {
            return currentLatency;
        }

        public void setCurrentLatency(final long currentLatency) {
            this.currentLatency = currentLatency;
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(final long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }
    }

    @Test
    public void testLatencyFault() {
        LatencyFaultTolerance latencyFaultTolerance = new LatencyFaultToleranceImpl();
        String firstInstanceAddr = "localhost:8080";
        long firstInstanceLatency = 10L;
        latencyFaultTolerance.updateFaultItem(firstInstanceAddr, firstInstanceLatency);
        Assert.assertTrue(latencyFaultTolerance.isAvailable(firstInstanceAddr));

        firstInstanceLatency = 560L;
        latencyFaultTolerance.updateFaultItem(firstInstanceAddr, firstInstanceLatency);
        Assert.assertFalse(latencyFaultTolerance.isAvailable(firstInstanceAddr));

        String secondInstanceAddr = "localhost:8081";
        long secondInstanceLatency = 101L;
        latencyFaultTolerance.updateFaultItem(secondInstanceAddr, secondInstanceLatency);
        String actualInstanceAddr = latencyFaultTolerance.pickOneAtLeast();
        Assert.assertEquals(secondInstanceAddr, actualInstanceAddr);

        latencyFaultTolerance.remove(secondInstanceAddr);
        Assert.assertFalse(latencyFaultTolerance.isAvailable(firstInstanceAddr));
        actualInstanceAddr = latencyFaultTolerance.pickOneAtLeast();
        Assert.assertEquals(firstInstanceAddr, actualInstanceAddr);
    }

}