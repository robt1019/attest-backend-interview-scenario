package com.askattest.interview;

import com.askattest.interview.repository.ResponseRepo;
import com.askattest.interview.repository.SurveyRepo;
import com.askattest.interview.service.SurveyAnalysisService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        SurveyRepo surveys;
        ResponseRepo responses;
        SurveyAnalysisService surveyAnalysisService;
        try {
            surveys = new SurveyRepo();
            responses = new ResponseRepo();
            surveyAnalysisService = new SurveyAnalysisService(responses, surveys);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Logger.getGlobal().log(Level.INFO, surveys.surveyById(200).toString());
        Logger.getGlobal().log(Level.INFO, responses.responsesByRespondent(300).toString());
        Logger.getGlobal().log(Level.INFO, surveyAnalysisService.getQuestionCountByRespondent(200).toString());
        Logger.getGlobal().log(Level.INFO, surveyAnalysisService.getMoneyEarnedByRespondent(200).toString());
    }
}