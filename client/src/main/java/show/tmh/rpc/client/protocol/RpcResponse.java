package show.tmh.rpc.client.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private Long responseId;
    private Object result;
    private Throwable throwable;

}
