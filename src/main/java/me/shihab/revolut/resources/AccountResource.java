package me.shihab.revolut.resources;

import io.dropwizard.hibernate.UnitOfWork;
import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);
    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @Path("/{accountId}")
    @GET
    @UnitOfWork
    public AccountDTO getAccount(@PathParam("accountId") long accountId) throws RuntimeException {
        LOGGER.info("Received account fetch request for account id: {}", accountId);
        return accountService.get(accountId);
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public AccountDTO createAccount(@Valid AccountDTO account) {
        LOGGER.info("Received account creation request: {}", account.toString());
        return accountService.create(account);
    }
}
