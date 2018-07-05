package it.nicolagiacchetta.betfair.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class Filter {

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String textQuery;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int[] eventTypeIds;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int[] competitionIds;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MarketStartTime marketStartTime;

    private Filter(Builder builder) {
        this.textQuery = builder.textQuery;
        this.eventTypeIds = builder.eventTypeIds;
        this.marketStartTime = builder.marketStartTime;
        this.competitionIds = builder.competitionIds;
    }

    public String getTextQuery() {
        return textQuery;
    }

    public int[] getEventTypeIds() {
        return eventTypeIds;
    }

    public int[] getCompetitionIds() {
        return competitionIds;
    }

    public MarketStartTime getMarketStartTime() {
        return marketStartTime;
    }

    public static class Builder {

        private String textQuery;

        private int[] eventTypeIds;

        private int[] competitionIds;

        private MarketStartTime marketStartTime;

        public Builder() {}

        public Builder withTextQuery(String textQuery) {
            this.textQuery = textQuery;
            return this;
        }

        public Builder withEventTypeIds(int[] eventTypeIds) {
            this.eventTypeIds = eventTypeIds;
            return this;
        }

        public Builder withCompetitionIds(int[] competitionIds) {
            this.competitionIds = competitionIds;
            return this;
        }

        public Builder withMarketStartTime(MarketStartTime marketStartTime) {
            this.marketStartTime = marketStartTime;
            return this;
        }

        public Filter build() {
            return new Filter(this);
        }
    }
}