package com.askattest.interview.repository;

import com.askattest.interview.models.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ResponseRepo {
    public List<Response> responses;

    public ResponseRepo() throws IOException {
        this.responses = ResponseRepo.loadResponses("responses.json");
    }

    public List<Integer> respondents() {
        return this.responses.stream().map(response -> response.respondent).distinct().toList();
    }

    public List<Response> responsesByRespondent(int respondentId) {
        return this.responses.stream().filter(response -> response.respondent == respondentId).toList();
    }

    public List<Response> responsesByRespondentAndQuestion(int respondentId, int questionId) {
        List<Response> responses= this.responses.stream().filter(response -> response.respondent == respondentId && response.question == questionId).toList();
        return responses;
    }

    public List<Response> listResponses() {
        return this.responses;
    }

    public static List<Response> loadResponses(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            URL filenameUrl = ResponseRepo.class.getResource("/survey-data/" + filename);
            return objectMapper.readValue(filenameUrl, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IOException("Could not load survey from file: " + filename, e);
        }
    }
}
