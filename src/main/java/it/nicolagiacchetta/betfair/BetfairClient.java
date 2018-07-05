package it.nicolagiacchetta.betfair;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nicolagiacchetta.betfair.entities.EventResult;
import it.nicolagiacchetta.betfair.entities.Filter;
import it.nicolagiacchetta.betfair.entities.LoginResponse;
import it.nicolagiacchetta.betfair.entities.RequestBody;
import it.nicolagiacchetta.betfair.exceptions.RequestFailedException;
import it.nicolagiacchetta.betfair.utils.ApacheComponentsHttpClient;
import it.nicolagiacchetta.betfair.utils.HttpClient;
import it.nicolagiacchetta.betfair.utils.HttpResponse;
import it.nicolagiacchetta.betfair.utils.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static it.nicolagiacchetta.betfair.utils.BetfairUtils.defaultHeaders;

public class BetfairClient {

    private String loginAppKey;
    private String loginSessionToken;

    // Login & Session Management
    public static final String IDENTITY_SSO_URL = "https://identitysso.betfair.com/api";
    public static final String LOGIN_URL = IDENTITY_SSO_URL + "/login";
    public static final String LOGOUT_URL = IDENTITY_SSO_URL + "/logout";
    public static final String SESSION_KEEPALIVE_URL = IDENTITY_SSO_URL + "/keepAlive";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";

    // Betting
    public static final String BETTING_API_URL = "https://api.betfair.com/exchange/betting/rest/v1.0";
    public static final String LIST_EVENTS_URL = BETTING_API_URL + "/listEvents/";

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    private BetfairClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public LoginResponse login(String username, String password, String appKey) throws Exception {
        checkArgumentsNonNull(username, password, appKey);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(USERNAME_PARAM, username);
        queryParams.put(PASSWORD_PARAM, password);
        String uri = HttpUtils.appendQueryString(LOGIN_URL, queryParams);
        LoginResponse loginResponse = sendSessionManagementRequest(appKey, uri);
        this.loginAppKey = appKey;
        this.loginSessionToken = loginResponse.getToken();
        return loginResponse;
    }

    public LoginResponse keepAliveSession() throws Exception {
        return keepAliveSession(this.loginAppKey, this.loginSessionToken);
    }

    public LoginResponse keepAliveSession(String appKey, String sessionToken) throws Exception {
        checkArgumentsNonNull(appKey, sessionToken);
        return sendSessionManagementRequest(appKey, sessionToken, SESSION_KEEPALIVE_URL);
    }

    public LoginResponse logout() throws Exception {
        return logout(this.loginAppKey, this.loginSessionToken);
    }

    public LoginResponse logout(String appKey, String sessionToken) throws Exception {
        checkArgumentsNonNull(appKey, sessionToken);
        LoginResponse loginResponse = sendSessionManagementRequest(appKey, sessionToken, LOGOUT_URL);
        this.loginAppKey = null;
        this.loginSessionToken = null;
        return loginResponse;
    }

    private LoginResponse sendSessionManagementRequest(String appKey, String url) throws Exception {
        return sendSessionManagementRequest(appKey, null, url);
    }

    private LoginResponse sendSessionManagementRequest(String appKey, String sessionToken, String url) throws Exception {
        Map<String, String> headers = defaultHeaders(appKey, sessionToken);
        HttpResponse response = this.httpClient.post(url, headers);
        return parseHttpResponseOrFail(response, LoginResponse.class);
    }

    public EventResult[] listEvents(Filter filter) throws Exception {
        checkArgumentsNonNull(this.loginAppKey, this.loginSessionToken, filter);
        Map<String, String> headers = defaultHeaders(this.loginAppKey, this.loginSessionToken);
        RequestBody body = new RequestBody.Builder(filter).build();
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpResponse response = this.httpClient.post(LIST_EVENTS_URL, headers, jsonBody);
        return parseHttpResponseOrFail(response, EventResult[].class);
    }

    public EventResult[] listEvents(String appKey, String sessionToken, Filter filter) throws Exception {
        checkArgumentsNonNull(appKey, sessionToken, filter);
        Map<String, String> headers = defaultHeaders(appKey, sessionToken);
        RequestBody body = new RequestBody.Builder(filter).build();
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpResponse response = this.httpClient.post(LIST_EVENTS_URL, headers, jsonBody);
        return parseHttpResponseOrFail(response, EventResult[].class);
    }

    private void checkArgumentsNonNull(Object... args){
        for(Object arg : args)
            if(arg == null)
                throw new IllegalArgumentException("Invalid argument: null value not allowed");
    }

    private <R> R parseHttpResponseOrFail(HttpResponse httpResponse, Class<R> clazz) throws RequestFailedException, IOException {
        if(httpResponse.getStatusCode() != 200) {
            throw new RequestFailedException("Request failed. Returned status code " + httpResponse.getStatusCode());
        }
        return objectMapper.readValue(httpResponse.getContent(), clazz);
    }

    public static class Builder {

        private HttpClient httpClient;
        private ObjectMapper objectMapper;

        public Builder withHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public BetfairClient build() {
            if(this.httpClient == null)
                this.httpClient = ApacheComponentsHttpClient.newInstance();
            if(this.objectMapper == null)
                this.objectMapper = new ObjectMapper();
            return new BetfairClient(this.httpClient, this.objectMapper);
        }
    }
}
