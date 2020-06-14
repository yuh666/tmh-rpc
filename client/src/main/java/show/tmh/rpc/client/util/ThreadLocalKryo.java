package show.tmh.rpc.client.util;

import com.esotericsoftware.kryo.Kryo;
import show.tmh.rpc.client.protocol.RpcRequest;
import show.tmh.rpc.client.protocol.RpcResponse;

public class ThreadLocalKryo extends ThreadLocal<Kryo> {

    public static final ThreadLocalKryo kryo = new ThreadLocalKryo();

    @Override
    protected Kryo initialValue() {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    }


}
