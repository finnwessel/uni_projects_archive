package social_media_platform.services;

import org.mindrot.jbcrypt.*;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.entities.AccountEntity;
import social_media_platform.entities.MailVerificationEntity;

import javax.ejb.Stateful;
import javax.persistence.*;

@Stateful
public class AuthService {

    @PersistenceContext(unitName = "default", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    /**
     * Returns an account given its credentials
     *
     * @param username the username
     * @param password the password
     * @return the account
     */
    public AccountDto getAccountWithCredentials(String username, String password) {
        TypedQuery<AccountEntity> query = em.createQuery(
                "select a from AccountEntity a where a.username = :username", AccountEntity.class);
        AccountEntity account;
        try {
            account = query.setParameter("username", username)
                    .getSingleResult();

            if (BCrypt.checkpw(password, account.getPassword())) {
                return new AccountDto(account);
            }
        } catch (NoResultException e) {
            System.out.println("No account with given credentials found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns is an account is verified or not
     *
     * @param id account id
     * @return is the account verified or not
     */
    public boolean isVerified(long id) {
        MailVerificationEntity verification = em.find(MailVerificationEntity.class, id);
        if (verification == null) {
            return false;
        } else {
            return verification.isVerified();
        }
    }
}
