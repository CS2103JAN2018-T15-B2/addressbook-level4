package seedu.address.model;

import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_PERSON;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;


/**
 * Gradient Descent solver on Person models in prediction of purchasing power
 */
public class GradientDescent {
    public static final String MESSAGE_PREDICTION_SUCCESS = "Prediction success";
    public static final String MESSAGE_PREDICTION_FAIL = "Prediction failed";
    private static GradientDescent instance;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);
    private Model model;

    /**
     * The weights we are calculating
     * [ x1= income ]
     */
    private ArrayList<Double> weights = new ArrayList<>(Arrays.asList(0.0));

    /**
     * The learning rate
     */
    private final Double learningRate = 0.00001;

    /**
     * The amount of epoch of looping through training data
     */
    private final Integer epoch = 30000;


    /**
     * Singleton
     *
     * @param model
     */
    private GradientDescent(Model model) {
        this.model = model;
    }

    public static GradientDescent getInstance(Model model) {
        if (instance == null) {
            instance = new GradientDescent(model);
        }
        return instance;
    }


    /**
     * Drives the whole algorithm to solve the problem
     */
    public CommandResult solve() throws CommandException {


        ObservableList<Person> personList = this.model.getAddressBook().getPersonList();

        /**
         * row -> each entry of data
         * col -> [ income ]
         */
        ArrayList<ArrayList<Double>> matrix = new ArrayList<>(new ArrayList<>());

        /**
         * The actual outcome, this "ActualSpending"
         */
        ArrayList<Double> targets = new ArrayList<>();


        //extract values
        prepareData(personList, matrix, targets);

        //solve
        descent(matrix, targets);

        //update results
        try {
            updateResult(personList, this.weights);
            return new CommandResult(String.format(MESSAGE_PREDICTION_SUCCESS));
        } catch (CommandException e) {
            return new CommandResult(String.format(MESSAGE_PREDICTION_FAIL));
        }
    }


    /**
     * Perform stochastic gradient descent on the input data
     */
    private void descent(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> targets) {
        for (int itt = 0; itt < epoch; itt++) { // fixed amount of training epoch
            for (int r = 0; r < matrix.size(); r++) { //going through each training data
                ArrayList<Double> row = matrix.get(r);
                Double outcome = predict(row);
                Double error = targets.get(r) - outcome;
                for (int i = 0; i < row.size(); i++) {
                    Double deltaW = this.learningRate * error * row.get(i);
                    this.weights.set(i, this.weights.get(i) + deltaW);
                }
            }
        }
    }

    /**
     * extract values
     *
     * @param personList
     * @param matrix
     */
    private void prepareData(ObservableList<Person> personList, ArrayList<ArrayList<Double>> matrix,
                             ArrayList<Double> targets) {


        for (int i = 0; i < personList.size(); i++) {
            double as = personList.get(i).getActualSpending().value;

            //the person has no actual spending recorded
            if (as == 0.0) {
                continue;
            }

            ArrayList<Double> row = new ArrayList<>();
            //record down the actual value
            row.add(personList.get(i).getIncome().value);
            targets.add(as);

            matrix.add(row);
        }
    }

    /**
     * Update the prediction results back to the model
     *
     * @param personList
     * @throws CommandException
     */
    private void updateResult(ObservableList<Person> personList, ArrayList<Double> weights) throws CommandException {
        //update person model
        for (int i = 0; i < personList.size(); i++) {
            if (personList.get(i).getActualSpending().value != 0.0) {
                //the person already has known value of spending
                continue;
            }

            //else update the person with expected spending
            Person p = personList.get(i);
            logger.info("Prediction results: " + this.weights.get(0));
            Person updatedPerson = p.mluUpdatedPerson(p.getExpectedSpending().value * this.weights.get(0));
            //update the model here


            try {
                model.updatePerson(personList.get(i), updatedPerson);
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }
    }

    /**
     * calculate values based on the current weights
     */
    private Double predict(Person person) {
        return this.weights.get(0) * person.getIncome().value;
    }

    /**
     * calculate values based on the current weights
     */
    private Double predict(ArrayList<Double> row) {
        Double sum = 0.0;
        //sum income
        sum += this.weights.get(0) * row.get(0);

        return sum;
    }
}
