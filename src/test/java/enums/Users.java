package enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum Users {
    RAMBLER_STANDARD_USER(new User("simpleautomation@rambler.ru", "S1mpl34ut0m4ti0n"));

    public String getUsername() {
        return user.getUsername();
    }

    public String getPassword() {
        return user.getPassword();
    }

    @Value
    @Builder(toBuilder = true)
    @Getter
    final static public class User {
        private String username;
        private String password;
    }

    @NonNull
    private final User user;
}
