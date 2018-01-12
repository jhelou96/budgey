package net.budgey.app;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.budgey.app.views.BankAccountsController;
import net.budgey.app.views.BudgetController;
import net.budgey.app.views.BudgetDetailsController;
import net.budgey.app.views.DashboardController;
import net.budgey.app.views.RootLayoutController;
import net.budgey.app.views.SettingsController;

/**
 * Serves as the main controller for the application.
 * It links all the pages of the application together
 *
 */
public class MainApp extends Application {

	/**
	 * JavaFX stage for the application
	 * @see MainApp#getPrimaryStage()
	 */
    private Stage primaryStage;
    
    /**
     * JavaFX root layout.
     * This layout is common for all the pages
     */
    private BorderPane rootLayout;
    
    /**
     * Bundle for the i18n system
     * BankAccounts.fxml layout
     */
    private ResourceBundle bankAccountsBundle;
    
    /**
     * Bundle for the i18n system
     * Budget.fxml layout
     */
    private ResourceBundle budgetBundle;
    
    /**
     * Bundle for the i18n system
     * BudgetDetails.fxml layout
     */
    private ResourceBundle budgetDetailsBundle;

    /**
     * Bundle for the i18n system
     * Dashboard.fxml layout
     */
    private ResourceBundle dashboardBundle;

    /**
     * Bundle for the i18n system
     * Settings.fxml layout
     */
    private ResourceBundle settingsBundle;
    
    /**
     * Currency to display
     * Taken from the config file
     */
    private String currency;
    
    /**
     * MainApp constructor
     * @throws FileNotFoundException if config file not found
     * @throws JSONException if JSON file could not be parsed
     */
    public MainApp() throws JSONException, FileNotFoundException {
		JSONObject config = Config.parseConfigFile();
		
		//Load currency
		currency = config.getString("currency");

		//Load i18n bundles
		Locale locale = new Locale(config.getString("lang"), ""); //Get lang from config file
		bankAccountsBundle = ResourceBundle.getBundle("net.budgey.app.lang.bankAccounts", locale);
		budgetBundle = ResourceBundle.getBundle("net.budgey.app.lang.budget", locale);
		budgetDetailsBundle = ResourceBundle.getBundle("net.budgey.app.lang.budgetDetails", locale);
		dashboardBundle = ResourceBundle.getBundle("net.budgey.app.lang.dashboard", locale);
		settingsBundle = ResourceBundle.getBundle("net.budgey.app.lang.settings", locale);
    }
    
    /**
     * Initializes the application stage
     * @param primaryStage
     * @throws Exception if the FXML file(s) is/are not found
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    		//Prepare application stage
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Budgey");
        this.primaryStage.setResizable(true);
        this.primaryStage.setFullScreen(false);
        this.primaryStage.setFullScreenExitHint("");
        this.primaryStage.getIcons().add(new Image("file:resources/images/logo.png")); //Application icon
        
        //Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("views/RootLayout.fxml"));
        rootLayout = (BorderPane) loader.load();
        
        //Load the controller of the FXML file and sent some parameters to it
	    RootLayoutController rootLayoutController = loader.getController();
	    rootLayoutController.setMainApp(this);
	    
        //Load the dashboard as the index page
        showDashboard();
        
        //Once everything is loaded, display the scene
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        rootLayout.requestLayout();
    }

    /**
     * Loads the dashboard page inside the root layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded
     */
    public void showDashboard() throws Exception {
    		//Load the dashboard.fxml layout in the root layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("views/Dashboard.fxml"));
        loader.setResources(dashboardBundle);
        HBox dashboard = (HBox) loader.load();

        //Set the layout at the center of the root layout
        VBox center = (VBox) rootLayout.getCenter();
        AnchorPane body = (AnchorPane) center.getChildren().get(2);
        
        //VBox is set to the size of the body of the root layout
        dashboard.prefHeightProperty().bind(body.heightProperty());
        dashboard.prefWidthProperty().bind(body.widthProperty());
        
        //Clear the content of the body and include the FXML loaded layout
        body.getChildren().clear();
        body.getChildren().add(dashboard);
        
        //Load the controller of the FXML file and sent some parameters to it
	    DashboardController dashboardController = loader.getController();
	    dashboardController.setBundle(dashboardBundle);
	    dashboardController.setMainApp(this);
	    dashboardController.load();
    }
    
