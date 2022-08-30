package contextMy;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserContext extends AbstractContext {
    private String login;
    private String password;

    UserContext() {
        super(ContextType.USER);
    }

    @Override
    protected void clearContext() {
        login = new String();
        password = new String();
        super.clearContext();
    }
}
