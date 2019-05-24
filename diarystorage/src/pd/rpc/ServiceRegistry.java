package pd.rpc;

import java.util.HashMap;

public class ServiceRegistry {

    public static String getTypeName(Class<?> interfaceClass) {
        return interfaceClass.getCanonicalName();
    }

    private boolean frozen = false;

    private final HashMap<String, Class<?>> registry = new HashMap<>();

    public void freeze() {
        frozen = true;
    }

    public Class<?> getType(String typeName) {
        return registry.get(typeName);
    }

    public boolean register(Class<?> interfaceClass, Class<?> implementationClass) {
        if (frozen) {
            return false;
        }
        registry.put(getTypeName(interfaceClass), implementationClass);
        return true;
    }
}
