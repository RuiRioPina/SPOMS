package eapli.base.grammar;

import eapli.base.clientusermanagement.domain.Customer;
import eapli.base.questionnaire.application.SurveyController;
import eapli.base.questionnaire.domain.Answer;
import eapli.base.questionnaire.domain.Obligatoriness;
import eapli.base.questionnaire.domain.QuestionType;
import eapli.base.questionnaire.domain.Survey;
import eapli.base.usermanagement.application.AddCustomerController;
import eapli.framework.io.util.Console;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class EvalVisitor extends LabeledExprBaseVisitor<String> {
    //private final SurveyController theController = new SurveyController();

    //TODO falta guardar as respostas e fazer o condition dependant com essas respostas
    private List<Answer> answers = new ArrayList<>();
    private Map<String, Boolean> mapObligatoriness = new HashMap<>();
    private String section;

    private String questionId;

    private Customer customer1;

    private Survey survey;

    AddCustomerController controller = new AddCustomerController();

    SurveyController surveyController = new SurveyController();


    @Override
    public String visitSection(LabeledExprParser.SectionContext ctx) {
        //theController.surveyToBeAnswered();
        System.out.println("---------------------------------------------------------------------");
        System.out.println(ctx.SECTION_ID());
        System.out.println(ctx.SECTION_TITLE());
        if (ctx.SECTION_DESCRIPTION() != null) {
            System.out.println(ctx.SECTION_DESCRIPTION());
        }
        System.out.println(ctx.OBLIGATORINESS());

        System.out.println("---------------------------------------------------------------------");
        System.out.println();

        section = ctx.SECTION_ID().toString();
        if (!treatObligatoriness(ctx.SECTION_ID(), ctx.OBLIGATORINESS()).equalsIgnoreCase("N")) {
            for (LabeledExprParser.QuestionContext context : ctx.question()) {
                visitQuestion(context);
            }
        }
        return ctx.getText();
    }

    @Override
    public String visitQuestion(LabeledExprParser.QuestionContext ctx) {
        Optional<Customer> customer = controller.getCustomer(11L);
        customer1 = customer.get();

        survey = surveyController.surveyToBeAnswered().get();
        System.out.println(ctx.QUESTION_ID());
        System.out.println(ctx.Q());
        if (!ctx.INSTRUCTION().isEmpty()) {
            System.out.println(ctx.INSTRUCTION());
        }
        System.out.println(ctx.OBLIGATORINESS());
        questionId = ctx.QUESTION_ID().toString();

        String questionType = ctx.questionType().getText();
        questionType = questionType.split(" ")[1].replace("EXTRA", "");
        System.out.println(questionType);
        String yau = "";
        if (!treatObligatoriness(ctx.QUESTION_ID(), ctx.OBLIGATORINESS()).equalsIgnoreCase("N")) {
            yau = treatQuestionType(questionType, ctx);
        }
        if (yau != null) {
            String response = "QUESTION " + ctx.QUESTION_ID().toString().replace("QUESTION ID: ", "");
            mapObligatoriness.put(response, true);
        }

        assert yau != null;
        if (!yau.equalsIgnoreCase("n")) {
            answers.add(new Answer(yau, section, ctx.QUESTION_ID().toString(), customer1, survey));
        }
        System.out.println();
        return ctx.getText();
    }

    @Override
    public String visitFree_text(LabeledExprParser.Free_textContext ctx) {
        if (ctx.EXTRA_INFO() != null && !ctx.EXTRA_INFO().toString().isBlank()) {
            System.out.println(ctx.EXTRA_INFO() + " " + ctx.SENTENCE());
        }
        return Console.readLine("Answer:");
    }

    @Override
    public String visitNumeric(LabeledExprParser.NumericContext ctx) {
        if (ctx.EXTRA_INFO() != null && !ctx.EXTRA_INFO().toString().isBlank()) {
            System.out.println(ctx.EXTRA_INFO() + " " + ctx.NUMBER());
        }

        return new Scanner(System.in).nextLine();
    }

    @Override
    public String visitSingleChoice(LabeledExprParser.SingleChoiceContext ctx) {
        int i = 0;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();
        do {
            while (i < numberOfOptions) {
                optionToBeShown = ctx.CHOOSE(i).toString().contains("|") ? ctx.CHOOSE(i).toString().replace("|", "") : ctx.CHOOSE(i).toString();
                System.out.println(optionToBeShown);
                i++;
            }
            try {
                response = Console.readLine("Answer:");
                if (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0)
                    System.out.println("Option not available");
            } catch (NumberFormatException e) {
                response = String.valueOf(Integer.MAX_VALUE);
                System.out.println("Option not available");
            }
        } while (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0);
        return response;
    }


    @Override
    public String visitSingleChoiceWithInput(LabeledExprParser.SingleChoiceWithInputContext ctx) {
        int i = 0;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();
        do {
            while (i < numberOfOptions) {
                optionToBeShown = ctx.CHOOSE(i).toString().contains("|") ? ctx.CHOOSE(i).toString().replace("|", "") : ctx.CHOOSE(i).toString();
                System.out.println(optionToBeShown);
                i++;
            }
            try {
                response = Console.readLine("Answer:");
                if (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0)
                    System.out.println("Option not available");
            } catch (NumberFormatException e) {
                response = String.valueOf(Integer.MAX_VALUE);
                System.out.println("Option not available");
            }
        } while (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0);

        String input;
        if (Integer.parseInt(response) == ctx.CHOOSE().size()) {
            input = Console.readLine("Input");
        }
        return response;
    }

    public String visitMultipleChoice(LabeledExprParser.MultipleChoiceContext ctx) {
        int i = 0;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();
        do {
            while (i < numberOfOptions) {
                optionToBeShown = ctx.CHOOSE(i).toString().contains("|") ? ctx.CHOOSE(i).toString().replace("|", "") : ctx.CHOOSE(i).toString();
                System.out.println(optionToBeShown);
                i++;
            }
            response = Console.readLine("Answer (Input \"N\" to stop):");
            try {
                if ((!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("NO")) && (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0))
                    System.out.println("Option not available");
            } catch (NumberFormatException e) {
                response = String.valueOf(Integer.MAX_VALUE);
                System.out.println("Option not available");
            }
            if (!response.equalsIgnoreCase("n")) {
                answers.add(new Answer(response, section, questionId, customer1, survey));
            }
        } while ((answers.size() != numberOfOptions) && (!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("NO")));
        return response;
    }

    public String visitMultipleChoiceWithInput(LabeledExprParser.MultipleChoiceWithInputContext ctx) {
        int i = 0;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();
        do {
            while (i < numberOfOptions) {
                optionToBeShown = ctx.CHOOSE(i).toString().contains("|") ? ctx.CHOOSE(i).toString().replace("|", "") : ctx.CHOOSE(i).toString();
                System.out.println(optionToBeShown);
                i++;
            }
            response = Console.readLine("Answer (Input \"N\" to stop):");
            try {
                if ((!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("NO")) && (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0))
                    System.out.println("Option not available");
            } catch (NumberFormatException e) {
                response = String.valueOf(Integer.MAX_VALUE);
                System.out.println("Option not available");
            }
            if (!response.equalsIgnoreCase("n")) {
                answers.add(new Answer(response, section, questionId, customer1, survey));
            }
        } while ((answers.size() != numberOfOptions) && (!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("NO")));

        String input = "";
        if (Integer.parseInt(response) == (numberOfOptions)) {
            input = Console.readLine("Input");
        }

        return response + " " + input;
    }

    public String visitSortingOptions(LabeledExprParser.SortingOptionsContext ctx) {
        int i;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();

        List<TerminalNode> list = ctx.CHOOSE();
        List<String> nodesInStringFormat = new ArrayList<>();
        for (TerminalNode node : list) {
            nodesInStringFormat.add(node.toString());
        }

        do {
            i = 0;
            while (i < numberOfOptions) {
                optionToBeShown = nodesInStringFormat.get(i).contains("|") ? nodesInStringFormat.get(i).replace("|", "") : nodesInStringFormat.get(i);
                System.out.println(optionToBeShown);
                i++;
            }
            response = Console.readLine("Select the option you want to be first (INPUT \"N\" to exit)");
            try {
                String replaced = nodesInStringFormat.get(Integer.parseInt(response) - 1).replace(response, "1");
                nodesInStringFormat.set(Integer.parseInt(response) - 1, replaced);
                String replacedTheFirstElement = nodesInStringFormat.get(0).replace("1", response);

                nodesInStringFormat.set(0, replacedTheFirstElement);


                Collections.swap(nodesInStringFormat, 0, Integer.parseInt(response) - 1);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {

            }


        } while (!response.equalsIgnoreCase("N") && !response.equalsIgnoreCase("NO"));

        return response;
    }


    public String visitScalingOptions(LabeledExprParser.ScalingOptionsContext ctx) {
        int i;
        String response;
        String optionToBeShown;
        int numberOfOptions = ctx.CHOOSE().size();
        do {
            i = 0;
            while (i < numberOfOptions) {
                optionToBeShown = ctx.CHOOSE(i).toString().contains("|") ? ctx.CHOOSE(i).toString().replace("|", "") : ctx.CHOOSE(i).toString();
                System.out.println(optionToBeShown);
                i++;
            }
            try {
                response = Console.readLine("Answer:");
                if (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0)
                    System.out.println("Option not available");
            } catch (NumberFormatException e) {
                response = String.valueOf(Integer.MAX_VALUE);
                System.out.println("Option not available");
            }
        } while (Integer.parseInt(response) > numberOfOptions || Integer.parseInt(response) <= 0);
        return response;
    }


    private String treatQuestionType(String questionType, LabeledExprParser.QuestionContext ctx) {
        switch (QuestionType.valueOf(questionType)) {
            case FREE_TEXT:
                return visitFree_text((ctx.questionType().free_text()));
            case NUMERIC:
                return visitNumeric(ctx.questionType().numeric());
            case SINGLE_CHOICE:
                return visitSingleChoice(ctx.questionType().singleChoice());
            case SINGLE_CHOICE_INPUT_VALUE:
                return visitSingleChoiceWithInput(ctx.questionType().singleChoiceWithInput());
            case MULTIPLE_CHOICE:
                return visitMultipleChoice(ctx.questionType().multipleChoice());
            case MULTIPLE_CHOICE_INPUT_VALUE:
                return visitMultipleChoiceWithInput(ctx.questionType().multipleChoiceWithInput());
            case SCALING_OPTIONS:
                return visitScalingOptions(ctx.questionType().scalingOptions());
            case SORTING_OPTIONS:
                return visitSortingOptions(ctx.questionType().sortingOptions());
            default:
                return null;
        }
    }


    private String treatObligatoriness(TerminalNode questionOrSection, TerminalNode obligatoriness) {
        String treat = obligatoriness.toString().replace("OBLIGATORINESS: ", "").trim();
        if (treat.contains("CONDITION_DEPENDENT")) {
            treat = treat.split(" ")[0];
        }

        Obligatoriness obligatorinessConverted = Obligatoriness.valueOf(treat);

        switch (obligatorinessConverted) {
            case MANDATORY:
                break;
            case OPTIONAL:
                String optional = Console.readLine("This is optional! Do you want to answer it?");
                if (optional.equalsIgnoreCase("N") || optional.equalsIgnoreCase("NO")) {
                    return "N";
                }
                break;
            case CONDITION_DEPENDENT:
                String result = "";
                int indexOfQuestion = obligatoriness.toString().indexOf("QUESTION");
                int indexOfSection = obligatoriness.toString().indexOf("SECTION");

                if (indexOfQuestion != -1) {
                    result = obligatoriness.toString().substring(indexOfQuestion);
                } else if (indexOfSection != -1) {
                    result = obligatoriness.toString().substring(indexOfSection);

                }
                String yau = result.replace(")", "");
                boolean conditionDependent = mapObligatoriness.get(yau);
                if (conditionDependent) {
                    return "S";
                } else {
                    return "N";
                }
        }
        return "";
    }


    public List<Answer> getAnswers() {
        return answers;
    }
}
