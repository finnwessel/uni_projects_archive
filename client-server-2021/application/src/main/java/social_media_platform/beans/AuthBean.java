package social_media_platform.beans;

import lombok.Data;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.services.AuthService;
import social_media_platform.utils.SessionUtils;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Data
@Named("auth")
@SessionScoped
public class AuthBean implements Serializable {

    private String username;
    private String password;
    private boolean loggedIn = false;

    @Inject
    private AuthService service;

    /**
     * Validates the username and password
     *
     * @return the route depending on the validation status
     */
    public String validateUsernameAndPassword() {
        AccountDto account = service.getAccountWithCredentials(username, password);

        if (account != null) {
            SessionUtils.setAccount(account);
            SessionUtils.setVerified(service.isVerified(account.getId()));
            this.username = null;
            this.password = null;
            this.loggedIn = true;
            return "pretty:root";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Incorrect Username or Password",
                            "Please provide correct combination username and Password"
                    )
            );
            this.password = null;
            return "pretty:login";
        }
    }

    /**
     * Logs out the user
     *
     * @return the login page
     */
    public String logout() {
        SessionUtils.invalidate();
        this.loggedIn = false;
        return "pretty:login";
    }
}
