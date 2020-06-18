package show.tmh.rpc.client.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private Long responseId;
    /**
     * 响应码
     * 0.正常
     * 1.重启
     * 2.压力过大
     */
    private Byte responseCode = 0;
    private Object result;
    private Throwable throwable;

}
