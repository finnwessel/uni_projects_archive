package social_media_platform.services;

import org.mindrot.jbcrypt.*;
import social_media_platform.dto.account.NewAccountDto;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.entities.AccountEntity;
import social_media_platform.entities.MailVerificationEntity;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateful
public class AccountService {

    @PersistenceContext(unitName = "default", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Inject
    private MailService mailService;

    /**
     * Returns all accounts
     *
     * @return all accounts
     */
    public List<AccountDto> getAllAccounts() {
        TypedQuery<AccountEntity> query = em.createQuery("select a from AccountEntity a", AccountEntity.class);

        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns an account by the given id
     *
     * @param id account id
     * @return the account
     */
    public AccountDto getAccountById(long id) {
        AccountEntity user = em.find(AccountEntity.class, id);
        if (user == null) {
            return null;
        } else {
            return new AccountDto(user);
        }
    }

    /**
     * Creates a new account
     *
     * @param newUserDto a new user
     */
    public void createAccount(NewAccountDto newUserDto) {
        AccountEntity account = new AccountEntity();
        account.setUsername(newUserDto.getUsername());
        account.setEmail(newUserDto.getEmail());
        account.setFirstname(newUserDto.getFirstname());
        account.setLastname(newUserDto.getLastname());
        account.setPassword(BCrypt.hashpw(newUserDto.getPassword(), BCrypt.gensalt()));
        em.persist(account);
        em.flush();
        createVerificationEntry(new AccountDto(account));
    }

    /**
     * Creates a new email verification entry for an account
     *
     * @param account an account
     */
    private void createVerificationEntry(AccountDto account) {
        MailVerificationEntity verification = new MailVerificationEntity();
        verification.setId(account.getId());
        em.persist(verification);
        sendVerification(account, verification.getCode());
    }

    /**
     * Sends a verification email to an account
     *
     * @param account an account
     * @param code the verification code
     */
    private void sendVerification(AccountDto account, String code) {
        mailService.sendVerificationMail(account, code);
    }

    /**
     * Verifies an account by the sent verification code
     *
     * @param id account id
     * @param code a verification code
     * @return
     */
    public boolean verifyAccount(long id, String code) {
        MailVerificationEntity verification = em.find(MailVerificationEntity.class, id);
        if (verification != null) {
            if (verification.getCode().equals(code)) {
                verification.setVerified(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates an account
     *
     * @param accountDto an account
     */
    public void updateAccount(AccountDto accountDto) {
        AccountEntity account = em.find(AccountEntity.class, accountDto.getId());
        if (account != null) {
            account.setUsername(accountDto.getUsername());
            account.setFirstname(accountDto.getFirstname());
            account.setLastname(accountDto.getLastname());
            account.setEmail(accountDto.getEmail());
            account.setPublicProfile(accountDto.isPublicProfile());
        }
    }

    /**
     * Deletes an account
     *
     * @param id account id
     * @return the deleted account
     */
    public long deleteAccount(long id) {
        AccountEntity account = em.find(AccountEntity.class, id);
        long accountId = account.getId();
        em.remove(account);
        return accountId;
    }

    /**
     * Follows an account
     *
     * @param id account id
     * @param followAccId account id to follow
     */
    public void followAccount(long id, long followAccId) {
        AccountEntity account = em.getReference(AccountEntity.class, id);
        AccountEntity followAccount = em.getReference(AccountEntity.class, followAccId);
        List<AccountEntity> follows = account.getFollows();
        follows.add(followAccount);
    }

    /**
     * Unfollows an account
     *
     * @param id account id
     * @param followAccId account id of followed account
     */
    public void unfollowAccount(long id, long followAccId) {
        AccountEntity account = em.getReference(AccountEntity.class, id);
        AccountEntity followAccount = em.getReference(AccountEntity.class, followAccId);
        List<AccountEntity> follows = account.getFollows();
        follows.remove(followAccount);
    }

    /**
     * Returns the accounts following an account
     *
     * @param id account id
     * @return the followers of the account
     */
    public List<AccountDto> accountFollower(long id) {
        TypedQuery<AccountEntity> query = em.createQuery("select a.follower from AccountEntity a where a.id = :account_id",
                AccountEntity.class);
        query.setParameter("account_id", id);

        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns the accounts an account is following
     *
     * @param id account id
     * @return the followed accounts
     */
    public List<AccountDto> accountFollows(long id) {
        TypedQuery<AccountEntity> query = em.createQuery("select a.follows from AccountEntity a where a.id = :account_id",
                AccountEntity.class);
        query.setParameter("account_id", id);

        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns a specific account that an account is following
     *
     * @param id account id
     * @param follows account id of followed account
     * @return the followed account
     */
    public boolean accountFollows(long id, long follows) {
        return accountFollows(id).stream().anyMatch(follower -> follower.getId() == follows);
    }
}
