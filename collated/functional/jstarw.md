# jstarw
###### /resources/view/PersonDetail.fxml
``` fxml
<?import java.net.URL?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.FlowPane?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.Scene?>

<?import javafx.scene.control.TextField?>
<fx:root type="javafx.stage.Stage" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
         minWidth="450" minHeight="600">
    <scene>
        <Scene>
            <stylesheets>
                <URL value="@ClearTheme.css" />
                <URL value="@Extensions.css" />
            </stylesheets>

            <HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
                <GridPane HBox.hgrow="ALWAYS">
                    <columnConstraints>
                        <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
                    </columnConstraints>
                    <VBox alignment="CENTER_LEFT" minHeight="105" GridPane.columnIndex="0">
                        <padding>
                            <Insets top="5" right="5" bottom="5" left="15" />
                        </padding>
                        <HBox spacing="5" alignment="CENTER_LEFT">
                            <Label fx:id="id" styleClass="cell_big_label">
                                <minWidth>
                                    <!-- Ensures that the label text is never truncated -->
                                    <Region fx:constant="USE_PREF_SIZE" />
                                </minWidth>
                            </Label>
                            <Label fx:id="name" text="\$first" styleClass="cell_big_label" />
                        </HBox>
                        <FlowPane fx:id="tags" />
                        <Label styleClass="cell_small_label" >Phone: </Label>
                        <TextField fx:id="phone" text="/$phone"></TextField>
                        <Label fx:id="address" styleClass="cell_small_label" text="\$address" />
                        <Label fx:id="email" styleClass="cell_small_label" text="\$email" />
                        <Label fx:id="age" styleClass="cell_small_label" text="\$age" />
                        <Label fx:id="income" styleClass="cell_small_label" text="\$income" />
                        <Label fx:id="actualSpending" styleClass="cell_small_label" text="\$actualSpending" />
                        <Label fx:id="isNewClient" styleClass="cell_small_label" text="\$isNewClient" />
                        <Label fx:id="expectedSpending" styleClass="cell_small_label" text="\$expectedSpending" />
                        <Label fx:id="policy" styleClass="cell_small_label" text="\$policy" />
                    </VBox>
                </GridPane>
            </HBox>
        </Scene>
    </scene>
</fx:root>


```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    private void loadPersonDetail(Person person) {
        PersonDetail personDetail = new PersonDetail(person, 1);
        personDetail.show();
    }
    private void loadPersonPage(Person person) {
        loadPage(SEARCH_PAGE_URL + person.getName().fullName);
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonPage(event.getNewSelection().person);
    }

```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    @Subscribe
    private void handlePersonCardDoubleClick(PersonCardDoubleClick event) {
        loadPersonDetail(event.getNewSelection());
    }
}
```
###### /java/seedu/address/ui/PersonDetail.java
``` java
/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonDetail extends UiPart<Stage> {
    private static final String FXML = "PersonDetail.fxml";
    public final Person person;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private TextField phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Label income;
    @FXML
    private Label actualSpending;
    @FXML
    private Label expectedSpending;
    @FXML
    private Label age;
    @FXML
    private Label isNewClient;
    @FXML
    private Label policy;

    public PersonDetail(Person person, int displayedIndex) {
        super("PersonDetail.fxml", new Stage());
        this.person = person;
        registerAsAnEventHandler(this);
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        //@author SoilChang
        income.setText("Income: " + person.getIncome().toString());
        age.setText("Age: " + person.getAge().toString() + " years old");
        email.setText(person.getEmail().value);
        actualSpending.setText("Actual Spending: " + person.getActualSpending().toString());
        expectedSpending.setText("Predicted Spending: " + person.getExpectedSpending().toString());
        isNewClient.setText("New Client");
        if (person.getPolicy().isPresent()) {
            policy.setText("Policy: " + person.getPolicy().get().toString());
        } else {
            policy.setText("Has not applied to any policy");
        }

        if (person.getActualSpending().value != 0.0) {
            // the client has actual income
            actualSpending.setVisible(true);
            isNewClient.setVisible(false);
            expectedSpending.setVisible(false);
        } else {
            actualSpending.setVisible(false);
            isNewClient.setVisible(true);
            expectedSpending.setVisible(true);
        }
        //@author
        person.getTags().forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Equals function.
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof PersonDetail)) {
            return false;
        } else {
            PersonDetail detail = (PersonDetail) other;
            return this.id.getText().equals(detail.id.getText()) && this.person.equals(detail.person);
        }
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        getRoot().show();
    }
}
```
###### /java/seedu/address/ui/PersonCard.java
``` java
    private void setDoubleClickEvent() {
        cardPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        raise(new PersonCardDoubleClick(person));
                    }
                }
            }
        });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        PersonCard card = (PersonCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}
```
###### /java/seedu/address/commons/events/ui/PersonCardDoubleClick.java
``` java
/**
 * Represents a double click event in the Person Card
 */
public class PersonCardDoubleClick extends BaseEvent {

    private final Person newSelection;

    public PersonCardDoubleClick(Person newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public Person getNewSelection() {
        return newSelection;
    }
}
```
###### /java/seedu/address/logic/parser/ShowCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ShowCommand object
 */
