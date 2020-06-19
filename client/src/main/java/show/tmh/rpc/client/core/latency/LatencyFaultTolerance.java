package show.tmh.rpc.client.core.latency;

/**
 * @author <a href="mailto:zhouzhihui@zhangyue.com">wisdom</a>
 * Date: 2020-06-18 18:25
 * version: 1.0
 * Description:服务延时故障容忍性接口组件
 **/
public interface LatencyFaultTolerance {

    /**
     * 更新服务实例延时信息
     *
     * @param instanceAddr   实例地址
     * @param currentLatency 当前延时
     */
    void updateFaultItem(final String instanceAddr, final long currentLatency);

    /**
     * 当前服务实例是否可用
     *
     * @param instanceAddr 实例地址
     * @return true:可用 false:不可用
     */
    boolean isAvailable(final String instanceAddr);

    /**
     * 移除服务实例
     *
     * @param instanceAddr 服务实例地址
     */
    void remove(final String instanceAddr);

    /**
     * 服务实例都不可用选择一台server
     *
     * @return 服务实例地址
     */
    String pickOneAtLeast();

    /**
     * 根据code编码延时时间
     *
     * @param instanceAddr 服务实例地址
     * @param code         编码
     */
    void updateLatencyByCode(String instanceAddr, String code);
}
