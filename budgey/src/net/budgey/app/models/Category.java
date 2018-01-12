package net.budgey.app.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Category entity as it is saved in the JSON file
 * Adapted for a tableView display
 *
 */
public class Category {
	/**
	 * Category ID
	 */
	private final IntegerProperty id;
	
	/**
	 * Category name
	 */
	private final StringProperty name;
	
	/**
	 * Category description
	 */
	private final StringProperty description;
	
	/**
	 * Category constructor
	 */
	public Category() {
		id = new SimpleIntegerProperty();
		name = new SimpleStringProperty();
		description = new SimpleStringProperty();
	}
	
	/**
	 * Returns category ID
	 * @return category ID
	 */
	public int getId() {
		return id.get();
	}
	
	/**
	 * Updates category ID
	 * @param id The new ID
	 */
	public void setId(int id) {
		this.id.set(id);
	}
	
	/**
	 * Returns category ID as property
	 * @return category ID property
	 */
	public IntegerProperty idProperty() {
		return id;
	}
	
	/**
	 * Returns category name
	 * @return category name
	 */
	public String getName() {
		return name.get();
	}
	
	/**
	 * Updates category name
	 * @param name The new name
	 * @throws Exception if category name is empty
	 */
	public void setName(String name) throws Exception {
		if(name.isEmpty())
			throw new Exception("categoryNameCannotBeEmpty");
		
		this.name.set(name);
	}
	
	/**
	 * Returns category name as property
	 * @return category name property
	 */
	public StringProperty nameProperty() {
		return name;
	}
	
	/**
	 * Returns category description
	 * @return category description
	 */
	public String getDescription() {
		return description.get();
	}

	/**
	 * Updates category description
	 * @param description The new description
	 * @throws Exception if description is empty
	 */
	public void setDescription(String description) throws Exception {
		if(description.isEmpty())
			throw new Exception("categoryDescriptionCannotBeEmpty");
		this.description.set(description);
	}
	
	/**
	 * Returns category description as property
	 * @return category description property
	 */
	public StringProperty descriptionProperty() {
		return description;
	}
}
