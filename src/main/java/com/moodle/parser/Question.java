package com.moodle.parser;

import java.util.List;
import java.util.Objects;

/**
 * Класс для вопросов. Содержит текст вопроса, его тип и ответы.
 */
public class Question {
    private String text = "";
    private String type = "";
    private List<String> answers = null;
    private String parsedAnswer = "";

    public Question() {
    }

    public Question(String text, String type, List<String> answers) {
        this.text = text;
        this.answers = answers;
        if(Objects.equals(type, "ddwtos")) {
            this.type = "Перетаскивание в текст";
            parsedAnswer = "<p>Варианты ответов:</p><p>"+answers.toString()+"</p>";
        } else if(Objects.equals(type, "essay")) {
            this.type = "Эссе";
        } else if(Objects.equals(type, "gapselect")) {
            this.type = "Выбор пропущенных слов";
            chooseAnswers(answers, true);
        } else if(Objects.equals(type, "multichoice")) {
            this.type = "Множественный выбор и выбор пропущенных слов";
            chooseAnswers(answers, true);
        }
        else if(Objects.equals(type, "multichoiceset")) {
            this.type = "Все или ничего";
            chooseAnswers(answers, true);
        }
        else if(Objects.equals(type, "numerical")) {
            this.type = "Числовой ответ";
            parsedAnswer += "<p>Правильные варианты ответов:</p>";
            chooseAnswers(answers, false);
        }
        else if(Objects.equals(type, "shortanswer")) {
            this.type = "Короткий ответ";
            parsedAnswer += "<p>Правильные варианты ответов:</p>";
            chooseAnswers(answers, false);
        }
        else this.type="";
    }

    /**
     * Для вопросов с выбором
     * @param answers
     * @param isYellow
     */
    private void chooseAnswers(List<String> answers, boolean isYellow) {
        for (int i = 1; i <= answers.size(); i++){
            if(answers.get(i - 1).contains(" - Правильный ответ") && isYellow){
                parsedAnswer += "<p><span style=\"background:#FFFF00\">" + i + ". " + answers.get(i - 1).replaceAll(" - Правильный ответ","") +"</span></p>";
            } else {
                parsedAnswer += "<p>" + i + ". " + answers.get(i - 1).replaceAll(" - Правильный ответ","") + "</p>";
            }
        }
    }

    @Override
    public String toString() {
        return "Question{" +
                "Вопрос='" + text + '\'' +
                ", Тип вопроса ='" + type + '\'' +
                ", ответы=" + answers +
                '}';
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getParsedAnswer() {
        return parsedAnswer;
    }
}
