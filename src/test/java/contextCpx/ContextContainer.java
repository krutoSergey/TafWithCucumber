package contextCpx;

import java.util.List;

public interface ContextContainer <T extends AbstractCucumberContext> {
    T getCurrentContext();

    T createNewContext();

    List<T> getContexts();
}
