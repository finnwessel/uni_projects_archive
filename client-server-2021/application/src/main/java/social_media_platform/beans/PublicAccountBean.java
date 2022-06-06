package social_media_platform.beans;

import lombok.Data;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.services.AccountService;
import social_media_platform.utils.SessionUtils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Data
@Named("publicAccount")
@SessionScoped
public class PublicAccountBean implements Serializable {

    @Inject
    private AccountService service;

    /**
     * Returns an account by the given id
     *
     * @param id an account id
     * @return the corresponding account
     */
    public AccountDto getAccount(long id) {
        return service.getAccountById(id);
    }

    /**
     * Returns the accounts following the given account
     *
     * @param id an account id
     * @return the followers of the account
     */
    public List<AccountDto> getFollower(long id) {
        return service.accountFollower(id);
    }

    /**
     * Returns the accounts the given account is following
     *
     * @param id an account id
     * @return the accounts the given account is following
     */
    public List<AccountDto> getFollows(long id) {
        return service.accountFollows(id);
    }

    /**
     * Follows the given account
     *
     * @param id an account id
     */
    public void follow(long id) {
        service.followAccount(SessionUtils.getAccount().getId(), id);
    }

    /**
     * Unfollows the given account
     *
     * @param id an account id
     */
    public void unfollow(long id) {
        service.unfollowAccount(SessionUtils.getAccount().getId(), id);
    }

    /**
     * Returns an account that is followed by the current user if this account is followed
     *
     * @param follows the account that is followed by the current user
     * @return the account that is followed
     */
    public boolean follows(long follows) {
        return service.accountFollows(SessionUtils.getAccount().getId(), follows);
    }

    /**
     * Returns true or false depending on if the current user has a user id
     *
     * @param id user id
     * @return has user id or not
     */
    public boolean hasUserId(long id) {
        return SessionUtils.getAccount().getId() == id;
    }

    /**
     * Returns if a user is logged in
     *
     * @return true or false depending on login status
     */
    public boolean isLoggedIn() {
        return SessionUtils.isLoggedIn();
    }
}
