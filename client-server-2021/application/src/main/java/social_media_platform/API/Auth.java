package social_media_platform.API;

import social_media_platform.dto.account.AccountDto;
import social_media_platform.dto.account.NewAccountDto;
import social_media_platform.dto.auth.CredentialsDto;
import social_media_platform.dto.auth.TokenDto;
import social_media_platform.services.AccountService;
import social_media_platform.services.AuthService;
import social_media_platform.services.JwtService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/auth")
public class Auth {

    @Inject
    private AccountService accountService;
    @Inject
    private AuthService authService;
    @Inject
    private JwtService jwtService;

    /**
     * Returns a new JTW-Token
     *
     * @param credentialsDto the credentials
     * @return a JWT token
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public TokenDto getJwt(CredentialsDto credentialsDto) {
        AccountDto account = authService.getAccountWithCredentials(credentialsDto.getUsername(), credentialsDto.getPassword());
        if (account == null) {
            throw new WebApplicationException("No user matching the credentials", Response.Status.NOT_FOUND);
        } else {
            return new TokenDto(jwtService.createJWT("1", "api", String.valueOf(account.getId()), 60));
        }
    }

    /**
     * Registers a new account
     *
     * @param newAccountDto the new account
     */
    @POST
    @Path("/register")
    @Produces("application/json")
    public void registerAccount(NewAccountDto newAccountDto) {
        accountService.createAccount(newAccountDto);
    }
}
