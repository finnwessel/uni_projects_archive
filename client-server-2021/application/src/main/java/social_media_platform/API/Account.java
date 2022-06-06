package social_media_platform.API;

import social_media_platform.dto.account.AccountDto;
import social_media_platform.filter.JwtFilter;
import social_media_platform.filter.JwtVerification;
import social_media_platform.filter.Role;
import social_media_platform.services.AccountService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("/account")
public class Account {

    @Inject
    private AccountService accountService;


    /**
     * Returns the current users account
     *
     * @param sC a security context
     * @return the authorized account
     */
    @GET
    @JwtVerification({Role.User, Role.Admin})
    @Produces("application/json")
    public AccountDto getAuthedAccount(@Context SecurityContext sC) {
        return accountService.getAccountById(((JwtFilter.User) sC.getUserPrincipal()).getId());
    }

    /**
     * Updates the current users account
     *
     * @param sC a security context
     * @param accountDto an account
     */
    @PUT
    @JwtVerification({Role.User, Role.Admin})
    @Produces("application/json")
    public void updateAuthedAccount(@Context SecurityContext sC, AccountDto accountDto) {
        JwtFilter.User user = (JwtFilter.User) sC.getUserPrincipal();
        accountDto.setId(user.getId());
        accountService.updateAccount(accountDto);
    }

    // Admin Routes below

    /**
     * Returns the account with a given id
     *
     * @param id account id
     * @return the corresponding account
     */
    @GET
    @JwtVerification({ Role.Admin })
    @Path("{id}")
    @Produces("application/json")
    public AccountDto getAccount(@PathParam("id") int id) {
        return accountService.getAccountById(id);
    }

    /**
     * Updates the account with a given id
     *
     * @param id account id
     * @param accountDto an account
     */
    @PUT
    @JwtVerification({ Role.Admin })
    @Path("{id}")
    @Produces("application/json")
    public void updateAccount(@PathParam("id") int id, AccountDto accountDto) {
        accountService.updateAccount(accountDto);
    }

    /**
     * Deletes the account with a given id
     *
     * @param id account id
     * @return the deleted account
     */
    @DELETE
    @JwtVerification({ Role.Admin })
    @Path("{id}")
    @Produces("application/json")
    public long deleteAccount(@PathParam("id") long id) {
        return accountService.deleteAccount(id);
    }
}
