package net.budgey.app.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import net.budgey.app.Config;
import net.budgey.app.util.ArraySorting;

public class OperationDAO {
	/**
	 * Retrieves the list of operations of a specific category
	 * @param category Category from where operations have to be retrieved
	 * @return arraylist of operations
	 * @throws Exception if JSON file not found or if category entity raises an exception
	 */
	public ArrayList<Operation> getOperations(Category category) throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		
		//Save the retrieved operations in an array list of operations objects
		ArrayList<Operation> listOperations = new ArrayList<>();
		for(int i = 0; i < operations.length(); i++) {
			//Check if operation belongs to specified category
			if (operations.getJSONObject(i).getInt("idCategory") == category.getId()) {
				Operation operation = new Operation();
				operation.setId(operations.getJSONObject(i).getInt("id"));
				operation.setIdBankAccount(operations.getJSONObject(i).getInt("idBankAccount"));
				operation.setIdCategory(operations.getJSONObject(i).getInt("idCategory"));
				operation.setOperationType(operations.getJSONObject(i).getInt("operationType"));
				operation.setAmount(operations.getJSONObject(i).getDouble("amount"));
				operation.setDescription(operations.getJSONObject(i).getString("description"));
				operation.setDate(operations.getJSONObject(i).getString("date"));
				
				listOperations.add(operation);
			}
		}
		