public class ShowCommandParser implements Parser<ShowCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ShowCommand
     * and returns an ShowCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ShowCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new ShowCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
```
###### /java/seedu/address/logic/commands/EditCommand.java
``` java
        public void setAge(Age age) {
            this.age = age;
        }
```
###### /java/seedu/address/logic/commands/EditCommand.java
``` java
        public Optional<Age> getAge() {
            return Optional.ofNullable(age);
        }
```
###### /java/seedu/address/logic/commands/ShowCommand.java
``` java
/**
 * Opens up a PersonDetail window
 */
public class ShowCommand extends Command {

    public static final String COMMAND_WORD = "show";

    public static final String MESSAGE_USAGE = "Opens up the details window of a specified person.\n"
            + "Parameters: FULL NAME OF PERSON\n"
            + "Example: " + COMMAND_WORD + " John Doe";;

    public static final String MESSAGE_SUCCESS = "Opened up person detail window";
    public static final String MESSAGE_FAIL = "Failed to open window: person not found.";

    private final NameContainsKeywordsPredicate predicate;

    public ShowCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute() throws CommandException {
        try {
            Person person = model.findOnePerson(predicate);
            loadPersonDetail(person);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (PersonNotFoundException e) {
            throw new CommandException(MESSAGE_FAIL);
        }
    }

    private void loadPersonDetail(Person person) {
        PersonDetail personDetail = new PersonDetail(person, 1);
        personDetail.show();
    }
}
```
###### /java/seedu/address/model/person/Age.java
``` java
/**
 * Represents a Person's age in the address book.
 * Represents a Person's value in the address book
 * Guarantees: immutable; is valid as declare in {@link #isValidAge(Integer)}}
 */
public class Age {
    public static final String AGE_CONSTRAINTS =
            "Persons age must be above 0 years old and under 150.";

    public final Integer value;

    /**
     * @param age a valid value
     */
    public Age(Integer age) {
        requireNonNull(age);
        checkArgument(isValidAge(age), AGE_CONSTRAINTS);
        this.value = age;
    }

    /**
     * checks if the age is valid
     * @param age
     * @return
     */
    public static boolean isValidAge(Integer age) {
        return age >= 0 && age < 150;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Age
                && this.value == ((Age) other).value);
    }

}
```
###### /java/seedu/address/model/ModelManager.java
``` java
    public Person findOnePerson(Predicate<Person> predicate) throws PersonNotFoundException {
        requireNonNull(predicate);
        ObservableList<Person> persons = addressBook.getPersonList();
        for (Person person : persons) {
            if (predicate.test(person)) {
                return person;
            }
        }
        throw new PersonNotFoundException();
    }
```
