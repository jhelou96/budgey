package net.budgey.app.views;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import net.budgey.app.MainApp;
import net.budgey.app.models.Operation;
import net.budgey.app.models.OperationDAO;

public class DashboardController {
	/**
	 * JavaFX label showing the number of operations for the current year
	 */
	@FXML
	private Label operationsNumberForCurrentYearLabel;
	
	/**
	 * JavaFX label showing the amount of expenses for the current year
	 */
	@FXML
	private Label expensesForCurrentYearLabel;
	
	/**
	 * JavaFX label showing the amount of income for the current year
	 */
	@FXML
	private Label incomeForCurrentYearLabel;
	
	/**
	 * JavaFX label showing the operations total amount of incomes - expenses for the current year
	 */
	@FXML
	private Label operationsTotalAmountForCurrentYearLabel;
	
	/**
	 * JavaFX label showing the number of operations for the current month
	 */
	@FXML
	private Label operationsNumberForCurrentMonthLabel;
	
	/**
	 * JavaFX label showing the amount of expenses for the current month
	 */
	@FXML
	private Label expensesForCurrentMonthLabel;
	
	/**
	 * JavaFX label showing the amount of income for the current month
	 */
	@FXML
	private Label incomeForCurrentMonthLabel;
	
	/**
	 * JavaFX label showing the operations total amount of incomes - expenses for the current month
	 */
	@FXML
	private Label operationsTotalAmountForCurrentMonthLabel;
	
	/**
	 * JavaFX bar chart showing the operations per month for the current year
	 */
	@FXML
	private BarChart<String, Number> operationsChart;
	
	/**
	 * Operation DB manager
	 * @see OperationDAO
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
	public DashboardController() throws FileNotFoundException {
		operationDAO = new OperationDAO();
	}
	
	/**
	 * Loads data in layout page
	 * Method first called from outside: MainApp class
	 * Also called everytime there is an update to the data (edit, delete, add)
	 * @see MainApp#showDashboard()
	 * @throws Exception if any loaded data throws an exception
	 */
	public void load() throws Exception {
    		showCurrentYearOperationsDetails(); //Load operations details for current year
    		showCurrentMonthOperationsDetails(); //Load operations details for current month
    		populateOperationsChart(); //Populate the operations chart with data
	}
	
	/**
	 * Shows the details of the operations performed during the current year
	 * @throws Exception if OperationDAO throws exception
	 * @see operationDAO#getOperations()
	 */
	public void showCurrentYearOperationsDetails() throws Exception {
		//Get the list of operations for the current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String fromDate = currentYear + "-01-01"; //January 1st of current year
		String toDate = currentYear + "-12-31"; //December 31st of current year
		ArrayList<Operation> operations = operationDAO.getOperations(fromDate, toDate);
		
		//Compute total expenses and income amount and the difference between them
		double expensesAmount = 0, incomeAmount = 0;
		for(int i = 0; i < operations.size(); i++) {
			//If operation is an income
			if(operations.get(i).getOperationType() == 1) 
				incomeAmount += operations.get(i).getAmount();
			else
				expensesAmount += operations.get(i).getAmount();
		}
		double total = incomeAmount - expensesAmount;
		
		DecimalFormat decimalFormat = new DecimalFormat("##.00"); //Convert number to 2 decimal digits
		
		operationsNumberForCurrentYearLabel.setText(operations.size() + "");
		incomeForCurrentYearLabel.setText(decimalFormat.format(incomeAmount) + mainApp.getCurrency());
		expensesForCurrentYearLabel.setText(decimalFormat.format(expensesAmount) + mainApp.getCurrency());
		operationsTotalAmountForCurrentYearLabel.setText((total > 0 ? "+" : "") + decimalFormat.format(total) + mainApp.getCurrency());
	}
	
