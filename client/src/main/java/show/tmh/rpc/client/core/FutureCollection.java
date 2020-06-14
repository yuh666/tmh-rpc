package show.tmh.rpc.client.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author zy-user
 */
public class FutureCollection {

    private FutureCollection() {

    }

    public static FutureCollection INSTANCE = new FutureCollection();

    private ConcurrentHashMap<Long, ResponseFuture> respTable = new ConcurrentHashMap<>();

    public ResponseFuture register(Long id) {
        ResponseFuture responseFuture = new ResponseFuture();
        respTable.put(id, responseFuture);
        return responseFuture;
    }

    public ResponseFuture get(Long id) {
        return respTable.get(id);
    }

}
