package igogo;

import igogo.model.Account;
import igogo.repository.AccountRepository;
import javafx.application.Application;
import org.aspectj.lang.annotation.Before;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by rgavrilov on 9/22/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IgogoApplication.class)
@WebAppConfiguration
public class AccountControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Account account;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }


    @org.junit.Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        this.accountRepository.deleteAllInBatch();
        Account acct = new Account();
        acct.setFirstName("firstName");
        acct.setLastName("lastName");
        acct.setUsername("username");
        acct.setEmail("test@test.com");

        this.account = accountRepository.save(acct);
    }


    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/secure/user/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUser() throws Exception {

        mockMvc.perform(get("/secure/user/{id}",this.account.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(this.account.getId().intValue())))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.email", is("test@test.com")));
    }


    @Test
    public void createAccount() throws Exception {
        Account newAccount = new Account();
        newAccount.setFirstName("new first name");
        newAccount.setLastName("new last name");
        newAccount.setUsername("new username");
        newAccount.setEmail("newemail@test.com");

        String accountJson = json(newAccount);

        mockMvc.perform(post("/secure/user/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateAccount() throws Exception {
        this.account.setUsername("boby");
        String accountJson = json(this.account);

        mockMvc.perform(post("/secure/user/{id}",this.account.getId())
        .contentType(contentType)
        .content(accountJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/secure/user/{id}",this.account.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(this.account.getId().intValue())))
                .andExpect(jsonPath("$.username", is("boby")));

    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);

        return mockHttpOutputMessage.getBodyAsString();
    }
}