	/**
	 * Shows the details of the operations performed during the current month
	 * @throws Exception if OperationDAO throws exception
	 * @see operationDAO#getOperations()
	 */
	public void showCurrentMonthOperationsDetails() throws Exception {
		//Get the list of operations for the current month
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; //Current month returned is 0-indexed
		String fromDate = currentYear + "-" + (currentMonth < 10 ? "0" : "") + currentMonth + "-01";
		String toDate = currentYear + "-" + (currentMonth < 10 ? "0" : "") + currentMonth  +"-" + Calendar.getInstance().getActualMaximum(Calendar.DATE);
		ArrayList<Operation> operations = operationDAO.getOperations(fromDate, toDate);

		//Compute total expenses and income amount and the difference between them
		int expensesAmount = 0, incomeAmount = 0;
		for(int i = 0; i < operations.size(); i++) {
			//If operation is an income
			if(operations.get(i).getOperationType() == 1) 
				incomeAmount += operations.get(i).getAmount();
			else
				expensesAmount += operations.get(i).getAmount();
		}
		int total = incomeAmount - expensesAmount;
		
		DecimalFormat decimalFormat = new DecimalFormat("##.00"); //Convert number to 2 decimal digits
		
		operationsNumberForCurrentMonthLabel.setText(operations.size() + "");
		incomeForCurrentMonthLabel.setText(decimalFormat.format(incomeAmount) + mainApp.getCurrency());
		expensesForCurrentMonthLabel.setText(decimalFormat.format(expensesAmount) + mainApp.getCurrency());
		operationsTotalAmountForCurrentMonthLabel.setText(((total > 0) ? "+" : "") + decimalFormat.format(total) + mainApp.getCurrency());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void populateOperationsChart() throws Exception { 
		//Get the list of operations for the current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String fromDate = currentYear + "-01-01"; //January 1st of current year
		String toDate = currentYear + "-12-31"; //December 31st of current year
		ArrayList<Operation> operations = operationDAO.getOperations(fromDate, toDate);
		
		//List of months based on their index - Example: 01 = January (Remember array is 0-indexed)
		ArrayList<String> months = new ArrayList<String>();
		months.add(bundle.getString("january"));
		months.add(bundle.getString("february"));
		months.add(bundle.getString("march"));
		months.add(bundle.getString("april"));
		months.add(bundle.getString("may"));
		months.add(bundle.getString("june"));
		months.add(bundle.getString("july"));
		months.add(bundle.getString("august"));
		months.add(bundle.getString("september"));
		months.add(bundle.getString("october"));
		months.add(bundle.getString("november"));
		months.add(bundle.getString("december"));
		
		//Compute expenses and income amount per month
		Double [] arr = new Double[12];
		ArrayList<Double> expensesAmountPerMonth= new ArrayList<>(Arrays.asList(arr));
		Collections.fill(expensesAmountPerMonth, 0.0); //Fills all 12 entries with 0
		ArrayList<Double> incomeAmountPerMonth = new ArrayList<>(Arrays.asList(arr));
		Collections.fill(incomeAmountPerMonth, 0.0); //Fills all 12 entries with 0
		for(int i = 0; i < 12;  i++) { //Loop 12 times for the 12 months
			for(int j = 0; j < operations.size(); j++) {
				//Check if operation has been made during month i
				String[] date = operations.get(j).getDate().split("-");
				if(Integer.parseInt(date[1]) == i+1) { //i+1 because months are 0-indexed
					if(operations.get(j).getOperationType() == 1) //If operation is an income
						incomeAmountPerMonth.set(i, incomeAmountPerMonth.get(i) + operations.get(j).getAmount());
					else 
						expensesAmountPerMonth.set(i, expensesAmountPerMonth.get(i) + operations.get(j).getAmount());
				}
			}
		}
		
		//Populate expenses series
		XYChart.Series expensesSeries = new XYChart.Series();
		expensesSeries.setName(bundle.getString("expenses"));
		for(int i = 0; i < expensesAmountPerMonth.size(); i++)
	    		expensesSeries.getData().add(new XYChart.Data(months.get(i), expensesAmountPerMonth.get(i)));
	    
	    //Populate income series
		XYChart.Series incomeSeries = new XYChart.Series();
		incomeSeries.setName(bundle.getString("income"));
		for(int i = 0; i < incomeAmountPerMonth.size(); i++)
			incomeSeries.getData().add(new XYChart.Data(months.get(i), incomeAmountPerMonth.get(i)));
	    
        operationsChart.getData().addAll(expensesSeries, incomeSeries);
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