		Collections.sort(listOperations, new ArraySorting()); //Sorts arraylist based on operations date
		return listOperations;
	}
	
	/**
	 * Retrieves the list of operations of a specific bank account
	 * @param bankAccount Bank account from where operations have to be retrieved
	 * @return arraylist of operations
	 * @throws Exception if JSON file not found or if category entity raises an exception
	 */
	public ArrayList<Operation> getOperations(BankAccount bankAccount) throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		
		//Save the retrieved operations in an array list of operations objects
		ArrayList<Operation> listOperations = new ArrayList<>();
		for(int i = 0; i < operations.length(); i++) {
			//Check if operation belongs to specified category
			if (operations.getJSONObject(i).getInt("idBankAccount") == bankAccount.getId()) {
				Operation operation = new Operation();
				operation.setId(operations.getJSONObject(i).getInt("id"));
				operation.setIdBankAccount(operations.getJSONObject(i).getInt("idBankAccount"));
				operation.setIdCategory(operations.getJSONObject(i).getInt("idCategory"));
				operation.setOperationType(operations.getJSONObject(i).getInt("operationType"));
				operation.setAmount(operations.getJSONObject(i).getDouble("amount"));
				operation.setDescription(operations.getJSONObject(i).getString("description"));
				operation.setDate(operations.getJSONObject(i).getString("date"));
				
				listOperations.add(operation);
			}
		}
		
		Collections.sort(listOperations, new ArraySorting()); //Sorts arraylist based on operations date
		return listOperations;
	}
	
	/**
	 * Retrieves the list of operations within a specific range date
	 * @param fromDate Starting date: yyyy-mm-dd format
	 * @param toDate Ending date: yyyy-mm-dd format
	 * @return arraylist of operations
	 * @throws Exception if JSON file not found or if category entity raises an exception
	 */
	public ArrayList<Operation> getOperations(String fromDate, String toDate) throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		
		//Save the retrieved operations in an array list of operations objects
		ArrayList<Operation> listOperations = new ArrayList<>();
		for(int i = 0; i < operations.length(); i++) {
			//Check if operation date is after fromDate and before toDate
			if(operations.getJSONObject(i).getString("date").compareTo(fromDate) >= 0 && operations.getJSONObject(i).getString("date").compareTo(toDate) <= 0) {
				Operation operation = new Operation();
				operation.setId(operations.getJSONObject(i).getInt("id"));
				operation.setIdBankAccount(operations.getJSONObject(i).getInt("idBankAccount"));
				operation.setIdCategory(operations.getJSONObject(i).getInt("idCategory"));
				operation.setOperationType(operations.getJSONObject(i).getInt("operationType"));
				operation.setAmount(operations.getJSONObject(i).getDouble("amount"));
				operation.setDescription(operations.getJSONObject(i).getString("description"));
				operation.setDate(operations.getJSONObject(i).getString("date"));
				
				listOperations.add(operation);
			}
		}

		Collections.sort(listOperations, new ArraySorting()); //Sorts arraylist based on operations date
		return listOperations;
	}
	
	/**
	 * Saves a new operation in the JSON file
	 * @param operation Operation to be saved
	 * @throws IOException if JSON file not found
	 */
	public void add(Operation operation) throws IOException {
		//Retrieve all the operations from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		
		//Create a new JSON object representing the new operation to be saved
		JSONObject newOperation = new JSONObject();
		
		//New operation ID is computed by retrieving and incrementing the ID of the last operation stored
		int idOperation = 1;
		if(operations.length() > 0)
			idOperation = operations.getJSONObject(operations.length()-1).getInt("id") + 1;
		
		newOperation.put("id", idOperation);
		newOperation.put("idBankAccount", operation.getIdBankAccount());
		newOperation.put("idCategory", operation.getIdCategory());
		newOperation.put("operationType", operation.getOperationType());
		newOperation.put("amount", operation.getAmount());
		newOperation.put("description", operation.getDescription());
		newOperation.put("date", operation.getDate());
		operations.put(newOperation);
		
		//Update categories JSON array and save the data
		data.put("operations", operations);
		Config.updateDataFile(data);
	}
	
	/**
	 * Updates an operation in the JSON file
	 * @param operation Operation to be updated
	 * @throws IOException if JSON file not found
	 */
	public void update(Operation operation) throws IOException {
		/*
		 * First, find the index of the operation to be updated.
		 * This is done by comparing the operation ID with the ID of each entry from the
		 * JSON array of operations.
		 */
		int index = 0;
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		for(int i = 0; i < operations.length(); i++) {
			//Check if IDs are equal, save index and exit loop if they are
			if(operations.getJSONObject(i).getInt("id") == operation.getId()) {
				index = i;
				break;
			}
		}
		
		//Create a new JSON object representing the operation with the updated information
		JSONObject newOperation = new JSONObject();
		newOperation.put("id", operation.getId());
		newOperation.put("amount", operation.getAmount());
		newOperation.put("idBankAccount", operation.getIdBankAccount());
		newOperation.put("idCategory", operation.getIdCategory());
		newOperation.put("description", operation.getDescription());
		newOperation.put("operationType", operation.getOperationType());
		newOperation.put("date", operation.getDate());
		
		//Replace old category by the new one at the retrieved index
		operations.put(index, newOperation);
		
		//Update categories JSON array and save the data
		data.put("operations", operations);
		Config.updateDataFile(data);
	}
	
	/**
	 * Removes a specific operation
	 * @param operation Operation to be removed
	 * @throws IOException if JSON file not found
	 */
	public void remove(Operation operation) throws IOException {
		//Check for every operations if it belongs to a specific bank account
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		for(int i = 0; i < operations.length(); i++) {
			if(operations.getJSONObject(i).getInt("id") == operation.getId()) {
				operations.remove(i);
				break;
			}
		}
		
		//Update operations JSON array and update JSON file
		data.put("operations", operations);
		Config.updateDataFile(data);
	}
	
	/**
	 * Removes all the operations related to a specific bank account
	 * @param bankAccount Bank account from where operations have to be removed
	 * @throws IOException if JSON file not found
	 */
	public void remove(BankAccount bankAccount) throws IOException {
		//Check for every operations if it belongs to a specific bank account
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		for(int i = 0; i < operations.length(); i++) {
			if(operations.getJSONObject(i).getInt("idBankAccount") == bankAccount.getId()) {
				operations.remove(i);
				i--;
			}
		}
		
		//Update operations JSON array and update JSON file
		data.put("operations", operations);
		Config.updateDataFile(data);
	}
	
	/**
	 * Removes all the operations related to a specific category
	 * @param category Category from where operations have to be removed
	 * @throws IOException if JSON file not found
	 */
	public void remove(Category category) throws IOException {
		//Check for every operations if it belongs to a specific category
		JSONObject data = Config.parseDataFile();
		JSONArray operations = data.getJSONArray("operations");
		
		for(int i = 0; i < operations.length(); i++) {
			if(operations.getJSONObject(i).getInt("idCategory") == category.getId()) {
				operations.remove(i);
				i--;
			}
		}
		
		//Update operations JSON array and update JSON file
		data.put("operations", operations);
		Config.updateDataFile(data);
	}
}
