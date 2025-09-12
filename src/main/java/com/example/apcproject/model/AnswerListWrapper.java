package com.example.apcproject.model;

import java.util.ArrayList;
import java.util.List;

public class AnswerListWrapper {
    private List<Answer> answers = new ArrayList<>();

    public AnswerListWrapper() {}

    public AnswerListWrapper(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}