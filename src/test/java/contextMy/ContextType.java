package contextMy;

public enum ContextType {
    USER(UserContext.class);

    private Class value;

    ContextType(Class<? extends AbstractContext> val) {
        this.value = val;
    }

    public Class<? extends AbstractContext> getValue(ContextType type) {
        return type.value;
    }
}
