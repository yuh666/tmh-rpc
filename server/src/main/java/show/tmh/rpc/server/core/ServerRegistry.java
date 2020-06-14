package show.tmh.rpc.server.core;


import show.tmh.rpc.client.annotation.RpcMember;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zy-user
 */
public class ServerRegistry {

    private Map<String, Map<Integer, Method>> methodRegistryMap = new HashMap<>();
    private Map<String, Object> instanceRegistryMap = new HashMap<>();

    public static final ServerRegistry INSTANCE = new ServerRegistry();

    private ServerRegistry() {

    }

    public Method getMethod(String interfaceName, int rpcMember) {
        Map<Integer, Method> handleMap = methodRegistryMap.get(interfaceName);
        if (handleMap == null) {
            return null;
        }
        return handleMap.get(rpcMember);
    }


    public Object getInstance(String interfaceName) {
        return instanceRegistryMap.get(interfaceName);
    }

    public void register(Object instance) {
        Class<?> classInterface = instance.getClass().getInterfaces()[0];
        Map<Integer, Method> methodHandleMap = methodRegistryMap.get(classInterface.getName());
        if (methodHandleMap == null) {
            methodHandleMap = new HashMap<>();
            methodRegistryMap.put(classInterface.getName(), methodHandleMap);

        }
        Method[] declaredMethods = classInterface.getDeclaredMethods();
        for (Method method : declaredMethods) {
            RpcMember annotation = method.getAnnotation(RpcMember.class);
            if (annotation != null) {
                method.setAccessible(true);
                methodHandleMap.put(annotation.value(), method);
            }
        }
        instanceRegistryMap.put(classInterface.getName(), instance);
    }
}
