package com.trustbank.Model;

import java.io.Serializable;

public class FAQModel implements Serializable {

    private String questions;
    private String answers;

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }
}
