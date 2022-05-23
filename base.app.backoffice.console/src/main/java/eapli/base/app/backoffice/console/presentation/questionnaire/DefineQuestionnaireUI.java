package eapli.base.app.backoffice.console.presentation.questionnaire;/*
 * Copyright (c) 2013-2019 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import eapli.base.productCategory.domain.AlphaNumericCode;
import eapli.base.questionnaire.domain.*;
import eapli.base.usermanagement.application.AddCustomerController;
import eapli.framework.general.domain.model.Description;
import eapli.framework.io.util.Console;
import eapli.framework.presentation.console.AbstractUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * UI for adding a questionnaire to the application.
 * <p>
 * Created by nuno on 22/03/16.
 */
public class DefineQuestionnaireUI extends AbstractUI {

    private final AddCustomerController theController = new AddCustomerController();
    private String alphanumericCodeString;
    private String descriptionString;
    private String period;
    private String obligatorinessString;
    private String questionTypeString;
    private Obligatoriness obligatoriness;
    private QuestionType questionType;
    private String repeatability;
    private String extraInfo;
    private String questionId;
    private String questionMessage;
    private String sectionTitle;
    private String sectionId;
    private String finalMessage;
    private String welcomeMessage;
    private String questionnaireTitle;
    private String questionnaireId;
    private String response = "";
    private List<Section> sectionList = new ArrayList<>();
    private List<Question> questionList = new ArrayList<>();
    private Questionnaire questionnaire;
    private String instruction;

    @Override
    protected boolean doShow() {
        String option;
        System.out.println("Select how you want to define the Questionnaire:");
        System.out.println("1. Input the data");
        System.out.println("2. Import text file");
        do {
            option = Console.readLine("");
        } while (!(option.equals("1") || option.equals("2")));

        if (option.equals("1")) {
            questionnaire = inputData();
            Survey survey = new Survey(AlphaNumericCode.valueOf(alphanumericCodeString), Description.valueOf(descriptionString), Period.valueOf(period), questionnaire);

            System.out.println(survey);
        } else {
            importTextFile();
        }
        return false;
    }

    private Questionnaire inputData() {
        insertSurveyData();
        insertQuestionnaireData();
        while (!(response.equalsIgnoreCase("NO") || response.equalsIgnoreCase("N"))) {
            insertSectionsData();
            while (!(response.equalsIgnoreCase("NO") || response.equalsIgnoreCase("N"))) {
                insertQuestionsData();
                response = Console.readLine("Do you want to define another question? (Y/N)");
            }
            response = Console.readLine("Do you want to define another section? (Y/N)");

            sectionList.add(new Section(sectionId
                    , sectionTitle
                    , Description.valueOf(descriptionString)
                    , obligatoriness
                    , repeatability
                    , questionList));
            questionList = new ArrayList<>();

        }
        return new Questionnaire(questionnaireId, questionnaireTitle, welcomeMessage, sectionList, finalMessage);
    }

    private void importTextFile() {
        insertSurveyData();
        String questionnaire = "";
        try {
            questionnaire = Files.readString(Path.of("questionnaire/Question.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Survey survey = new Survey(AlphaNumericCode.valueOf(alphanumericCodeString),
                Description.valueOf(descriptionString),
                Period.valueOf(period), new Content(questionnaire));
        System.out.println(survey);
    }

    private void insertSurveyData() {
        System.out.println("SURVEY");
        alphanumericCodeString = Console.readLine("Alphanumeric code");
        descriptionString = Console.readLine("Description");
        period = Console.readLine("Period");
    }

    private void insertQuestionnaireData() {
        System.out.println("QUESTIONNAIRE");
        questionnaireId = Console.readLine("Questionnaire id:");
        questionnaireTitle = Console.readLine("Questionnaire title");
        welcomeMessage = Console.readLine("Welcome Message");
        finalMessage = Console.readLine("Final Message");
        period = Console.readLine("Period");
    }


    private void insertSectionsData() {
        System.out.println("SECTION");
        sectionId = Console.readLine("Section id:");
        sectionTitle = Console.readLine("Section title");
        descriptionString = Console.readLine("Description");
        selectObligatoriness();
    }


    private void insertQuestionsData() {
        System.out.println("QUESTION");
        questionId = Console.readLine("Question ID");
        questionMessage = Console.readLine("Question message");
        instruction = Console.readLine("Intruction");
        selectQuestionType();
        selectObligatoriness();
        extraInfo = Console.readLine("Extra info");
        questionList.add(new Question(questionId, questionMessage, instruction, questionType, obligatoriness, extraInfo));
    }


    private void selectObligatoriness() {
        int i = 0;
        System.out.println("Select the obligatoriness");
        for (Obligatoriness obligatorinessValues : Obligatoriness.values()) {
            System.out.println(i + ". " + obligatorinessValues);
            i++;
        }
        do {
            obligatorinessString = Console.readLine("");
            if (obligatorinessString.equals("0")) {
                obligatoriness = Obligatoriness.MANDATORY;
            } else if (obligatorinessString.equals("1")) {
                obligatoriness = Obligatoriness.OPTIONAL;
            } else if (obligatorinessString.equals("2")) {
                obligatoriness = Obligatoriness.CONDITION_DEPENDANT;
            }
        } while (!(obligatorinessString.equals("0") || obligatorinessString.equals("1") || obligatorinessString.equals("2")));
    }


    private void selectQuestionType() {
        int i = 0;
        System.out.println("Select the Question Type");
        for (QuestionType questionTypeValues : QuestionType.values()) {
            System.out.println(i + ". " + questionTypeValues);
            i++;
        }
        do {
            questionTypeString = Console.readLine("");
            if (questionTypeString.equals("0")) {
                questionType = QuestionType.FREE_TEXT;
            } else if (obligatorinessString.equals("1")) {
                questionType = QuestionType.NUMERIC;
            } else if (obligatorinessString.equals("2")) {
                questionType = QuestionType.SINGLE_CHOICE;
            } else if (obligatorinessString.equals("3")) {
                questionType = QuestionType.SINGLE_CHOICE_INPUT_VALUE;
            } else if (obligatorinessString.equals("4")) {
                questionType = QuestionType.MULTIPLE_CHOICE;
            } else if (obligatorinessString.equals("5")) {
                questionType = QuestionType.MULTIPLE_CHOICE_INPUT_VALUE;
            } else if (obligatorinessString.equals("6")) {
                questionType = QuestionType.SORTING_OPTIONS;
            } else if (obligatorinessString.equals("7")) {
                questionType = QuestionType.SCALING_OPTIONS;
            }
        } while (!(obligatorinessString.equals("0") || obligatorinessString.equals("1") || obligatorinessString.equals("2")
                || obligatorinessString.equals("3") || obligatorinessString.equals("4") || obligatorinessString.equals("5")
                || obligatorinessString.equals("6") || obligatorinessString.equals("7")));
    }


    @Override
    public String headline() {
        return "Define new Questionnaire";
    }
}