package net.budgey.app.views;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

public class BudgetDetailsController {
	/**
	 * JavaFX list containing the list of operations
	 */
	@FXML
    private ListView<Operation> operationsList;
	
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
	 * JavaFX label showing the amount of a specific operation
	 */
	@FXML
	private Label operationAmountLabel;
	
	/**
	 * JavaFX label showing the type of a specific operation
	 */
	@FXML
	private Label operationTypeLabel;
	
	/**
	 * JavaFX label showing the category of a specific operation
	 */
	@FXML
	private Label operationCategoryLabel;
	
	/**
	 * JavaFX label showing the bank name of a specific operation
	 */
	@FXML
	private Label operationBankLabel;
	
	/**
	 * JavaFX label showing the bank account owner of a specific operation
	 */
	@FXML
	private Label operationBankAccountOwnerLabel;
	
	/**
	 * JavaFX label showing the date of a specific operation
	 */
	@FXML
	private Label operationDateLabel;
	
	/**
	 * JavaFX label showing the description of a specific operation
	 */
	@FXML
	private Label operationDescriptionLabel;
	
	/**
	 * JavaFX label showing the bank account description of a specific operation
	 */
	@FXML
	private Label operationBankAccountDescriptionLabel;
	
	/**
	 * JavaFX label showing the number of operations
	 */
	@FXML
	private Label summaryNbOperationsLabel;
	
	/**
	 * JavaFX label showing the operations total amount of incomes
	 */
	@FXML
	private Label summaryIncomesLabel;
	
	/**
	 * JavaFX label showing the operations total amount of expenses
	 */
	@FXML
	private Label summaryExpensesLabel;
	
	/**
	 * JavaFX label showing the operations total amount of incomes - expenses
	 */
	@FXML
	private Label summaryTotalAmountLabel;
	
	/**
	 * JavaFX datePicker allowing user to choose a starting date for list of operations
	 */
	@FXML
	private DatePicker startingDate;
	
	/**
	 * JavaFX datePicker allowing user to choose an ending date for list of operations
	 */
	@FXML
	private DatePicker endingDate;
	
	/**
	 * JavaFX lineChart that draws a line for the expenses and a line for the income within the date range specified by the user
	 */
	@FXML
	private LineChart<Number, Number> operationsChart;
	
	/**
	 * JavaFX X axis for the lineChart
	 */
	@FXML
	private NumberAxis operationsXAxis;
	
	/**
	 * JavaFX Y axis for the lineChart
	 */
	@FXML
	private NumberAxis operationsYAxis;
	
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
	public BudgetDetailsController() throws FileNotFoundException {
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
		//If no row is selected, delete and edit buttons are disabled
		deleteOperationButton.disableProperty().bind(Bindings.isEmpty(operationsList.getSelectionModel().getSelectedItems()));
		editOperationButton.disableProperty().bind(Bindings.isEmpty(operationsList.getSelectionModel().getSelectedItems()));
	}
	
	/**
	 * Shows list of operations within a specific date range
	 * Method executed when user clicks on submit button
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void showOperations() {
		try {
			//Check if date range has been provided
			if(startingDate.getValue() == null || endingDate.getValue() == null)
				throw new Exception("invalidDateRange");
			
			//Check if date range provided is valid
			if(startingDate.getValue().isAfter(endingDate.getValue()))
				throw new Exception("invalidDateRange");
			
			//Default text if no items in operations list
			operationsList.setPlaceholder(new Label(bundle.getString("noOperationsForSpecifiedDateRange")));
			
			ArrayList<Operation> operations = operationDAO.getOperations(startingDate.getValue().toString(), endingDate.getValue().toString());
			
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
			
			//Summary of operations
			int nbOperations = operations.size();
			double operationsIncomes = 0;
			for(int i = 0; i < operations.size(); i++) {
				if(operations.get(i).getOperationType() == 1)
					operationsIncomes += operations.get(i).getAmount();
			}
			double operationsExpenses = 0;
			for(int i = 0; i < operations.size(); i++) {
				if(operations.get(i).getOperationType() == 2)
					operationsExpenses += operations.get(i).getAmount();
			}
			double operationsTotal = operationsIncomes - operationsExpenses;
			
			DecimalFormat decimalFormat = new DecimalFormat("##.00"); //Convert number to 2 decimal digits
			
			summaryNbOperationsLabel.setText(nbOperations + "");
			summaryIncomesLabel.setText(decimalFormat.format(operationsIncomes) + mainApp.getCurrency());
			summaryExpensesLabel.setText(decimalFormat.format(operationsExpenses) + mainApp.getCurrency());
			summaryTotalAmountLabel.setText(((operationsTotal > 0) ? "+" : "") + decimalFormat.format(operationsTotal) + mainApp.getCurrency());
			
			/*
			 * Chart of operations
			 * X-axis of chart corresponds to operations date timestamp
			 * Y-axis of chart corresponds to the sum of amounts of the operations of same type and same timestamp
			 */
			operationsChart.getData().clear(); // First clear the operations chart from all previous computations
			
