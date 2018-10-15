package it.nicolagiacchetta.betfair;

import it.nicolagiacchetta.betfair.entities.EventResult;
import it.nicolagiacchetta.betfair.entities.EventTypeResult;
import it.nicolagiacchetta.betfair.entities.MarketCatalogue;
import it.nicolagiacchetta.betfair.entities.MarketFilter;
import it.nicolagiacchetta.betfair.entities.LoginResponse;
import it.nicolagiacchetta.betfair.entities.MarketStartTime;
import it.nicolagiacchetta.betfair.entities.MarketType;

import java.util.Date;

public class Examples {

    public static void main(String [] args) throws Exception {

        // To run the examples substitute the value of the following variables with your real Betfair credentials
        String username = "YOUR_BETFAIR_USERNAME";
        String password = "YOUR_BETFAIR_PASSWORD";
        String appKey = "YOUR_BETFAIR_APPKEY";

        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 86400000);


        // Build the BetfairClientImpl
        try(BetfairClient betfairClient = BetfairClients.createDefault()) {


            // Login and obtain a session token
            LoginResponse loginResponse = betfairClient.login(username, password, appKey);
            String sessionToken = loginResponse.getToken();


            // Refresh the Session Token
            betfairClient.keepAliveSession(appKey, sessionToken);


            // List all the events of the event type id '1' happening in the next 24 hours
            MarketFilter marketFilter = new MarketFilter.Builder()
                                      .withEventTypeIds(new int[]{1})
                                      .withMarketStartTime(new MarketStartTime(now, tomorrow))
                                      .build();

            EventResult[] events = betfairClient.listEvents(appKey, sessionToken, marketFilter);

            // List all the event types with name 'Soccer'
            marketFilter = new MarketFilter.Builder()
                               .withTextQuery("Soccer")
                               .build();

            EventTypeResult[] eventTypes = betfairClient.listEventTypes(appKey, sessionToken, marketFilter);

            MarketType[] marketTypes = betfairClient.listMarketTypes(appKey, sessionToken, marketFilter);

            // List Market Catalogue for the market 'OVER_UNDER_25'
            marketFilter = new MarketFilter.Builder()
                    .withMarketTypeCodes(new String[]{"OVER_UNDER_25"})
                    .build();

            MarketCatalogue[] marketCatalogues = betfairClient.listMarketCatalogue(appKey, sessionToken, marketFilter, 1);


            // Logout to invalidate the session token
            betfairClient.logout(appKey, sessionToken);
        }
    }
}
