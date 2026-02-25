package com.askattest.interview.service;

import com.askattest.interview.models.*;
import com.askattest.interview.repository.ResponseRepo;
import com.askattest.interview.repository.SurveyRepo;

import java.util.*;
import java.util.stream.Collectors;

public class SurveyAnalysisService {

    private final ResponseRepo responseRepository;
    private final SurveyRepo surveyRepository;

    public Question startQuestion(int surveyId) {
        return surveyRepository.surveyById(surveyId).questions.getFirst();
    }


    public Question nextQuestion(Question currentQuestion, int respondentId) {

        Response userResponse;

        try {
            userResponse = this.responseRepository.responsesByRespondentAndQuestion(respondentId, currentQuestion.id).getFirst();
        } catch (NoSuchElementException e) {
//        Base case - respondent has not answered question
//            Return question
            return currentQuestion;
        }

//        Base case - answered option.route is null
//            Return end of survey
        if (currentQuestion.options.stream().allMatch(o -> o.route == -1)) {
            Question endOfSurveyQuestion = new Question();
            endOfSurveyQuestion.text = "End_Survey";
            endOfSurveyQuestion.id = -1;
            return endOfSurveyQuestion;
        }


//        Find the next question, call nextQuestion with it
        Optional<Question> next = surveyRepository.surveyById(200).questions.stream().filter(q -> {
            return q.id == currentQuestion.options.get(userResponse.choice).route;
        }).findFirst();
//        return nextQuestion(respondent answer to current question)
        return nextQuestion(next.orElseThrow(), respondentId);

    }

    public SurveyAnalysisService(ResponseRepo responseRepository, SurveyRepo surveyRepository) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
    }

    public QuestionCountByRespondent getQuestionCountByRespondent(Integer surveyId) {

        QuestionCountByRespondent questionCountByRespondent = new QuestionCountByRespondent(surveyId);

        List<Integer> respondentIds = this.responseRepository.respondents();

        for (Integer respondentId : respondentIds) {
            Integer questionCount = this.questionsAnsweredCountBySurveyAndRespondent(surveyId, respondentId);
            questionCountByRespondent.addRespondentQuestionCount(respondentId, questionCount);
        }

        return questionCountByRespondent;
    }

    public PayoutByRespondent getMoneyEarnedByRespondent(Integer surveyId){
        PayoutByRespondent payoutByRespondent = new PayoutByRespondent(surveyId);
        PayoutByQuestion payoutByQuestion = this.getPayoutByQuestion(surveyId);

        List<Integer> respondentIds = this.responseRepository.respondents();
        for (Integer respondentId : respondentIds) {
            Integer moneyEarned = this.moneyEarnedBySurveyAndRespondent(surveyId, respondentId, payoutByQuestion);
            payoutByRespondent.addPayoutByRespondent(respondentId, moneyEarned);
        }
        return payoutByRespondent;
    }

    private PayoutByQuestion getPayoutByQuestion(Integer surveyId) {
        PayoutByQuestion payoutByQuestion = new PayoutByQuestion(surveyId);

        this.surveyRepository.surveyById(surveyId).questions.forEach(question -> {
            payoutByQuestion.addQuestionPayout(question.id, question.payout);
        });

        return payoutByQuestion;
    }

    private Integer questionsAnsweredCountBySurveyAndRespondent(Integer surveyId, Integer respondentId) {

        Set<Integer> questionIds = this.surveyRepository.surveyById(surveyId).questions.stream()
                .map(question -> question.id).collect(Collectors.toSet());

        return this.responseRepository.responsesByRespondent(respondentId).stream().filter(
                response -> questionIds.contains(response.question)).toList().size();
    }

    private Integer moneyEarnedBySurveyAndRespondent(Integer surveyId, Integer respondentId, PayoutByQuestion payoutByQuestion) {

        Set<Integer> questionIds = this.surveyRepository.surveyById(surveyId).questions.stream()
                .map(question -> question.id).collect(Collectors.toSet());

        return this.responseRepository.responsesByRespondent(respondentId).stream()
                .filter(response -> questionIds.contains(response.question))
                .mapToInt(response -> payoutByQuestion.getPayoutByQuestion().get(response.question))
                .sum();

    }

}
