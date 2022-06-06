package social_media_platform.beans;

import lombok.Data;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.services.AccountService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Data
@Named("admin")
public class AdminBean {

    @Inject
    private AccountService service;

    public List<AccountDto> accounts;

    @PostConstruct
    private void init() {
        accounts = getAllAccounts();
    }

    /**
     * Returns all accounts
     *
     * @return all accounts
     */
    public List<AccountDto> getAllAccounts() {
        return service.getAllAccounts();
    }

    /**
     * Deletes an account
     *
     * @param id account id
     */
    public void deleteAccount(long id) {
        service.deleteAccount(id);
        accounts = getAccounts();
    }
}