    /**
     * Loads the bank accounts page inside the root layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded
     * @see BankAccountsController
     */
    public void showBankAccounts() throws Exception {
		//Load the BankAccounts.fxml layout in the root layout
	    FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(MainApp.class.getResource("views/BankAccounts.fxml"));
	    loader.setResources(bankAccountsBundle);
	    SplitPane bankAccounts = (SplitPane) loader.load();
	
	    //Set the layout at the center of the root layout
	    VBox center = (VBox) rootLayout.getCenter();
	    AnchorPane body = (AnchorPane) center.getChildren().get(2);
	    
	    //VBox is set to the size of the body of the root layout
	    bankAccounts.prefHeightProperty().bind(body.heightProperty());
	    bankAccounts.prefWidthProperty().bind(body.widthProperty());
	    
	    //Clear the content of the body and include the FXML loaded layout
	    body.getChildren().clear();
	    body.getChildren().add(bankAccounts);
	    
	    //Load the controller of the FXML file and sent some parameters to it
	    BankAccountsController bankAccountsController = loader.getController();
	    bankAccountsController.setMainApp(this);
	    bankAccountsController.setBundle(bankAccountsBundle);
	    
	    //Populate the table with the list of bank accounts
	    bankAccountsController.populateBankAccountsTable();
	}
    
    /**
     * Loads the budget page inside the root layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded
     * @see BudgetController
     */
    public void showBudget() throws Exception {
    		//Load the Budget.fxml layout in the root layout
    		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(MainApp.class.getResource("views/Budget.fxml"));
	    loader.setResources(budgetBundle);
	    SplitPane budget = (SplitPane) loader.load();
	    
	    //Set the layout at the center of the root layout
	    VBox center = (VBox) rootLayout.getCenter();
	    AnchorPane body = (AnchorPane) center.getChildren().get(2);
	    
	    //VBox is set to the size of the body of the root layout
	    budget.prefHeightProperty().bind(body.heightProperty());
	    budget.prefWidthProperty().bind(body.widthProperty());
	    
	    //Clear the content of the body and include the FXML loaded layout
	    body.getChildren().clear();
	    body.getChildren().add(budget);
	    
	    //Load the controller of the FXML file and sent some parameters to it
	    BudgetController budgetController = loader.getController();
	    budgetController.setMainApp(this);
	    budgetController.setBundle(budgetBundle);
	    budgetController.load();
    }
    
    /**
     * Loads the budget page inside the root layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded
     * @see BudgetDetailsController
     */
    public void showBudgetDetails() throws Exception {
    		//Load the BudgetDetails.fxml layout in the root layout
    		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(MainApp.class.getResource("views/BudgetDetails.fxml"));
	    loader.setResources(budgetDetailsBundle);
	    VBox budget = (VBox) loader.load();
	    
	    //Set the layout at the center of the root layout
	    VBox center = (VBox) rootLayout.getCenter();
	    AnchorPane body = (AnchorPane) center.getChildren().get(2);
	    
	    //VBox is set to the size of the body of the root layout
	    budget.prefHeightProperty().bind(body.heightProperty());
	    budget.prefWidthProperty().bind(body.widthProperty());
	    
	    //Clear the content of the body and include the FXML loaded layout
	    body.getChildren().clear();
	    body.getChildren().add(budget);
	    
	    //Load the controller of the FXML file and sent some parameters to it
	    BudgetDetailsController budgetDetailsController = loader.getController();
	    budgetDetailsController.setMainApp(this);
	    budgetDetailsController.setBundle(budgetDetailsBundle);
    }
    
    /**
     * Loads the settings page inside the root layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded
     */
    public void showSettings() throws Exception {
    		//Load the dashboard.fxml layout in the root layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("views/Settings.fxml"));
        loader.setResources(settingsBundle);
        VBox settings = (VBox) loader.load();

        //Set the layout at the center of the root layout
        VBox center = (VBox) rootLayout.getCenter();
        AnchorPane body = (AnchorPane) center.getChildren().get(2);
        
        //VBox is set to the size of the body of the root layout
        settings.prefHeightProperty().bind(body.heightProperty());
        settings.prefWidthProperty().bind(body.widthProperty());
        
        //Clear the content of the body and include the FXML loaded layout
        body.getChildren().clear();
        body.getChildren().add(settings);
        
        //Load the controller of the FXML file and sent some parameters to it
	    SettingsController settingsController = loader.getController();
	    settingsController.setMainApp(this);
	    settingsController.setBundle(settingsBundle);
	    settingsController.load();
    }
    
    /**
     * Used to give access to FXML controllers to the main stage
     * @return the main stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Used to give access to controllers to currency
     * @return String currency
     */
    public String getCurrency() {
    		return currency;
    }
}
