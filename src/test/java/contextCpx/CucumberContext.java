package contextCpx;

import java.util.Map;

public class CucumberContext {
    private Long testRunId;
    private final ThreadLocal<ContextData> contextData = new ThreadLocal();
    private static CucumberContext _this = null;

    public static Object get(String key) {
        return getInstance().getContext().get(key);
    }

    public static void put(String key, Object value) {
        getInstance().getContext().put(key, value);
    }

    private CucumberContext() {
    }

    private ContextData getContextData() {
        if (this.contextData.get() == null) {
            this.contextData.set(new ContextData());
        }

        return (ContextData)this.contextData.get();
    }

    public Map<String, Object> getContext() {
        return this.getContextData().getContext();
    }

    public void setContext(Map<String, Object> context) {
        this.getContextData().setContext(context);
    }

    public static CucumberContext getInstance() {
        return _this == null ? (_this = new CucumberContext()) : _this;
    }

    public Long getTestRunId() {
        return this.testRunId;
    }

    public void setTestRunId(Long testRunId) {
        this.testRunId = testRunId;
    }
}
