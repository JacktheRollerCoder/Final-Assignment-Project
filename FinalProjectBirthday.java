import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class FinalProjectBirthday extends Application {
    private List<FriendBirthday> friends = new ArrayList<>();
    private TextField nameField;
    private TextField birthdayField;
    private TextField likesField;
    private TextArea summaryArea;
    private VBox imageContainer; // Container for images

    private Map<String, String> giftRecommendations;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Birthday Manager");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8));
        grid.setVgap(8);
        grid.setHgap(10);
        
        // Input fields
        nameField = new TextField();
        birthdayField = new TextField();
        likesField = new TextField();
        summaryArea = new TextArea();
        summaryArea.setEditable(false);
        Pane pane = new HBox(10);
        
        // Image container
        imageContainer = new VBox();
        imageContainer.setSpacing(10);

        //Initialize gift recommendations
        initializeGiftRecommendations();

        // Labels and buttons
        grid.add(new Label("Friend's Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Birthday (yyyy-mm-dd):"), 0, 1);
        grid.add(birthdayField, 1, 1);
        grid.add(new Label("Interests:"), 0, 2);
        grid.add(likesField, 1, 2);

        Button addButton = new Button("Add Birthday");
        Button showButton = new Button("Show Birthdays");
        grid.add(addButton, 0, 3);
        grid.add(showButton, 1, 3);
        grid.add(new Label("Summary:"), 0, 4);
        grid.add(summaryArea, 0, 5, 2, 1);
        grid.add(imageContainer, 0, 6, 2, 1); // Add image container to the grid

        // Button actions
        addButton.setOnAction(e -> addBirthday());
        showButton.setOnAction(e -> showBirthdays());

        Scene scene = new Scene(grid, 400, 300);
        
        // Load CSS
        URL cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS file not found!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGiftRecommendations() {
        giftRecommendations = new HashMap<>();
        giftRecommendations.put("books", "A bestseller novel or a gift card to a bookstore.");
        giftRecommendations.put("music", "Concert tickets or a vinyl record of their favorite artist.");
        giftRecommendations.put("games", "The latest video game or a fun board game.");
        giftRecommendations.put("cooking", "A gourmet cooking class or a kitchen gadget.");
        giftRecommendations.put("travel", "You could include kayaks, bycicles, or car recommendations.");
        giftRecommendations.put("art", "There are many drawing tools, paint, and paper that are perfect for making art");
        giftRecommendations.put("sports", "You could get some shoes, gloves, water bottles, and other essentials for sports.");
        giftRecommendations.put("toys", "Try picking out the most popular kinds of toys; Legos, action figures, puzzles, etc.");
        giftRecommendations.put("movies", "If you want physical copies, feel free to pick out any movies that include action, drama, \n or has their favorite celebrity. (Note: Most movies are in streaming services).");
        // Add more interests and corresponding gift ideas as needed
    }

    private void addBirthday() {
        String friendName = nameField.getText().trim();
        String birthdayInput = birthdayField.getText();
        String friendLikes = likesField.getText();
        LocalDate birthday;

        if(friendName.isEmpty()) {
            showAlert("Error", "Friend's name is required. Please add a name!");
            return;
        }

        try {
            birthday = LocalDate.parse(birthdayInput, DateTimeFormatter.ISO_LOCAL_DATE);
            friends.add(new FriendBirthday(friendName, birthday, friendLikes));
            showAlert("Success", "Birthday added for " + friendName);
            nameField.clear();
            birthdayField.clear();
            likesField.clear();
        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid date format. Use yyyy-mm-dd.");
        } 
    }

    private void showBirthdays() {
        StringBuilder summary = new StringBuilder();
        LocalDate today = LocalDate.now();
        
        summary.append("--- Summary of Friends' Birthdays ---\n");
        imageContainer.getChildren().clear(); // Clear previous images
        
        for (FriendBirthday friend : friends) {
            LocalDate nextBirthday = friend.getBirthday().withYear(today.getYear());
            String interests = friend.getFriendLikes();

            if (nextBirthday.isEqual(today)) {
                summary.append("It is ").append(friend.getFriendName()).append("'s Birthday! Go celebrate!\n");

                // Create and add the ImageView
                Image image = new Image("file:cupcakeImage.jpg");
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                
                imageContainer.getChildren().add(imageView); // Add image to the container

                if (interests == null || interests.isEmpty()) {
                    summary.append("No interests included!\n").append("\n");
                } else {
                    summary.append("Interests include: ").append(interests).append(".\n");
                    summary.append(getGiftRecommendation(interests)).append("\n");
                }

            } else {
                if (nextBirthday.isBefore(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }
                long daysUntilBirthday = java.time.temporal.ChronoUnit.DAYS.between(today, nextBirthday);
                summary.append(friend.getFriendName()).append("'s birthday is in ")
                       .append(daysUntilBirthday).append(" days on ")
                       .append(nextBirthday).append(".\n");

                if (interests == null || interests.isEmpty()) {
                        summary.append("No interests included!\n").append("\n");
                } else {
                        summary.append("Interests include: ").append(interests).append(".\n");
                        summary.append(getGiftRecommendation(interests)).append("\n");
                }

            }

        }

        summaryArea.setText(summary.toString());
    }

    private String getGiftRecommendation(String interests) {
        StringBuilder recommendations = new StringBuilder("Gift Recommendations:\n");
        String[] interestArray = interests.split(",\\s*"); // Split interests by comma

        for (String interest : interestArray) {
            String gift = giftRecommendations.get(interest.toLowerCase());
            if (gift != null) {
                recommendations.append("- ").append(gift).append("\n");
            }
        }

        return recommendations.length() > 0 ? recommendations.toString() : "No gift recommendations available.";
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

class FriendBirthday {
    private String friendName;
    private LocalDate birthday;
    private String friendLikes;

    public FriendBirthday(String friendName, LocalDate birthday, String friendLikes) {
        this.friendName = friendName;
        this.birthday = birthday;
        this.friendLikes = friendLikes;
    }

    public String getFriendName() {
        return friendName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getFriendLikes() {
        return friendLikes;
    }
}
