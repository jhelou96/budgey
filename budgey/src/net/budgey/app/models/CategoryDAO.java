package net.budgey.app.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import net.budgey.app.Config;

/**
 * Data manager for the category entity
 */
public class CategoryDAO {
	
	/**
	 * Retrieves the list of categories
	 * @return arraylist of categories
	 * @throws Exception if Category entity throws exception or if JSON file not found
	 */
	public ArrayList<Category> getCategories() throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray categories = data.getJSONArray("categories");

		//Save the retrieved categories in an array list of categories objects
		ArrayList<Category> listCategories = new ArrayList<>();
		for(int i = 0; i < categories.length(); i++) {
			Category category = new Category();
			category.setId(categories.getJSONObject(i).getInt("id"));
			category.setName(categories.getJSONObject(i).getString("name"));
			category.setDescription(categories.getJSONObject(i).getString("description"));
			
			listCategories.add(category);
		}
		
		return listCategories;
	}
	
	/**
	 * Retrieves the data of a category based on its id
	 * @param category Category to be retrieved
	 * @return Category retrieved
	 * @throws Exception if Category entity throws exception or if JSON file not found
	 */
	public Category get(Category category) throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray categories = data.getJSONArray("categories");
		for(int i = 0; i < categories.length(); i++) {
			if(category.getId() == categories.getJSONObject(i).getInt("id")) {
				category.setId(categories.getJSONObject(i).getInt("id"));
				category.setName(categories.getJSONObject(i).getString("name"));
				category.setDescription(categories.getJSONObject(i).getString("description"));
				return category;
			}	
		}
		
		return null;
	}
	
	/**
	 * Saves a new category in the JSON file
	 * @param category Category to be saved
	 * @throws IOException if JSON file not found
	 */
	public void add(Category category) throws IOException {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray categories = data.getJSONArray("categories");
		
		//Create a new JSON object representing the new category to be saved
		JSONObject newCategory = new JSONObject();
		
		//New category ID is computed by retrieving and incrementing the ID of the last category stored
		int idCategory = 1;
		if(categories.length() > 0)
			idCategory = categories.getJSONObject(categories.length()-1).getInt("id") + 1;
		
		newCategory.put("id", idCategory);
		newCategory.put("name", category.getName());
		newCategory.put("description", category.getDescription());
		categories.put(newCategory);
		
		//Update categories JSON array and save the data
		data.put("categories", categories);
		Config.updateDataFile(data);
	}
	
	/**
	 * Updates a category information in the JSON file
	 * @param category Category to be updated
	 * @throws IOException if JSON file not found
	 */
	public void update(Category category) throws IOException {
		/*
		 * First, find the index of the category to be updated.
		 * This is done by comparing the category ID with the ID of each entry from the
		 * JSON array of categories.
		 */
		int index = 0;
		JSONObject data = Config.parseDataFile();
		JSONArray categories = data.getJSONArray("categories");
		for(int i = 0; i < categories.length(); i++) {
			//Check if IDs are equal, save index and exit loop if they are
			if(categories.getJSONObject(i).getInt("id") == category.getId()) {
				index = i;
				break;
			}
		}
		
		//Create a new JSON object representing the category with the updated information
		JSONObject newCategory = new JSONObject();
		newCategory.put("id", category.getId());
		newCategory.put("name", category.getName());
		newCategory.put("description", category.getDescription());
		
		//Replace old category by the new one at the retrieved index
		categories.put(index, newCategory);
		
		//Update categories JSON array and save the data
		data.put("categories", categories);
		Config.updateDataFile(data);
	}
	
	/**
	 * Removes a category from the JSON file
	 * @param category Category to be removed
	 * @throws IOException if JSON file not found
	 */
	public void remove(Category category) throws IOException {
		/*
		 * First, find the index of the category to be removed.
		 * This is done by comparing the category ID with the ID of each entry from the
		 * JSON array of categories.
		 */
		int index = 0;
		JSONObject data = Config.parseDataFile();
		JSONArray categories = data.getJSONArray("categories");
		for(int i = 0; i < categories.length(); i++) {
			//Check if IDs are equal, save index and exit loop if they are
			if(categories.getJSONObject(i).getInt("id") == category.getId()) {
				index = i;
				break;
			}
		}

		//Remove entry at retrieved index
		categories.remove(index);

		//Update categories JSON array and save the data
		data.put("categories", categories);
		Config.updateDataFile(data);
	}
}
