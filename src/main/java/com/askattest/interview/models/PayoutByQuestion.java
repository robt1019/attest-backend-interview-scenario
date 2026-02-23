package com.askattest.interview.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class PayoutByQuestion {

    private final Integer surveyId;
    private final Map<Integer, Integer> payoutByQuestion;

    public PayoutByQuestion(Integer surveyId) {
        this.surveyId = surveyId;
        this.payoutByQuestion = new HashMap<>();
    }

    public Integer getSurveyId() {
        return this.surveyId;
    }

    public void addQuestionPayout(Integer questionId, Integer payout) {
        this.payoutByQuestion.put(questionId, payout);
    }

    public Map<Integer, Integer> getPayoutByQuestion() {
        return this.payoutByQuestion;
    }

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
