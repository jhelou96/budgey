package net.budgey.app.views;

import java.awt.Desktop;
import java.net.URI;

import net.budgey.app.MainApp;

public class RootLayoutController {
    /**
     * Main controller
     * @see MainApp
     */
    private MainApp mainApp;
	
    /**
     * Switches user to the Dashboard.fxml layout
     * @throws Exception if FXML file not found or if JSON data cannot be loaded 
     * @see MainApp#showDashboard()
     */
	public void showDashboard() throws Exception {
		mainApp.showDashboard();
	}
	
	/**
	 * Switches user to the BankAccounts.fxml layout
	 * @throws Exception if FXML file not found or if JSON data cannot be loaded
	 * @see MainApp#showBankAccounts()
	 */
	public void showBankAccounts() throws Exception {
		mainApp.showBankAccounts();
	}
	
	/**
	 * Switches user to the Budget.fxml layout
	 * @throws Exception if FXML file not found or if JSON data cannot be loaded
	 * @see MainApp#showBudget()
	 */
	public void showBudget() throws Exception {
		mainApp.showBudget();
	}
	
	/**
	 * Switches user to the Settings.fxml layout
	 * @throws Exception if FXML file not found or if JSON data cannot be loaded
	 * @see MainApp#showBudget()
	 */
	public void showSettings() throws Exception {
		mainApp.showSettings();
	}
    
    /**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp The application main controller
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    /**
     * Opens author website when copyright link is clicked
     * @throws Exception if link could not be opened
     */
    public void linkToAuthorWebsite() throws Exception {
	    	if(Desktop.isDesktopSupported())
            Desktop.getDesktop().browse(new URI("http://www.joeyhelou.com"));
    }
    
    /**
     * Opens product website when image link is clicked
     * @throws Exception if link could not be opened
     */
    public void linkToProductWebsite() throws Exception {
	    	if(Desktop.isDesktopSupported())
            Desktop.getDesktop().browse(new URI("http://www.budgey.net"));
    }
}
