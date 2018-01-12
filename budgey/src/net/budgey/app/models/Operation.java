package net.budgey.app.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Category entity as it is saved in the JSON file
 * Adapted for a listView display
 *
 */
public class Operation {
	/**
	 * Operation ID
	 */
	private final IntegerProperty id;
	
	/**
	 * Operation ID bank account
	 */
	private final IntegerProperty idBankAccount;
	
	/**
	 * Operation ID bank category
	 */
	private final IntegerProperty idCategory;
	
	/**
	 * Operation type
	 * 1 = Income
	 * 2 = Expenses
	 */
	private final IntegerProperty operationType;
	
	/**
	 * Operation amount
	 */
	private final DoubleProperty amount;
	
	/**
	 * Operation description
	 */
	private final StringProperty description;
	
	/**
	 * Operation date
	 */
	private final StringProperty date;
	
	/**
	 * Operation constructor
	 */
	public Operation() {
		id = new SimpleIntegerProperty();
		idBankAccount = new SimpleIntegerProperty();
		idCategory = new SimpleIntegerProperty();
		operationType = new SimpleIntegerProperty();
		amount = new SimpleDoubleProperty();
		description = new SimpleStringProperty();
		date = new SimpleStringProperty();
	}

	/**
	 * Returns operation ID
	 * @return operation ID
	 */
	public int getId() {
		return id.get();
	}
	
	/**
	 * Updates operation ID
	 * @param id The new ID
	 */
	public void setId(int id) {
		this.id.set(id);
	}
	
	/**
	 * Returns operation ID as property
	 * @return operation ID property
	 */
	public IntegerProperty idProperty() {
		return id;
	}
	
	/**
	 * Returns operation ID bank account
	 * @return operation bank account ID
	 */
	public int getIdBankAccount() {
		return idBankAccount.get();
	}
	
	/**
	 * Updates operation bank account ID
	 * @param idBankAccount The new operation bank account ID
	 */
	public void setIdBankAccount(int idBankAccount) {
		this.idBankAccount.set(idBankAccount);
	}
	
	/**
	 * Returns operation bank account ID as property
	 * @return operation bank account ID property
	 */
	public IntegerProperty idBankAccountProperty() {
		return idBankAccount;
	}
	
	/**
	 * Returns operation category ID
	 * @return operation category ID
	 */
	public int getIdCategory() {
		return idCategory.get();
	}
	
	/**
	 * Updates operation category ID
	 * @param idCategory The new operation category ID
	 */
	public void setIdCategory(int idCategory) {
		this.idCategory.set(idCategory);
	}
	
	/**
	 * Returns operation category ID property
	 * @return operation category ID property
	 */
	public IntegerProperty idCategoryProperty() {
		return idCategory;
	}
	
	/**
	 * Returns operation type
	 * @return operation type
	 */
	public int getOperationType() {
		return operationType.get();
	}
	
	/**
	 * Updates operation type 
	 * @param operationType The new operation type
	 */
	public void setOperationType(int operationType) {
		this.operationType.set(operationType);
	}
	
	/**
	 * Returns operation type property
	 * @return operation type property
	 */
	public IntegerProperty operationTypeProperty() {
		return operationType;
	}
	
	/**
	 * Returns operation amount
	 * @return operation amount
	 */
	public double getAmount() {
		return amount.get();
	}
	
	/**
	 * Updates operation amount
	 * @param amount The new amount
	 */
	public void setAmount(double amount) {
		this.amount.set(amount);
	}
	
	/**
	 * Updates operation amount property
	 * @return operation amount property
	 */
	public DoubleProperty amountProperty() {
		return amount;
	}

	/**
	 * Returns operation description
	 * @return operation description
	 */
	public String getDescription() {
		return description.get();
	}
	
	/**
	 * Updates operation description
	 * @param description The new description
	 * @throws Exception if description is empty
	 */
	public void setDescription(String description) throws Exception {
		if(!description.isEmpty())
			this.description.set(description);
		else
			throw new Exception("operationDescriptionCannotBeEmpty");
	}
	
	/**
	 * Returns operation description property
	 * @return operation description property
	 */
	public StringProperty descriptionProperty() {
		return description;
	}
	
	/**
	 * Returns operation date
	 * @return operation date
	 */
	public String getDate() {
		return date.get();
	}
	
	/**
	 * Updates operation date
	 * @param date The new date
	 */
	public void setDate(String date) {
		this.date.set(date);
	}
	
	/**
	 * Returns operation date property
	 * @return operation date property
	 */
	public StringProperty dateProperty() {
		return date;
	}
}
