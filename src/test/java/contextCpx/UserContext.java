package contextCpx;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContext extends  AbstractCucumberContext {
    private String login;
    private String password;

    public void clearContext()
    {
        this.login = null;
        this.password = null;
    }
}
