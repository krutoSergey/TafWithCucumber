package steps;

import contextMy.TAContext;

public class BaseSteps {

    protected TAContext getContext() {
        return TAContext.getInstance();
    }
}
