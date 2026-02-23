package com.askattest.interview.service;
import com.askattest.interview.models.PayoutByQuestion;
import com.askattest.interview.repository.ResponseRepo;
import com.askattest.interview.repository.SurveyRepo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurveyAnalysisServiceTest {


    @Test
    void getQuestionCountByRespondent() throws IOException {
        // Given
        SurveyRepo surveyRepo = new SurveyRepo();
        ResponseRepo responseRepo = new ResponseRepo();
        SurveyAnalysisService service = new SurveyAnalysisService(responseRepo, surveyRepo);

        // When
        var questionCountByRespondent = service.getQuestionCountByRespondent(200);

        System.out.println(questionCountByRespondent);

        // Then
        assertEquals(200, questionCountByRespondent.getSurveyId());

        assertEquals(5, questionCountByRespondent.getQuestionCountByRespondent().get(300));
        assertEquals(5, questionCountByRespondent.getQuestionCountByRespondent().get(301));
        assertEquals(4, questionCountByRespondent.getQuestionCountByRespondent().get(302));
        assertEquals(2, questionCountByRespondent.getQuestionCountByRespondent().get(303));
    }

    @Test
    void getMoneyEarnedByRespondent() throws IOException {
        // Given
        SurveyRepo surveyRepo = new SurveyRepo();
        ResponseRepo responseRepo = new ResponseRepo();
        SurveyAnalysisService service = new SurveyAnalysisService(responseRepo, surveyRepo);

        // When
        var moneyEarnedByRespondent = service.getMoneyEarnedByRespondent(200);

        System.out.println(moneyEarnedByRespondent);

        // Then
        assertEquals(200,moneyEarnedByRespondent.getSurveyId());

        assertEquals(29, moneyEarnedByRespondent.getPayoutByRespondent().get(300));
        assertEquals(24, moneyEarnedByRespondent.getPayoutByRespondent().get(301));
        assertEquals(19, moneyEarnedByRespondent.getPayoutByRespondent().get(302));
        assertEquals(3, moneyEarnedByRespondent.getPayoutByRespondent().get(303));

    }
}
