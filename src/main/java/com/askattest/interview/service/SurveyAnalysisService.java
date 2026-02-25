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
            return currentQuestion;
        }

        if (currentQuestion.options.stream().allMatch(o -> o.route == -1)) {
            Question endOfSurveyQuestion = new Question();
            endOfSurveyQuestion.text = "End_Survey";
            endOfSurveyQuestion.id = -1;
            return endOfSurveyQuestion;
        }


        Optional<Question> next = surveyRepository.surveyById(200).questions.stream()
                .filter(q -> q.id == currentQuestion.options.get(userResponse.choice).route).findFirst();
        return nextQuestion(next.orElseThrow(), respondentId);

    }

    public Integer getMaxRemainingQuestions(int respondentId, int surveyId) {

        Question firstQuestion = this.startQuestion(surveyId);
        Question nextQuestion = nextQuestion(firstQuestion, respondentId);

        if (nextQuestion.id == -1) {
            return 0;
        }

        return maxRemainingQuestions(nextQuestion, 1);
    }

    private Integer maxRemainingQuestions(Question currentQuestion, int questionCount) {
        if (currentQuestion.options.stream().allMatch(o -> o.route == -1)) {
            return questionCount;
        }
        var nextQuestions = currentQuestion.options.stream()
                .map(option -> option.route)
                .map(questionId -> surveyRepository.surveyById(200)
                .questions.stream().filter(question -> question.id == questionId).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get);

        return nextQuestions.map(nq -> maxRemainingQuestions(nq, questionCount + 1))
                .max(Integer::compareTo)
                .orElse(questionCount);
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
