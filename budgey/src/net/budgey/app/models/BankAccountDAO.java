package net.budgey.app.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import net.budgey.app.Config;

/**
 * Data manager for the bank account entity
 *
 */
public class BankAccountDAO {
	/**
	 * Retrieves the list of bank accounts
	 * @return arraylist of bank accounts
	 * @throws Exception if any exception is thrown by the BankAccount entity
	 */
	public ArrayList<BankAccount> getBankAccounts() throws Exception {
		//Get the bank accounts JSON array
		JSONObject data = Config.parseDataFile();
		JSONArray bankAccounts = data.getJSONArray("bankAccounts");

		//Save bank accounts in an array list of bank account objects
		ArrayList<BankAccount> listBankAccounts = new ArrayList<>();
		for(int i = 0; i < bankAccounts.length(); i++) {
			BankAccount bankAccount = new BankAccount();
			bankAccount.setId(bankAccounts.getJSONObject(i).getInt("id"));
			bankAccount.setBankName(bankAccounts.getJSONObject(i).getString("bankName"));
			bankAccount.setAccountOwner(bankAccounts.getJSONObject(i).getString("accountOwner"));
			bankAccount.setDescription(bankAccounts.getJSONObject(i).getString("description"));
			
			listBankAccounts.add(bankAccount);
		}
			
		return listBankAccounts;
	}
	
	/**
	 * Retrieves the data of a bank account based on the bank id
	 * @param bankAccount Bank account to be retrieved
	 * @return BankAccount retrieved
	 * @throws Exception if an exception is thrown by the bank account entity
	 */
	public BankAccount get(BankAccount bankAccount) throws Exception {
		//Retrieve all the categories from the JSON file
		JSONObject data = Config.parseDataFile();
		JSONArray bankAccounts = data.getJSONArray("bankAccounts");
		
		for(int i = 0; i < bankAccounts.length(); i++) {
			if(bankAccount.getId() == bankAccounts.getJSONObject(i).getInt("id")) {
				bankAccount.setId(bankAccounts.getJSONObject(i).getInt("id"));
				bankAccount.setBankName(bankAccounts.getJSONObject(i).getString("bankName"));
				bankAccount.setAccountOwner(bankAccounts.getJSONObject(i).getString("accountOwner"));
				bankAccount.setDescription(bankAccounts.getJSONObject(i).getString("description"));
				
				return bankAccount;
			}	
		}
		
		return null;
	}
	
	/**
	 * Saves a new bank account in the JSON file
	 * @param bankAccount Bank account to be saved
	 * @throws IOException if JSON file not found
	 */
	public void add(BankAccount bankAccount) throws IOException {
		//Retrieve JSON array of bank accounts
		JSONObject data = Config.parseDataFile();
		JSONArray bankAccounts = data.getJSONArray("bankAccounts");
		
		//Create a JSON object with the new bank account data
		JSONObject newAccount = new JSONObject();
		
		//New bank account ID is obtained by retrieving and incrementing the ID of the last account stored
		int idAccount = 1;
		if(bankAccounts.length() > 0) 
			idAccount = bankAccounts.getJSONObject(bankAccounts.length()-1).getInt("id") + 1;
		
		newAccount.put("id", idAccount);
		newAccount.put("bankName", bankAccount.getBankName());
		newAccount.put("accountOwner", bankAccount.getAccountOwner());
		newAccount.put("description", bankAccount.getDescription());
		bankAccounts.put(newAccount); //Add the new bank account to the JSON array of bank accounts
		
		//Update bank accounts array
		data.put("bankAccounts", bankAccounts);
		
		//Save updated data in JSON file
		Config.updateDataFile(data);
	}
	
	/**
	 * Updates a bank account information in the JSON file
	 * @param bankAccount Bank account to be updated
	 * @throws IOException if JSON file not found
	 */
	public void update(BankAccount bankAccount) throws IOException {
		/*
		 * First, find the index of the bank account to be updated.
		 * This is done by comparing the bank account ID with the ID of each entry from the
		 * JSON array of bank accounts.
		 */
		int index = 0;
		JSONObject data = Config.parseDataFile();
		JSONArray bankAccounts = data.getJSONArray("bankAccounts");
		for(int i = 0; i < bankAccounts.length(); i++) {
			//Check if IDs are equal, save index and exit loop if they are
			if(bankAccounts.getJSONObject(i).getInt("id") == bankAccount.getId()) {
				index = i;
				break;
			}
		}
		
		//Create new JSON object with the bank account information
		JSONObject newAccount = new JSONObject();
		newAccount.put("id", bankAccount.getId());
		newAccount.put("bankName", bankAccount.getBankName());
		newAccount.put("accountOwner", bankAccount.getAccountOwner());
		newAccount.put("description", bankAccount.getDescription());
		
		//Replace bank account JSON object by the new one at the retrieved index
		bankAccounts.put(index, newAccount);
		
		//Update bank accounts array and save updated data in the JSON file
		data.put("bankAccounts", bankAccounts);
		Config.updateDataFile(data);
	}
	
	/**
	 * Removes a bank account from the JSON file
	 * @param bankAccount Bank account to be removed
	 * @throws IOException if JSON file not found
	 */
	public void remove(BankAccount bankAccount) throws IOException {
		/*
		 * First, find the index of the bank account to be removed.
		 * This is done by comparing the bank account ID with the ID of each entry from the
		 * JSON array of bank accounts.
		 */
		int index = 0;
		JSONObject data = Config.parseDataFile();
		JSONArray bankAccounts = data.getJSONArray("bankAccounts");
		for(int i = 0; i < bankAccounts.length(); i++) {
			//If id of bank account is equal to the id of the bank account to be updated, save index value and exit loop
			if(bankAccounts.getJSONObject(i).getInt("id") == bankAccount.getId()) {
				index = i;
				break;
			}
		}
		
		//Remove entry at retrieved index
		bankAccounts.remove(index);
		
		//Update bank accounts JSON array and update JSON file
		data.put("bankAccounts", bankAccounts);
		Config.updateDataFile(data);
	}
}
