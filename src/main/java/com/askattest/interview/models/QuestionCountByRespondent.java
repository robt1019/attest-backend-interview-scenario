package com.askattest.interview.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class QuestionCountByRespondent {

    private final Integer surveyId;
    private final Map<Integer, Integer> questionCountByRespondent;

    public QuestionCountByRespondent(Integer surveyId) {
        this.surveyId = surveyId;
        this.questionCountByRespondent = new HashMap<>();
    }

    public void addRespondentQuestionCount(Integer respondentId, Integer questionCount) {
        this.questionCountByRespondent.put(respondentId, questionCount);
    }

    public Map<Integer, Integer> getQuestionCountByRespondent() {
        return this.questionCountByRespondent;
    }

    public Integer getSurveyId() {
        return this.surveyId;
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
