package net.budgey.app.views;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.budgey.app.Config;
import net.budgey.app.MainApp;
import javafx.scene.control.Alert.AlertType;

public class SettingsController {
	/**
	 * JavaFX Combobox listing all the world money currencies
	 */
	@FXML
	private ComboBox<String> currency;
	
	/**
	 * JavaFX Combobox listing the available application languages 
	 */
	@FXML
	private ComboBox<String> language;
	
	/**
     * Main controller
     * @see MainApp
     */
    private MainApp mainApp;
	
	/**
     * Bundle for the i18n system
     */
    private ResourceBundle bundle;
    
    /**
	 * Loads data in layout page
	 * Method first called from outside: MainApp class
	 * Also called everytime there is an update to the data (edit, delete, add)
	 * @see MainApp#showSettings()
	 */
    public void load() {
    		//Add options to comboboxes
		language.getItems().addAll(bundle.getString("french"), bundle.getString("english"));
		currency.getItems().addAll("$", "€");
    }
    
    /**
     * Updates user settings
     * @see Config#updateConfigFile() 
     */
    public void updateSettings() {
    		try {
	    		//If user submitted configuration settings without choosing any option
	    		if(language.getValue() == null && currency.getValue() == null)
	    			throw new Exception("invalidConfigurationOptions");
	    		
	    		//Store settings
	    		JSONObject data = new JSONObject();
	    		if(language.getValue() == bundle.getString("french"))
	    			data.put("lang", "fr");
	    		else if(language.getValue() == bundle.getString("english"))
	    			data.put("lang", "en");
	    		else
	    			data.put("lang", Config.parseConfigFile().get("lang")); //If not specified, leave it as it is
	    		if(currency.getValue() != null)
	    			data.put("currency", currency.getValue());
	    		else
	    			data.put("currency", Config.parseConfigFile().get("currency")); //If not specified, leave it as it is
	    		
	    		Config.updateConfigFile(data);
	    		
	    		//Success message
	    		Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(bundle.getString("success"));
			alert.setHeaderText(bundle.getString("settingsUpdated"));
			alert.setContentText(bundle.getString("settingsUpdatedSuccessfully"));
			alert.show();
    		} catch(Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(bundle.getString("error.dialog.title"));
			alert.setHeaderText(bundle.getString("error.dialog.header"));
			alert.setContentText(bundle.getString("error.dialog.content." + e.getMessage()));
			alert.show();
    		}
    }
    
    /**
     * Allows user to import JSON data file
     * @throws Exception if file could not be copied or if file provided is invalid
     */
    public void importDataFile() throws Exception {
    		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(bundle.getString("chooseFile"));
		File dataFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		//Check if file provided is a JSON file named data
		if(!dataFile.getName().equals("data.json"))
			throw new Exception("invalidDataFile");
		
		Files.copy(dataFile.toPath(), Paths.get("resources/data.json"), StandardCopyOption.REPLACE_EXISTING);
		
		//Success message
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(bundle.getString("success"));
		alert.setHeaderText(bundle.getString("fileImported"));
		alert.setContentText(bundle.getString("dataFileHasBeenImported"));
		alert.show();
    }
    
    /**
     * Allows user to clear JSON data file
     * @throws IOException if JSON file could not be updated
     */
    public void clearDataFile() throws IOException {
    		//Show a confirmation message
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.initOwner(mainApp.getPrimaryStage());
		confirmation.setTitle(bundle.getString("confirmation.dialog.title"));
		confirmation.setHeaderText(bundle.getString("confirmation.dialog.header.cleaDataFile"));
		confirmation.setContentText(bundle.getString("confirmation.dialog.text.cleaDataFile"));
		Optional<ButtonType> result = confirmation.showAndWait();
		
		//Delete the discussion if the user confirmed
		if (result.get() == ButtonType.OK) {
			//Update JSON file with empty JSON object
			JSONObject data = new JSONObject();
			data.put("operations", new JSONArray());
			data.put("bankAccounts", new JSONArray());
			data.put("categories", new JSONArray());
			Config.updateDataFile(data);
			
			//Success message
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(bundle.getString("success"));
			alert.setHeaderText(bundle.getString("fileCleared"));
			alert.setContentText(bundle.getString("dataFileHasBeenCleared"));
			alert.show();
		}
    }
    
    /**
     * Allows user to import JSON data file
     * @throws Exception if file could not be copied
     */
    public void exportDateFile() throws IOException {
    		DirectoryChooser directoryChooser = new DirectoryChooser();
    		directoryChooser.setTitle(bundle.getString("chooseDirectory"));
    		File selectedDirectory = directoryChooser.showDialog(mainApp.getPrimaryStage());
    		
    		Files.copy(Paths.get("resources/data.json"), Paths.get(selectedDirectory.toPath().toString() + "/data.json"), StandardCopyOption.REPLACE_EXISTING);
    		
    		//Success message
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.initOwner(mainApp.getPrimaryStage());
    		alert.setTitle(bundle.getString("success"));
    		alert.setHeaderText(bundle.getString("fileExported"));
    		alert.setContentText(bundle.getString("dataFileHasBeenExported"));
    		alert.show();
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp The application main controller
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    /**
     * I18n bundle parameter sent by the main application controller
     * @param bundle
     */
    public void setBundle(ResourceBundle bundle) {
    		this.bundle = bundle; 
    }
}
