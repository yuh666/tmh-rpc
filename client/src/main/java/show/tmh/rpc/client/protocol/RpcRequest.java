package show.tmh.rpc.client.protocol;


import lombok.Data;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class RpcRequest implements Serializable {

    private static transient AtomicLong requestIdGenerator = new AtomicLong();

    private Long requestId;
    private String interfaceName;
    private Integer methodCode;
    private Long expectTimeOut;
    private Object[] args;

    public RpcRequest() {
        this.requestId = requestIdGenerator.getAndIncrement() & 0x7fffffffffffffffL;
    }

}