			//TreeMap with timestamp as key and sum of the expenses amount of operations having the same timestamp as value
			TreeMap<Long, Double> expensesByDay = new TreeMap(); //TreeMap is used to automatically sort the keys (dates)
			for(int i = 0; i < operations.size(); i++) {
				//If operation is an income, ignore it
				if(operations.get(i).getOperationType() == 1)
					continue; 
				
				//Convert date to timestamp
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(operations.get(i).getDate());
				//If key already exists, add to its value the operation amount
				if(expensesByDay.containsKey(date.getTime()))
					expensesByDay.put((long) date.getTime(), expensesByDay.get(date.getTime()) + operations.get(i).getAmount());
				else //Otherwise, create new entry with operation timestamp as key
					expensesByDay.put((long) date.getTime(), operations.get(i).getAmount());
			}
			//Populate expenses series
			XYChart.Series expensesSeries = new XYChart.Series();
			expensesSeries.setName(bundle.getString("expenses"));
			Set expensesSet = expensesByDay.entrySet();
		    Iterator i = expensesSet.iterator();
		    while(i.hasNext()) {
		    		Map.Entry expenses = (Map.Entry)i.next();
		    		expensesSeries.getData().add(new XYChart.Data(expenses.getKey(), expenses.getValue()));
		    	}
			operationsChart.getData().add(expensesSeries);
			
			//TreeMap with timestamp as key and sum of the income amount of operations having the same timestamp as value
			TreeMap<Long, Double> incomeByDay = new TreeMap(); //TreeMap is used to automatically sort the keys (dates)
			for(int j = 0; j < operations.size(); j++) {
				//If operation is an expenses, ignore it
				if(operations.get(j).getOperationType() == 2)
					continue; 
				
				//Convert date to timestamp
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(operations.get(j).getDate());
				//If key already exists, add to its value the operation amount
				if(incomeByDay.containsKey(date.getTime()))
					incomeByDay.put((long) date.getTime(), incomeByDay.get(date.getTime()) + operations.get(j).getAmount());
				else //Otherwise, create new entry with operation timestamp as key
					incomeByDay.put((long) date.getTime(), operations.get(j).getAmount());
			}
			//Populate expenses series
			XYChart.Series incomeSeries = new XYChart.Series();
			incomeSeries.setName(bundle.getString("income"));
			Set incomeSet = incomeByDay.entrySet();
		    Iterator j = incomeSet.iterator();
		    while(j.hasNext()) {
		    		Map.Entry income = (Map.Entry)j.next();
		    		incomeSeries.getData().add(new XYChart.Data(income.getKey(), income.getValue()));
		    	}
			operationsChart.getData().add(incomeSeries);
			
			//Format timestamp labels to date labels in X-Axis
			operationsXAxis.setTickLabelFormatter(new StringConverter<Number>() {
	            @Override
	            public String toString(Number number) {
	            		Timestamp timestamp = new Timestamp(number.longValue());
	            		Date date = new Date(timestamp.getTime());
	            		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	            		return dateFormat.format(date);
	            }

	            @Override
	            public Number fromString(String string) {
					try {
						Date date = new SimpleDateFormat("dd/MM/yyyy").parse(string);
						return date.getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return null;  
	            }
	        });
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(bundle.getString("error.dialog.title"));
			alert.setHeaderText(bundle.getString("error.dialog.header"));
			alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
			alert.show();
		}
	}
	
	/**
	 * Shows the full details of a specific operation
	 * Method executed when user clicks on JavaFX listView
	 * @throws Exception if BankAccountDAO throws an exception
	 */
	public void showOperationDetail() throws Exception {
		//Check if operation  has been selected in the list
		if(!operationsList.getSelectionModel().isEmpty()) {
			Operation operation = operationsList.getSelectionModel().getSelectedItem();
			
			//Get category info
			Category category = new Category();
			category.setId(operation.getIdCategory());
			category = categoryDAO.get(category);
			
			//Get bank account info
			BankAccount bankAccount = new BankAccount();
			bankAccount.setId(operation.getIdBankAccount());
			bankAccount = bankAccountDAO.get(bankAccount);
			
			//Get operation date
			SimpleDateFormat convertor = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
			Date date = convertor.parse(operation.getDate());
			
			//Display operation information
			operationAmountLabel.setText(operation.getAmount() + mainApp.getCurrency());
			if(operation.getOperationType() == 1) //If operation represents an income
				operationTypeLabel.setText(bundle.getString("income"));
			else
				operationTypeLabel.setText(bundle.getString("expenses"));
			operationCategoryLabel.setText(category.getName());
			operationBankLabel.setText(bankAccount.getBankName());
			operationBankAccountOwnerLabel.setText(bankAccount.getAccountOwner());
			operationDateLabel.setText(formatter.format(date));
			operationDescriptionLabel.setText(operation.getDescription());
			operationBankAccountDescriptionLabel.setText(bankAccount.getDescription());
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
    				
    				//Refresh list and update details
    				showOperations();
    				operationAmountLabel.setText(amount.getText() + mainApp.getCurrency());
				operationTypeLabel.setText(operationType.getValue());
    				operationCategoryLabel.setText(category.getValue().getName());
    				operationBankLabel.setText(bankAccount.getValue().getBankName());
    				operationBankAccountOwnerLabel.setText(bankAccount.getValue().getAccountOwner());
    				operationDescriptionLabel.setText(description.getText());
    				operationBankAccountDescriptionLabel.setText(bankAccount.getValue().getDescription());
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
			
			//Refresh list and update details
			showOperations();
			operationAmountLabel.setText("-");
			operationTypeLabel.setText("-");
			operationCategoryLabel.setText("-");
			operationBankLabel.setText("-");
			operationBankAccountOwnerLabel.setText("-");
			operationDescriptionLabel.setText("-");
			operationBankAccountDescriptionLabel.setText("-");
			operationDateLabel.setText("-");
		}
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
