package contextMy;

import steps.ScenarioDefinitions;

import java.util.HashMap;
import java.util.Map;

public class TAContext {
    private static ThreadLocal<TAContext> instance = ThreadLocal.withInitial(() -> new TAContext());
    protected ScenarioDefinitions scenarioDefinitions;
    private Map<ContextType, Object> contexts = new HashMap();

    private TAContext(){
        scenarioDefinitions = new ScenarioDefinitions();
    }

    public static TAContext getInstance() {
        return instance.get();
    }

    protected Map<ContextType, Object> getContext() {
        return this.contexts;
    }

    public UserContext getUserContext() {
        return (UserContext)contexts.computeIfAbsent(ContextType.USER, (e) -> new UserContext());
    }
}
