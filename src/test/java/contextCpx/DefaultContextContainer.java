package contextCpx;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DefaultContextContainer extends AbstractCucumberContext{
    private transient Class<? extends AbstractCucumberContext> contextContainerClass;
    private List<AbstractCucumberContext> contexts = new ArrayList<>();

    public DefaultContextContainer(Class<? extends AbstractCucumberContext> contextContainerClass) {
        this.contextContainerClass = contextContainerClass;
    }

    public AbstractCucumberContext getCurrentContext() {
        if (this.contexts.size() == 0) {
            this.createNewContext();
        }

        return this.contexts.get(contexts.size()-1);
    }

    public AbstractCucumberContext createNewContext() {
        AbstractCucumberContext val = this.initNewContext();
        this.contexts.add(val);
        return val;
    }

    public List<AbstractCucumberContext> getContexts() {
        return this.contexts;
    }

    public void setCurrentContext(AbstractCucumberContext context) {
        if (this.contexts.contains(context)) {
            this.contexts.remove(context);
        }

        this.contexts.add(context);
    }

    private AbstractCucumberContext initNewContext() {
        try {
            return this.contextContainerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException var2) {
            log.error(var2);
            throw new RuntimeException(var2);
        }
    }
}