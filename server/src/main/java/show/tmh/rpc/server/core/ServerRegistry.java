package show.tmh.rpc.server.core;


import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zy-user
 */
public class ServerRegistry {

    private Map<String, Map<Integer, MethodHandle>> methodRegistryMap = new HashMap<>();
    private Map<String, Object> instanceRegistryMap = new HashMap<>();

    public static final ServerRegistry INSTANCE = new ServerRegistry();


    private ServerRegistry() {

    }

    public MethodHandle getMethod(String interfaceName, int rpcMember) {
        Map<Integer, MethodHandle> handleMap = methodRegistryMap.get(interfaceName);
        if (handleMap == null) {
            return null;
        }
        return handleMap.get(rpcMember);
    }


    public Object getInstance(String interfaceName) {
        return instanceRegistryMap.get(interfaceName);
    }

}
