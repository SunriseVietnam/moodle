package com.moodle.parser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.*;

import java.util.*;

public class XMLConvertor {

    /**
     * Собирает в questionsInfo инфу про вопрос в формате List<Question>
     *
     * @param inputXMLFile - входной файл moodleXML
     * @return
     */
    public static List<Question> collectXMLData(String inputXMLFile) {

        List<Question> questionsInfo = new ArrayList<>();

        try {
            Document document = XMLParser.parse(inputXMLFile);

            document.getDocumentElement().normalize();

            NodeList questionList = document.getElementsByTagName("question");
            for (int i = 0; i < questionList.getLength(); i++) {

                Node question = questionList.item(i);

                if (question.getNodeType() == Node.ELEMENT_NODE) {

                    Element questionElement = (Element) question;
                    String questionType = questionElement.getAttribute("type");
                    GWT.log(questionType);
                    String questionText = "";
                    ArrayList<String> answers = new ArrayList<>();

                    NodeList questionDetails = question.getChildNodes();
                    for (int j = 0; j < questionDetails.getLength(); j++) {

                        Node detail = questionDetails.item(j);
                        if (detail.getNodeType() == Node.ELEMENT_NODE) {
                            Element detailElement = (Element) detail;

                            if (detailElement.getTagName().equals("questiontext")) {
                                questionText = detailElement.getChildNodes().item(1).toString().replaceAll("<[^>]*>", "").trim();
                            }

                            /* Правильные ответы выделяю для мультивыбора и вставить пропущенное слово, остальное
                            пока не пониманию точно, как должно выглядеть в DOCX и как выделить их тоже ососбо пока не разобрался,
                             особенно с перетаскиванием это КРИНЖ.
                            */
                            if (detailElement.getTagName().equals("answer") || detailElement.getTagName().equals("dragbox") ||
                                    detailElement.getTagName().equals("selectoption")) {
                                for (int k = 0; k <= detailElement.getChildNodes().getLength(); k++) {
                                    if (detailElement.getChildNodes().item(k) != null) {
                                        String currentText = detailElement.getChildNodes().item(k).toString().replaceAll("<[^>]*>", "").trim();
                                        if (!currentText.equals("")) {
                                            if (k == 1) {
                                                if (currentText.endsWith("]]>")) {
                                                    currentText = currentText.substring(0, currentText.length() - 3).trim();
                                                }
                                                if (detailElement.getTagName().equals("answer")) {
                                                    boolean isCorrect = !detailElement.getAttribute("fraction").equals("0");
                                                    if (isCorrect) {
                                                        answers.add(currentText + " - Правильный ответ");
                                                    } else {
                                                        answers.add(currentText);
                                                    }
                                                } else {
                                                    answers.add(currentText);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Question qstn = normalizeXMLData(questionType, answers, questionText);
                    boolean isDuplicate = false;
                    for(Question q: questionsInfo){
                        if(q.getText().equals(qstn.getText())){
                            isDuplicate = true;
                            break;
                        }
                    }
                    if(!isDuplicate &&
                            !qstn.getText().equals("") &&
                            !qstn.getType().equals("") &&
                            qstn.getAnswers()!=null) {
                        questionsInfo.add(qstn);
                    }
                }
            }
        } catch (DOMException e) {
            Window.alert("Could not parse XML document.");
            throw new RuntimeException(e);
        }
        return questionsInfo;
    }

    /**
     * Нормализует ответы
     * @param questionType - тип вопроса
     * @param answers - ответы
     * @param questionText - формулировка
     * @return Question
     */
    public static Question normalizeXMLData(String questionType, ArrayList<String> answers, String questionText) {
        Question normalizedAnswers = new Question();
        ArrayList<String> newAnswers = new ArrayList<>();
        StringBuilder answer = new StringBuilder();
        String newQuestionText = questionText;
        if (newQuestionText.endsWith("]]>")) {
            newQuestionText = newQuestionText.substring(0, newQuestionText.length() - 3);
        }
        int count = 0;

        if (!questionType.equals("essay")) {
            for (int i = 0; i < answers.toArray().length; i++) {

                String currentAnswer = answers.toArray()[i].toString().replaceAll("<[^>]*>", "").trim();
                String[] splitAnswer = (currentAnswer.split("\n"));
                for (String s : splitAnswer) {
                    count ++;
                    currentAnswer = s.trim();
                    if (!currentAnswer.equals("")) {
                        answer = new StringBuilder(currentAnswer);
                    }
                }
                if (questionType.equals("gapselect")) {
                    String[] string =(newQuestionText.split(""));
                    for (int j = 0; j <= string.length; j ++) {
                        int index = -1;
                        if (string[j].equals("[") && string[j + 1] != null && string[j + 1].equals("[") && string[j + 3] != null && string[j + 3].equals("]")
                        && string[j + 4] != null && string[j + 4].equals("]")) {
                            try {
                                index = Integer.parseInt(string[j + 2]);
                            }
                            catch (NumberFormatException e) {
                            }
                            if (index > -1 && count == index) {
                                answer = new StringBuilder(currentAnswer + " - Правильный ответ");
                            }
                            break;
                        }
                    }
                }
                newAnswers.add(answer.toString());
                normalizedAnswers = new Question(newQuestionText,questionType, newAnswers);
            }
        } else {
            normalizedAnswers = new Question(newQuestionText, questionType, answers);
        }
        return normalizedAnswers;
    }
}
