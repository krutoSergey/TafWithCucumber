package context;


import java.util.HashMap;
import java.util.Map;

public class SingleTAContext {
    private static ThreadLocal<SingleTAContext> instance = ThreadLocal.withInitial(() -> new SingleTAContext());
    private final Map<String, Object> context = new HashMap<>();

    protected SingleTAContext() {
    }

    public static SingleTAContext getInstance() {
        return instance.get();
    }

    public void deleteKey(String key) {
        context.remove(key);
    }

    public <T> void put(String key, T object) {
        context.put(key, object);
    }

    public Object get(String key) {
        Object object;
        try {
            object = context.get(key);
        } catch (NullPointerException e) {
            throw new AssertionError(String.format("Object with key %s doesn't exist!", key));
        }
        return object;
    }

    public <T> T get(String key, Class<T> userClass) {
        Object object;
        try {
            object = context.get(key);
        } catch (NullPointerException e) {
            throw new AssertionError(String.format("Object with key %s doesn't exist!", key));
        }
        return userClass.cast(object);
    }

}
