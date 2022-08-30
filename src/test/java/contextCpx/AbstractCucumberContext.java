package contextCpx;

import java.util.Map;

abstract public class AbstractCucumberContext {
    public AbstractCucumberContext() {
    }

    protected static Object get(String name) {
        return CucumberContext.get(name);
    }

    protected static void put(String name, Object value) {
        CucumberContext.put(name, value);
    }

    public static Map<String, Object> getCucumberContext() {
        return CucumberContext.getInstance().getContext();
    }
}
