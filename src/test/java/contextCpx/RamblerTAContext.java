package contextCpx;

import java.util.List;

public class RamblerTAContext extends AbstractCucumberContext{

    private static ThreadLocal<RamblerTAContext> instance = new ThreadLocal<RamblerTAContext>() {
        protected RamblerTAContext initialValue() {
            return new RamblerTAContext();
        }
    };

    public static RamblerTAContext getInstance() {
        return (RamblerTAContext)instance.get();
    }

    public UserContext getUserContext() {
        return (UserContext)getCucumberContext().computeIfAbsent("userContext", (e) -> {
            return new UserContext();
        });
    }

    public void setUserContext(UserContext context) {
        put("userContext", context);
    }

    public List<? extends AbstractCucumberContext> getUserContexts() {
        return this.getUserContextContainer().getContexts();
    }

    protected DefaultContextContainer getUserContextContainer() {
        return (DefaultContextContainer)getCucumberContext().computeIfAbsent("userContextContainer", (e) -> {
            return new DefaultContextContainer(UserContext.class);
        });
    }
}
