package contextMy;

import steps.ScenarioDefinitions;

import java.util.HashMap;
import java.util.Map;

public class TAContext {
    private static ThreadLocal<TAContext> instance = ThreadLocal.withInitial(() -> new TAContext());
    protected ScenarioDefinitions scenarioDefinitions;

    private TAContext(){
        setScenarioDefinitions(new ScenarioDefinitions());
    }
    public static TAContext getInstance() {
        return instance.get();
    }

    private Map<ContextType, Object> contexts = new HashMap();

    protected Map<ContextType, Object> getContext() {
        return this.contexts;
    }

    public UserContext getUserContext() {
        return (UserContext)contexts.computeIfAbsent(ContextType.USER, (e) -> new UserContext());
    }

    private void setScenarioDefinitions(final ScenarioDefinitions scenarioDefinitions) {
        this.scenarioDefinitions = scenarioDefinitions;
    }
}
