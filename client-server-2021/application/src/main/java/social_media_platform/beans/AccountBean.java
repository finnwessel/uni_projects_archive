package social_media_platform.beans;

import lombok.Data;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.dto.account.NewAccountDto;
import social_media_platform.services.AccountService;
import social_media_platform.utils.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Data
@Named("account")
@SessionScoped
public class AccountBean implements Serializable {

    @Inject
    private AccountService service;

    private NewAccountDto newAccount;

    private AccountDto currentAccount;

    private String verificationCode;

    @PostConstruct
    private void init() {
        newAccount = new NewAccountDto();
        currentAccount = getCurrentAccount();
    }

    /**
     * Returns the current users account
     *
     * @return the current users account
     */
    public AccountDto getCurrentAccount() {
        return SessionUtils.getAccount();
    }

    /**
     * Creates a new account
     *
     * @return the created account
     */
    public String createAccount() {
        service.createAccount(newAccount);
        newAccount = new NewAccountDto();
        return "pretty:verification";
    }

    /**
     * Updates the current account
     */
    public void updateAccount() {
        service.updateAccount(currentAccount);
    }

    /**
     * Verifies the current account
     *
     * @return the route depending on the verification status
     */
    public String verifyAccount() {
        currentAccount = getCurrentAccount();
        boolean isVerified = service.verifyAccount(currentAccount.getId(), verificationCode);
        SessionUtils.setVerified(isVerified);
        if (isVerified) {
            return "pretty:root";
        } else {
            return "pretty:verification";
        }
    }

    /**
     * Returns the followers of the current user
     *
     * @return the current users followers
     */
    public List<AccountDto> getFollower() {
        return service.accountFollower(getCurrentAccount().getId());
    }

    /**
     * Returns the accounts the current user is following
     *
     * @return the followed accounts of the current user
     */
    public List<AccountDto> getFollows() {
        return service.accountFollows(getCurrentAccount().getId());
    }
}
