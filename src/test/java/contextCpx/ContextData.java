package contextCpx;

import java.util.HashMap;
import java.util.Map;

public class ContextData {
    private Map<String,Object> context = new HashMap<>();

    public Map<String, Object> getContext() {
        return this.context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
