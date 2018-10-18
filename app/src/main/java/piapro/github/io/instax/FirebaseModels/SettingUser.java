package piapro.github.io.instax.FirebaseModels;

public class SettingUser {

    private User user;
    private UserAccountSettings userSettings;

    public SettingUser(User user, UserAccountSettings userSettings) {
        this.user = user;
        this.userSettings = userSettings;
    }

    public SettingUser() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserAccountSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public String toString() {
        return "SettingUser{" +
                "user=" + user +
                ", userSettings=" + userSettings +
                '}';
    }
}
