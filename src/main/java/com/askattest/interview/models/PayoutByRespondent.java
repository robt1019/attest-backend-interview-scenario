package com.askattest.interview.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class PayoutByRespondent {
    private final Integer surveyId;
    private final Map<Integer, Integer> payoutByRespondent;

    public PayoutByRespondent(Integer surveyId) {
        this.surveyId = surveyId;
        this.payoutByRespondent = new HashMap<>();
    }

    public void addPayoutByRespondent(Integer respondentId, Integer payout) {
        this.payoutByRespondent.put(respondentId, payout);
    }

    public Integer getSurveyId() {
        return this.surveyId;
    }

    public Map<Integer, Integer> getPayoutByRespondent() {
        return this.payoutByRespondent;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
