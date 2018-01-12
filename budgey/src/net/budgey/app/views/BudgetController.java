package net.budgey.app.views;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
import net.budgey.app.Config;
import net.budgey.app.MainApp;
import net.budgey.app.models.BankAccount;
import net.budgey.app.models.BankAccountDAO;
import net.budgey.app.models.Category;
import net.budgey.app.models.CategoryDAO;
import net.budgey.app.models.Operation;
import net.budgey.app.models.OperationDAO;

public class BudgetController {
	/**
	 * JavaFX list containing the list of operations
	 */
	@FXML
    private ListView<Operation> operationsList;
	
	/**
	 * JavaFX table containing the list of categories
	 */
	@FXML
    private TableView<Category> categoriesTable;
	
	/**
	 * JavaFX table column listing the categories ID
	 */
	@FXML
	private TableColumn<Category, Number> idColumn;
	
	/**
	 * JavaFX table column listing the categories name
	 */
	@FXML
	private TableColumn<Category, String> nameColumn;
	
	/**
	 * JavaFX table column listing the categories description
	 */
	@FXML
	private TableColumn<Category, String> descriptionColumn;
	
	/**
	 * JavaFX button allowing the user to delete a category
	 */
	@FXML
	private Button deleteCategoryButton;
	
	/**
	 * JavaFX button allowing the user to edit a category
	 */
	@FXML 
	private Button editCategoryButton;
	
	/**
	 * JavaFX button allowing the user to delete an operation
	 */
	@FXML
	private Button deleteOperationButton;
	
	/**
	 * JavaFX button allowing the user to edit an operation
	 */
	@FXML 
	private Button editOperationButton;
	
	/**
	 * JavaFX PieChart allowing the user to see the amount of expenses per category
	 */
	@FXML
	private PieChart expensesChart;
	
	/**
	 * JavaFX PieChart allowing the user to see the amount of income per category
	 */
	@FXML
	private PieChart incomeChart;
	
	/**
	 * Table data containing the categories information
	 */
	private ObservableList<Category> categoriesData = FXCollections.observableArrayList();
	
	/**
	 * Category DB manager
	 * @see CategoryDAO
	 */
	private CategoryDAO categoryDAO;
	
	/**
	 * Operation DB manager
	 * @see OperationDAO
	 */
	private OperationDAO operationDAO;
	
	/**
	 * Bank account DB manager
	 * @see OperationDAO
	 */
	private BankAccountDAO bankAccountDAO;
	
	/**
     * Bundle for the i18n system
     */
    private ResourceBundle bundle;
    
