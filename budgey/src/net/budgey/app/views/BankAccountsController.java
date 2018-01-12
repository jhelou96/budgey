package net.budgey.app.views;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import net.budgey.app.MainApp;
import net.budgey.app.models.BankAccount;
import net.budgey.app.models.BankAccountDAO;
import net.budgey.app.models.Operation;
import net.budgey.app.models.OperationDAO;

public class BankAccountsController {
	/**
	 * JavaFX list containing the list of operations
	 */
	@FXML
    private ListView<Operation> operationsList;
	
	/**
	 * JavaFX table containing the list of bank accounts
	 */
	@FXML
    private TableView<BankAccount> bankAccountsTable;
	
	/**
	 * JavaFX table column listing the bank accounts ID
	 */
	@FXML
	private TableColumn<BankAccount, Number> idColumn;
	
	/**
	 * JavaFX table column listing the bank accounts owner
	 */
	@FXML
	private TableColumn<BankAccount, String> ownerColumn;
	
	/**
	 * JavaFX table column listing the bank accounts bank name
	 */
	@FXML
	private TableColumn<BankAccount, String> bankColumn;
	
	/**
	 * JavaFX table column listing the bank accounts description
	 */
	@FXML
	private TableColumn<BankAccount, String> descriptionColumn;
	
	/**
	 * JavaFX button allowing the user to remove a bank account
	 */
	@FXML
	private Button deleteButton;
	
	/**
	 * JavaFX button allowing the user to edit a bank account
	 */
	@FXML 
	private Button editButton;
	
	/**
	 * Table data containing the bank accounts information
	 */
	private ObservableList<BankAccount> bankAccountsData = FXCollections.observableArrayList();
	
	/**
	 * BankAccount DB manager
	 */
	private BankAccountDAO bankAccountDAO;
	
	/**
	 * Operation DB manager
	 */
	private OperationDAO operationDAO;
	
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
	public BankAccountsController() throws FileNotFoundException {
		bankAccountDAO = new BankAccountDAO();
		operationDAO = new OperationDAO();
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
	@FXML
    private void initialize() {
        // Initialize the discussion table with the three columns.
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		ownerColumn.setCellValueFactory(cellData -> cellData.getValue().accountOwnerProperty());
		bankColumn.setCellValueFactory(cellData -> cellData.getValue().bankNameProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		
		//If no row is selected, delete and edit button are disabled
		deleteButton.disableProperty().bind(Bindings.isEmpty(bankAccountsTable.getSelectionModel().getSelectedItems()));
		editButton.disableProperty().bind(Bindings.isEmpty(bankAccountsTable.getSelectionModel().getSelectedItems()));
	}
	
	/**
	 * Allows the user to add a new bank account
	 * @see BankAccountDAO#add()
	 */
	public void newBankAccount() {
		//New dialog
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("newAccount"));
		dialog.setHeaderText(bundle.getString("newBankAccount"));
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
		
		//Account owner field
		TextField accountOwner = new TextField();
		accountOwner.setPromptText(bundle.getString("accountOwner"));
		form.getChildren().add(accountOwner);
		
		//Bank name field
		TextField bankName = new TextField();
		bankName.setPromptText(bundle.getString("bankName"));
		form.getChildren().add(bankName);
		
		//Description field
		TextField description = new TextField();
		description.setPromptText(bundle.getString("description"));
		form.getChildren().add(description);
		
		dialog.getDialogPane().setContent(form);
		
		//Dialog validation
		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
        		//Try to save the account
			try {
				BankAccount bankAccount = new BankAccount();
				bankAccount.setAccountOwner(accountOwner.getText());
				bankAccount.setBankName(bankName.getText());
				bankAccount.setDescription(description.getText());
				
				bankAccountDAO.add(bankAccount);
				
				//Redirect user to the bankAccounts view layout
				mainApp.showBankAccounts();
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
	 * Allows the user to edit a bank account information
	 * @see BankAccountDAO#update()
	 */
	public void editBankAccount() {
		//New dialog
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("editBankAccount"));
		dialog.setHeaderText(bundle.getString("editBankAccountInfo"));
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
		
		//Account owner field
		TextField accountOwner = new TextField();
		accountOwner.setText(bankAccountsTable.getSelectionModel().getSelectedItem().getAccountOwner() + "");
		form.getChildren().add(accountOwner);
		
		//Bank name field
		TextField bankName = new TextField();
		bankName.setText(bankAccountsTable.getSelectionModel().getSelectedItem().getBankName() + "");
		form.getChildren().add(bankName);
		
		//Description field
		TextField description = new TextField();
		description.setText(bankAccountsTable.getSelectionModel().getSelectedItem().getDescription() + "");
		form.getChildren().add(description);
		
		dialog.getDialogPane().setContent(form);
		
		//Dialog validation
		final Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
        		//Try to save the account
			try {
				BankAccount bankAccount = bankAccountsTable.getSelectionModel().getSelectedItem();
				bankAccount.setAccountOwner(accountOwner.getText());
				bankAccount.setBankName(bankName.getText());
				bankAccount.setDescription(description.getText());
				
				bankAccountDAO.update(bankAccount);
				
				//Redirect user to the bankAccounts view layout
				mainApp.showBankAccounts();
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
	 * Allows the user to remove a bank account and all the operations related to it
	 * @throws Exception if bank account could not be removed
	 * @see BankAccountDAO#remove()
	 * @see OperationDAO#remove()
	 */
	public void removeBankAccount() throws Exception {
		//Show a confirmation message
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(bundle.getString("confirmation.dialog.title"));
		alert.setHeaderText(bundle.getString("confirmation.dialog.header.removeBankAccount"));
		alert.setContentText(bundle.getString("confirmation.dialog.text.removeBankAccount"));
		Optional<ButtonType> result = alert.showAndWait();
		
		//Delete the discussion if the user confirmed
		if (result.get() == ButtonType.OK) {
			BankAccount bankAccount = bankAccountsTable.getSelectionModel().getSelectedItem();
		    
			operationDAO.remove(bankAccount); //Removes all the operations of the bank account
			bankAccountDAO.remove(bankAccount);
		    
			//Redirect user to the bankAccounts view layout
			mainApp.showBankAccounts();
		}
	}
	
	/**
	 * Shows the list of operations of a specific bank account
	 * @throws Exception if operationDAO.getOperations() throws an exception
	 * @see OperationDAO#getOperations()
	 */
	public void showActivity() throws Exception {
		//Default text if no items in operations list
		operationsList.setPlaceholder(new Label(bundle.getString("noOperationsYet")));
		
		//Get category list of operations
		BankAccount bankAccount = (BankAccount) bankAccountsTable.getSelectionModel().getSelectedItem();
		if(bankAccount != null) {
			ArrayList<Operation> operations = operationDAO.getOperations(bankAccount);
			
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
	 * Populates the JavaFX table with the list of bank accounts
	 * @throws Exception if JSON file not found
	 * @see BankAccountDAO#getBankAccounts()
	 */
	public void populateBankAccountsTable() throws Exception {
		//Get list of discussions of the member
		ArrayList<BankAccount> bankAccounts = bankAccountDAO.getBankAccounts();
	
		//Add the list of discussions to the table
		for(int i = 0; i < bankAccounts.size(); i++) {
			//We populate the rows
			bankAccountsData.add((BankAccount) bankAccounts.get(i));
		}
		
		//Add observable list data to the table
	    bankAccountsTable.setItems(bankAccountsData);
	    
	    //Text to show if no discussions found
	    bankAccountsTable.setPlaceholder(new Label(bundle.getString("noBankAccountFound")));
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
