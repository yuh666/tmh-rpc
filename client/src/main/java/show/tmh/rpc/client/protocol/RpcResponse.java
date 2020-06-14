package show.tmh.rpc.client.protocol;

import lombok.Data;

@Data
public class RpcResponse {

    private Long responseId;
    private Object result;

}
