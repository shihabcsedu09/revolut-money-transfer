package me.shihab.revolut.integrationtest;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import me.shihab.revolut.App;
import me.shihab.revolut.AppConfiguration;
import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.exception.FailureStatusCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class AccountResourceIntegrationTest {

    private static Client testClient;
    private static String baseURL;
    private static String accountResourcePath;

    @Rule
    public final DropwizardAppRule<AppConfiguration> appRule = new DropwizardAppRule<>(App.class,
            ResourceHelpers.resourceFilePath("config_test.yml"));

    @Before
    public void setup() {
        testClient = new JerseyClientBuilder(appRule.getEnvironment()).build("test client");
        baseURL = String.format("http://localhost:%d/", appRule.getLocalPort());
        accountResourcePath = baseURL + "account/";
    }


    @Test
    public void givenValidAccountCreationRequest_ShouldReturnCorrectResponse() {
        AccountDTO testAccount = new AccountDTO("Test Account Name", BigDecimal.valueOf(100.02));
        AccountDTO testAccountCreated = new AccountDTO("Test Account Name", BigDecimal.valueOf(100.02));
        testAccountCreated.setId(1);

        Response accountCreationResponse = createAccount(testAccount);

        assertThat(accountCreationResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(accountCreationResponse.readEntity(AccountDTO.class)).isEqualToComparingFieldByField(testAccountCreated);
    }

    @Test
    public void givenInValidAccountCreationRequest_ShouldReturnCorrectResponse() {
        AccountDTO testAccount = new AccountDTO("Test Account Name", BigDecimal.valueOf(-100.02));

        Response accountCreationResponse = createAccount(testAccount);

        assertThat(accountCreationResponse.getStatus()).isEqualTo(422);

    }


    @Test
    public void givenValidAccountFetchRequest_ShouldReturnCorrectResponse() {
        AccountDTO testAccount = new AccountDTO("Test Account Name", BigDecimal.valueOf(100.0200));

        Response accountCreationResponse = createAccount(testAccount);

        AccountDTO createdAccount = accountCreationResponse.readEntity(AccountDTO.class);

        long createdAccountID = 1;

        Response fetchAccountResponse = testClient.target(
                accountResourcePath + createdAccountID)
                .request()
                .get();

        assertThat(fetchAccountResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(fetchAccountResponse.readEntity(AccountDTO.class)).isEqualTo(createdAccount);
    }

    @Test
    public void givenNonExistingAccountIdForFetching_ShouldReturnCorrectResponse() {

        long nonExistingAccountId = 1;

        Response fetchAccountResponse = testClient.target(
                accountResourcePath + nonExistingAccountId)
                .request()
                .get();

        assertThat(fetchAccountResponse.getStatus()).isEqualTo(FailureStatusCode.ACCOUNT_NOT_FOUND.statusCode());

    }


    private Response createAccount(AccountDTO accountDTO) {
        return testClient.target(
                accountResourcePath)
                .request()
                .post(Entity.json(accountDTO));
    }
}
