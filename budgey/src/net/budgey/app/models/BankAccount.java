package net.budgey.app.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bank account entity as it saved in the JSON file
 * Adapted for a tableView display
 *
 */
public class BankAccount {
	/**
	 * Bank account id
	 */
	private final IntegerProperty id;
	
	/**
	 * Bank name
	 */
	private final StringProperty bankName;
	
	/**
	 * Account owner name
	 */
	private final StringProperty accountOwner;
	
	/**
	 * Account description
	 */
	private final StringProperty description;
	
	/**
	 * Bank account constructor
	 */
	public BankAccount() {
		id = new SimpleIntegerProperty();
		bankName = new SimpleStringProperty();
		accountOwner = new SimpleStringProperty();
		description = new SimpleStringProperty();
	}
	
	/**
	 * Returns bank account ID
	 * @return bank account ID
	 */
	public int getId() {
		return id.get();
	}
	
	/**
	 * Updates bank account ID
	 * @param id The new ID
	 */
	public void setId(int id) {
		this.id.set(id);
	}
	
	/**
	 * Returns bank account ID as property
	 * @return bank account ID property
	 */
	public IntegerProperty idProperty() {
		return id;
	}
	
	/**
	 * Returns bank name
	 * @return bank name
	 */
	public String getBankName() {
		return bankName.get();
	}
	
	/**
	 * Updates bank name
	 * @param bankName The new bank name
	 * @throws Exception if bank name is empty
	 */
	public void setBankName(String bankName) throws Exception {
		if(!bankName.isEmpty())
			this.bankName.set(bankName);
		else
			throw new Exception("bankNameCannotBeEmpty");
	}
	
	/**
	 * Returns bank name as property
	 * @return bank name property
	 */
	public StringProperty bankNameProperty() {
		return bankName;
	}
	
	/**
	 * Returns account owner name
	 * @return account owner name
	 */
	public String getAccountOwner() {
		return accountOwner.get();
	}
	
	/**
	 * Updates account owner name
	 * @param accountOwner The new account owner
	 * @throws Exception if account owner is empty
	 */
	public void setAccountOwner(String accountOwner) throws Exception {
		if(!accountOwner.isEmpty())
			this.accountOwner.set(accountOwner);
		else
			throw new Exception("accountOwnerCannotBeEmpty");
	}	
	
	/**
	 * Returns account owner as property
	 * @return account owner property
	 */
	public StringProperty accountOwnerProperty() {
		return accountOwner;
	}
	
	/**
	 * Returns bank account description
	 * @return bank account description
	 */
	public String getDescription() {
		return description.get();
	}
	
	/**
	 * Updates bank account description
	 * @param description The new description
	 * @throws Exception if description is empty
	 */
	public void setDescription(String description) throws Exception {
		if(!description.isEmpty())
			this.description.set(description);
		else
			throw new Exception("descriptionCannotBeEmpty");
	}
	
	/**
	 * Returns bank account description as property
	 * @return bank account description property
	 */
	public StringProperty descriptionProperty() {
		return description;
	}
}