    /**
     * Main controller
     * @see MainApp
     */
    private MainApp mainApp;
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     * Used to intialize the DAOs
     * @throws FileNotFoundException if JSON file could not be loaded
     */
	public BudgetController() throws FileNotFoundException {
		categoryDAO = new CategoryDAO();
		operationDAO = new OperationDAO();
		bankAccountDAO = new BankAccountDAO();
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
	@FXML
    private void initialize() {
        //Initialize the categories table with the three columns.
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		
		//If no row is selected, delete and edit buttons are disabled
		deleteCategoryButton.disableProperty().bind(Bindings.isEmpty(categoriesTable.getSelectionModel().getSelectedItems()));
		editCategoryButton.disableProperty().bind(Bindings.isEmpty(categoriesTable.getSelectionModel().getSelectedItems()));
		deleteOperationButton.disableProperty().bind(Bindings.isEmpty(operationsList.getSelectionModel().getSelectedItems()));
		editOperationButton.disableProperty().bind(Bindings.isEmpty(operationsList.getSelectionModel().getSelectedItems()));
	}
	
	/**
	 * Loads data in layout page
	 * Method first called from outside: MainApp class
	 * Also called everytime there is an update to the data (edit, delete, add)
	 * @see MainApp#showBudget()
	 * @throws Exception if any loaded data throws an exception
	 */
	public void load() throws Exception {
		//Populate the expenses PieChart with data
		populateExpensesChart();
		
		//Populate the income PieChart with data
		populateIncomeChart();
		
		//Populate the table with the list of categories
	    populateCategoriesTable();
	}
	
	/**
	 * Allows the user to add a new operation
	 * @throws Exception if any exception is thrown by the corresponding models
	 * @see OperationDAO#add()
	 */
	public void newOperation() throws Exception {
		//New dialog
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("newOperation"));
		dialog.setHeaderText(bundle.getString("newOperation"));
		dialog.initOwner(mainApp.getPrimaryStage());

		//Icon
		ImageView dialogIcon = new ImageView();
		dialogIcon.setImage(new Image("images/dialogs/new.png"));
		dialog.setGraphic(dialogIcon);
		
		//Buttons
		ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
		
		//Form box
		VBox form = new VBox();
		form.setSpacing(10);
		
		//Amount field
		TextField amount = new TextField();
		amount.setPromptText(bundle.getString("amount"));
		form.getChildren().add(amount);
		
		//Description field
		TextField description = new TextField();
		description.setPromptText(bundle.getString("description"));
		form.getChildren().add(description);
		
		//Operation type field
		ComboBox<String> operationType = new ComboBox<String>();
		operationType.setMaxWidth(Double.MAX_VALUE);
		operationType.setPromptText(bundle.getString("operationType"));
		operationType.getItems().addAll(bundle.getString("income"), bundle.getString("expenses"));
		form.getChildren().add(operationType);
		
		//Bank account field
		ComboBox<BankAccount> bankAccount = new ComboBox<BankAccount>();
		bankAccount.setMaxWidth(Double.MAX_VALUE);
		bankAccount.setPromptText(bundle.getString("bankAccount"));
		ArrayList<BankAccount> bankAccounts = bankAccountDAO.getBankAccounts();
		for(int i = 0; i < bankAccounts.size(); i++)
			bankAccount.getItems().add(bankAccounts.get(i));
		bankAccount.setConverter(new StringConverter<BankAccount>() {
		    @Override
		    public String toString(BankAccount object) {
		        return object.getBankName() + " - " + object.getDescription();
		    }
		    @Override
		    public BankAccount fromString(String string) {
		        return bankAccount.getItems().stream().filter(ap -> 
		            ap.getBankName().equals(string)).findFirst().orElse(null);
		    }
		});
		form.getChildren().add(bankAccount);
		
		//Category field
		ComboBox<Category> category = new ComboBox<Category>();
		category.setMaxWidth(Double.MAX_VALUE);
		category.setPromptText(bundle.getString("category"));
		ArrayList<Category> categories = categoryDAO.getCategories();
		for(int i = 0; i < categories.size(); i++)
			category.getItems().add(categories.get(i));
		category.setConverter(new StringConverter<Category>() {
		    @Override
		    public String toString(Category object) {
		        return object.getName();
		    }
		    @Override
		    public Category fromString(String string) {
		        return category.getItems().stream().filter(ap -> 
		            ap.getName().equals(string)).findFirst().orElse(null);
		    }
		});
		form.getChildren().add(category);
		
		dialog.getDialogPane().setContent(form);
		
		//Dialog validation
		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
        		//Try to save the operation
			try {
				Operation operation = new Operation();
				
				if(Config.isDouble(amount.getText()))
					operation.setAmount(Double.parseDouble(amount.getText()));
				else 
					throw new Exception("invalidOperationAmount");
				
				operation.setDescription(description.getText());
				
				if(operationType.getValue() == bundle.getString("income"))
					operation.setOperationType(1);
				else if(operationType.getValue() == bundle.getString("expenses"))
					operation.setOperationType(2);
				else
					throw new Exception("invalidOperationType");
				
				//Check if bank account is specified
				if(bankAccount.getValue() == null)
					throw new Exception("invalidOperationBankAccount");
				operation.setIdBankAccount(bankAccount.getValue().getId());
				
				//Check if category is specified
				if(category.getValue() == null)
					throw new Exception("invalidOperationCategory");
				operation.setIdCategory(category.getValue().getId());
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				operation.setDate(dateFormat.format(date));
				
				operationDAO.add(operation);
				load(); //Refresh data
			} catch(Exception e) {
				event.consume();
				
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle(bundle.getString("error.dialog.title"));
				alert.setHeaderText(bundle.getString("error.dialog.header"));
				alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
				alert.show();
			}
        });
		
		dialog.showAndWait();
    }
	
	/**
	 * Displays a category latest operations performed
	 * Category is specified when selecting a row in the tableView
	 * @throws Exception if JSON file not found or if category entity raises an exception
	 * @see OperationDAO#getOperations()
	 */
	public void showActivity() throws Exception {
		//Default text if no items in operations list
		operationsList.setPlaceholder(new Label(bundle.getString("noOperationsYet")));
		
		//Get category list of operations
		Category category = (Category) categoriesTable.getSelectionModel().getSelectedItem();
		if(category != null) {
			ArrayList<Operation> operations = operationDAO.getOperations(category);
			
			//Add operations to GUI list
			operationsList.setItems(FXCollections.observableList(operations));
			operationsList.setCellFactory(param -> new ListCell<Operation>() {
			    @Override
			    protected void updateItem(Operation item, boolean empty) {
			        super.updateItem(item, empty);
	
			        if(!empty && item != null) {
			        		//Apply different style based on operation type
			        		ImageView imageView = new ImageView();
				        	if(item.getOperationType() == 1) { //If operation represent an income
			            		setStyle("-fx-text-fill: rgb(23, 132, 49);");
				        		imageView.setImage(new Image("images/income.png"));
			            } else {
				            	setStyle("-fx-text-fill: rgb(160, 36, 36);");
				        		imageView.setImage(new Image("images/expenses.png"));
			            }
				        	
			        		setGraphic(imageView);
			        		
			        		String[] date = item.getDate().split("-");
			        		
			            setText("[" + date[2] + "/" + date[1] + "/" + date[0] + "] " + item.getAmount() + mainApp.getCurrency());
			        }
			    }
			});
		}
	}
	
	/**
	 * Allows the user to edit an operation
	 * @throws Exception if operation could not be edited
	 * @see OperationDAO#update()
	 */
	public void editOperation() throws Exception {
    		Operation operation = operationsList.getSelectionModel().getSelectedItem();
       
    		//New dialog
    		Dialog<Pair<String, String>> dialog = new Dialog<>();
    		dialog.setTitle(bundle.getString("editOperation"));
    		dialog.setHeaderText(bundle.getString("editOperation"));
    		dialog.initOwner(mainApp.getPrimaryStage());

    		//Icon
    		ImageView dialogIcon = new ImageView();
    		dialogIcon.setImage(new Image("images/dialogs/edit.png"));
    		dialog.setGraphic(dialogIcon);
    		
    		//Buttons
    		ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
    		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
    		
    		//Form box
    		VBox form = new VBox();
    		form.setSpacing(10);
    		
    		//Amount field
    		TextField amount = new TextField();
    		amount.setText(operation.getAmount() + "");
    		form.getChildren().add(amount);
    		
    		//Description field
    		TextField description = new TextField();
    		description.setText(operation.getDescription());
    		form.getChildren().add(description);
    		
    		//Operation type field
    		ComboBox<String> operationType = new ComboBox<String>();
    		operationType.setMaxWidth(Double.MAX_VALUE);
    		operationType.getItems().addAll(bundle.getString("income"),bundle.getString("expenses"));
    		if(operation.getOperationType() == 1) 
    			operationType.setValue(bundle.getString("income"));
    		else
    			operationType.setValue(bundle.getString("expenses"));
    		
    		form.getChildren().add(operationType);
    		
    		//Bank account field
    		ComboBox<BankAccount> bankAccount = new ComboBox<BankAccount>();
    		bankAccount.setMaxWidth(Double.MAX_VALUE);
    		ArrayList<BankAccount> bankAccounts = bankAccountDAO.getBankAccounts();
    		for(int i = 0; i < bankAccounts.size(); i++) {
    			bankAccount.getItems().add(bankAccounts.get(i));
    			
    			//Set operation bank account selected by default from list of bank accounts
    			if(bankAccounts.get(i).getId() == operation.getIdBankAccount())
    				bankAccount.setValue(bankAccounts.get(i));
    		}
    		bankAccount.setConverter(new StringConverter<BankAccount>() {
    		    @Override
    		    public String toString(BankAccount object) {
    		        return object.getBankName() + " - " + object.getDescription();
    		    }
    		    @Override
    		    public BankAccount fromString(String string) {
    		        return bankAccount.getItems().stream().filter(ap -> 
    		            ap.getBankName().equals(string)).findFirst().orElse(null);
    		    }
    		});
    		form.getChildren().add(bankAccount);
    		
    		//Category field
    		ComboBox<Category> category = new ComboBox<Category>();
    		category.setMaxWidth(Double.MAX_VALUE);
    		ArrayList<Category> categories = categoryDAO.getCategories();
    		for(int i = 0; i < categories.size(); i++) {
    			category.getItems().add(categories.get(i));
    			
    			//Set operation category selected by default from list of categories
    			if(categories.get(i).getId() == operation.getIdCategory())
    				category.setValue(categories.get(i));
    		}
    		category.setConverter(new StringConverter<Category>() {
    		    @Override
    		    public String toString(Category object) {
    		        return object.getName();
    		    }
    		    @Override
    		    public Category fromString(String string) {
    		        return category.getItems().stream().filter(ap -> 
    		            ap.getName().equals(string)).findFirst().orElse(null);
    		    }
    		});
    		form.getChildren().add(category);
    		
    		dialog.getDialogPane().setContent(form);
    		
    		//Dialog validation
    		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
    			try {
    				if(Config.isDouble(amount.getText()))
    					operation.setAmount(Double.parseDouble(amount.getText()));
    				else 
    					throw new Exception("invalidOperationAmount");
    				
    				operation.setDescription(description.getText());
    				
    				if(operationType.getValue() == bundle.getString("income"))
    					operation.setOperationType(1);
    				else if(operationType.getValue() == bundle.getString("expenses"))
    					operation.setOperationType(2);
    				else
    					throw new Exception("invalidOperationType");
    				
    				//Check if bank account is specified
    				if(bankAccount.getValue() == null)
    					throw new Exception("invalidOperationBankAccount");
    				operation.setIdBankAccount(bankAccount.getValue().getId());
    				
    				//Check if category is specified
    				if(category.getValue() == null)
    					throw new Exception("invalidOperationCategory");
    				operation.setIdCategory(category.getValue().getId());
    				
    				operationDAO.update(operation);
    				load(); //Refresh data
        		} catch(Exception e) {
        			event.consume();
    				
    				Alert alert = new Alert(AlertType.WARNING);
    				alert.initOwner(mainApp.getPrimaryStage());
    				alert.setTitle(bundle.getString("error.dialog.title"));
    				alert.setHeaderText(bundle.getString("error.dialog.header"));
    				alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
    				alert.show();
        		}
        });
        
        dialog.showAndWait();
	}
	
	/**
	 * Allows user to remove an operation
	 * @throws Exception if operation could not be removed
	 *  @see OperationDAO#remove()
	 */
	public void deleteOperation() throws Exception {
		//Show a confirmation message
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(bundle.getString("confirmation.dialog.title"));
		alert.setHeaderText(bundle.getString("confirmation.dialog.header.removeOperation"));
		alert.setContentText(bundle.getString("confirmation.dialog.text.removeOperation"));
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.OK) {
			Operation operation = operationsList.getSelectionModel().getSelectedItem();
			operationDAO.remove(operation);
			load(); //Refresh data
		}
	}
		                
	/**
	 * Allows the user to create a new category
	 * @see CategoryDAO#add()
	 */
	public void newCategory() {
		//New dialog
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("newCategory"));
		dialog.setHeaderText(bundle.getString("createNewCategory"));
		dialog.initOwner(mainApp.getPrimaryStage());

		//Icon
		ImageView dialogIcon = new ImageView();
		dialogIcon.setImage(new Image("images/dialogs/new.png"));
		dialog.setGraphic(dialogIcon);
		
		//Buttons
		ButtonType submitButtonType = new ButtonType(bundle.getString("submit"), ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
		
		//Form box
		VBox form = new VBox();
		form.setSpacing(10);
		
		//Category name field
		TextField categoryName = new TextField();
		categoryName.setPromptText(bundle.getString("categoryName"));
		form.getChildren().add(categoryName);
		
		//Category description field
		TextField categoryDescription = new TextField();
		categoryDescription.setPromptText(bundle.getString("description"));
		form.getChildren().add(categoryDescription);
		
		dialog.getDialogPane().setContent(form);
		
		//Dialog validation
		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
        		//Try to save the category
			try {
				Category category = new Category();
				category.setName(categoryName.getText());
				
				//Check if category with same name already exists
				ArrayList<Category> categories = categoryDAO.getCategories();
				for(int i = 0; i < categories.size(); i++) {
					if(categories.get(i).getName().equalsIgnoreCase(category.getName()))
						throw new Exception("categoryWithSameNameAlreadyExists");
				}
				
				category.setDescription(categoryDescription.getText());
				
				categoryDAO.add(category);
				
				load(); //Refresh data
			} catch(Exception e) {
				event.consume();
				
				//Display an alert dialog if exception was thrown
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle(bundle.getString("error.dialog.title"));
				alert.setHeaderText(bundle.getString("error.dialog.header"));
				alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
				alert.show();
			}
        });
		
		dialog.showAndWait();
    }
	
	/**
	 * Allows the user to edit a category
	 * @see CategoryDAO#update()
	 */
	public void editCategory() {
		//New dialog
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("editCategory"));
		dialog.setHeaderText(bundle.getString("editCategoryInfo"));
		dialog.initOwner(mainApp.getPrimaryStage());

		//Icon
		ImageView dialogIcon = new ImageView();
		dialogIcon.setImage(new Image("images/dialogs/edit.png"));
		dialog.setGraphic(dialogIcon);
		
		//Buttons
		ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
		
		//Form box
		VBox form = new VBox();
		form.setSpacing(10);
		
		//Category name field
		TextField categoryName = new TextField();
		categoryName.setText(categoriesTable.getSelectionModel().getSelectedItem().getName() + "");
		categoryName.setPromptText(bundle.getString("categoryName"));
		form.getChildren().add(categoryName);
		
		//Category description field
		TextField categoryDescription = new TextField();
		categoryDescription.setText(categoriesTable.getSelectionModel().getSelectedItem().getDescription() + "");
		form.getChildren().add(categoryDescription);
		
		dialog.getDialogPane().setContent(form);
		
		//Dialog validation
		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
        		//Try to update the category
			try {
				Category category = categoriesTable.getSelectionModel().getSelectedItem();
				category.setName(categoryName.getText());
				category.setDescription(categoryDescription.getText());
				
				categoryDAO.update(category);
				
				//Redirect user to the budget view layout
				mainApp.showBudget();
			} catch(Exception e) {
				event.consume();
				
				//Display an alert dialog if exception was thrown
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle(bundle.getString("error.dialog.title"));
				alert.setHeaderText(bundle.getString("error.dialog.header"));
				alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
				alert.show();
			}
        });
		
		dialog.showAndWait();
    }
	
	/**
	 * Allows the user to delete a category after confirmation
	 * @throws Exception if category could not be removed
	 */
	public void removeCategory() throws Exception {
		//Show a confirmation message
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(bundle.getString("confirmation.dialog.title"));
		alert.setHeaderText(bundle.getString("confirmation.dialog.header.removeCategory"));
		alert.setContentText(bundle.getString("confirmation.dialog.text.removeCategory"));
		Optional<ButtonType> result = alert.showAndWait();
		
		//Delete the category if the user confirmed
		if (result.get() == ButtonType.OK) {
			Category category = categoriesTable.getSelectionModel().getSelectedItem();
		    
			operationDAO.remove(category); //Removes all the operations of the category
			categoryDAO.remove(category);
			
			load(); //Refresh data
		}
	}
	
	/**
	 * Switches user to the BudgetDetails.fxml layout
	 * @throws Exception if FXML file not found or if JSON data cannot be loaded
	 * @see MainApp#showBudgetDetails()
	 */
	public void showBudgetDetails() throws Exception {
		mainApp.showBudgetDetails();
	}

	/**
	 * Populates the JavaFX table with the list of categories
	 * @throws Exception if Category entity throws exception or if JSON file not found
	 * @see CategoryDAO#getCategories()
	 */
	public void populateCategoriesTable() throws Exception {
		//Reset table data
		categoriesTable.getItems().clear();
		
		//Get list of categories
		ArrayList<Category> categories = categoryDAO.getCategories();
	
		//Add the list of categories to the table
		for(int i = 0; i < categories.size(); i++) {
			//We populate the rows
			categoriesData.add((Category) categories.get(i));
		}
		
		//Add observable list data to the table
	    categoriesTable.setItems(categoriesData);
	    
	    //Text to show if no categories found
	    categoriesTable.setPlaceholder(new Label(bundle.getString("noCategoryFound")));
	}
	
	/**
	 * Populates the JavaFX pie chart with the amount of expenses per category
	 * @throws Exception if categoryDAO throws an exception
	 * @see categoryDAO#getCategories()
	 */
	public void populateExpensesChart() throws Exception {
		//Reset chart data
		expensesChart.getData().clear();
		
		//Get list of categories
		ArrayList<Category> categories = categoryDAO.getCategories();
		
		//Compute the amount of expenses per category
		TreeMap<String, Double> expensesPerCategory = new TreeMap<String, Double>(); //TreeMap with category name as key and total expenses amount related to that category as value
		for(int i = 0; i < categories.size(); i++) {
			//Get all the operations related to a specific category
			ArrayList<Operation> operations = operationDAO.getOperations(categories.get(i));
			for(int j = 0; j < operations.size(); j++) {
				//If operation is an income, ignore it
				if(operations.get(j).getOperationType() == 1)
					continue;
				
				/*
				 * If category has already been registered as key in TreeMap, get previous expenses amount and add to it new amount
				 * Otherwise create new key with operation amount as value
				 */
				if(expensesPerCategory.containsKey(categories.get(i).getName()))
					expensesPerCategory.put(categories.get(i).getName(), expensesPerCategory.get(categories.get(i).getName()) + operations.get(j).getAmount());
				else
					expensesPerCategory.put(categories.get(i).getName(), operations.get(j).getAmount());
			}
		}
		
		//Populate PieChart
		ObservableList<PieChart.Data> expensesChartData = FXCollections.observableArrayList();
		Set<Entry<String, Double>> expensesSet = expensesPerCategory.entrySet();
	    Iterator<Entry<String, Double>> i = expensesSet.iterator();
	    while(i.hasNext()) {
	    		@SuppressWarnings("rawtypes")
			Map.Entry categoryExpenses = (Map.Entry)i.next();
	    		expensesChartData.add(new PieChart.Data(categoryExpenses.getKey().toString(), Double.parseDouble(categoryExpenses.getValue().toString())));
	    	}
		expensesChart.setData(expensesChartData);
	}
	
	/**
	 * Populates the JavaFX pie chart with the amount of income per category
	 * @throws Exception if categoryDAO throws an exception
	 * @see categoryDAO#getCategories()
	 */
	public void populateIncomeChart() throws Exception {
		//Reset chart data
		incomeChart.getData().clear();
				
		//Get list of categories
		ArrayList<Category> categories = categoryDAO.getCategories();
		
		//Compute the amount of expenses per category
		TreeMap<String, Double> incomePerCategory = new TreeMap<String, Double>(); //TreeMap with category name as key and total expenses amount related to that category as value
		for(int i = 0; i < categories.size(); i++) {
			//Get all the operations related to a specific category
			ArrayList<Operation> operations = operationDAO.getOperations(categories.get(i));
			for(int j = 0; j < operations.size(); j++) {
				//If operation is an expenses, ignore it
				if(operations.get(j).getOperationType() == 2)
					continue;
				
				/*
				 * If category has already been registered as key in TreeMap, get previous expenses amount and add to it new amount
				 * Otherwise create new key with operation amount as value
				 */
				if(incomePerCategory.containsKey(categories.get(i).getName()))
					incomePerCategory.put(categories.get(i).getName(), incomePerCategory.get(categories.get(i).getName()) + operations.get(j).getAmount());
				else
					incomePerCategory.put(categories.get(i).getName(), operations.get(j).getAmount());
			}
		}
		
		//Populate PieChart
		ObservableList<PieChart.Data> incomeChartData = FXCollections.observableArrayList();
		Set<Entry<String, Double>> expensesSet = incomePerCategory.entrySet();
	    Iterator<Entry<String, Double>> i = expensesSet.iterator();
	    while(i.hasNext()) {
	    		@SuppressWarnings("rawtypes")
			Map.Entry categoryIncome = (Map.Entry)i.next();
	    		incomeChartData.add(new PieChart.Data(categoryIncome.getKey().toString(), Double.parseDouble(categoryIncome.getValue().toString())));
	    	}
		incomeChart.setData(incomeChartData);
	}
	
	/**
     * I18n bundle parameter sent by the main application controller
     * @param bundle
     */
    public void setBundle(ResourceBundle bundle) {
    		this.bundle = bundle; 
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp The application main controller
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
