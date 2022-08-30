package contextMy;

import java.util.HashMap;
import java.util.Map;

abstract public class AbstractContext {
    public final ContextType TYPE;
    private final Map<String, Object> context = new HashMap<>();

    protected void clearContext() {
        this.context.clear();
    }

    AbstractContext(ContextType type) {
        this.TYPE = type;
    }

    public Object get(String name) {
        return context.get(name);
    }

    public void set(String name, Object value) {
        context.put(name, value);
    }
}
