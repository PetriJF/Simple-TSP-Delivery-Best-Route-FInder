/* Year 2 Semester 2 End of Semester Project
 * TSP focusing on making as little angry customers by overpassing the 30 min pizza delivery limit 
 * Name: James Florin Petri
 * St.No.: 19712119
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class TSP {
	// CONSTANTS
	final static double EARTH_RADIUS = 6371;					// Mean radius of the earth
	final static double PRECISION = 1000000;					// The number of decimals after doubles
	final static int WINDOW_WIDTH = 1200;						// Window dimensions. Not resizable!			
	final static int WINDOW_HEIGHT = 950;
	final static int MENU_PANEL_WIDTH = 200;					// Dimensions for the right panel
	final static int MENU_PANEL_HEIGHT = WINDOW_HEIGHT;
	final static int MENU_PANEL_ELEMENT_WIDTH = 175;
	final static int BOTTOM_PANEL_HEIGHT = 200;					// Dimensions for the bottom panel
	final static int BOTTOM_PANEL_WIDTH = WINDOW_WIDTH - MENU_PANEL_WIDTH;
	final static int MAP_HEIGHT = WINDOW_HEIGHT - BOTTOM_PANEL_HEIGHT;	// Dimensions for the map
	final static int MAP_WIDTH = WINDOW_WIDTH - MENU_PANEL_WIDTH;
	final static int ROUTE_OUTPUT_BOX_HEIGHT = 345;				// The height of the output box
	final static int PROGRESS_BAR_HEIGHT = 25;					// The progress bar height
	final static double DEFAULT_LENGTHWISE_VALUES_FOR_ERROR[] = { 1, 1, 1, 1, 0.7, 0.45, 0.3, 0.2, 0.15, 0.15, 0.15 };	// The values for the route searcher
	
	// Value referrers for the progress bar modes in order to make things more understandable
	final static int PB_INITIAL_ROUTE = 1;
	final static int PB_OPTIMIZED_ROUTE = 2;
	final static int PB_SPEED_MODE = 3;
	
	// Co-ordinate mapping Constants. Please read description
	final static double S_LIMIT = 53.28426;
	final static double N_LIMIT = 53.41248;
	final static double E_LIMIT = -6.45509;
	final static double W_LIMIT = -6.71319;	// Or -6.71271
	/*	Because the map covers a distance great enough for the curvature of the Earth to be visible, the picture provided
	 * is more accurately represented as a irregular trapezoid with a rounding bulge at the middle. For this reason without
	 * the implementation of a mapping technique the error margin is of 63.8m on the Latitude and 225m on the Longitude. With
	 * the current zoom ratio, even without the mapping technique (and by square approximation) the representation on the map
	 * is still quite accurate. 
	 * In the future this might be an improvement that will be done to the program. The corners of the map can be seen below.
	 * NW=(53,41317; -6,71261) NE=(53,41179; -6,45509) SW=(53,28271; -6,71319) and SE=(53,28426; -6,45509)
	 */
	
	// VARIABLES
	static double scooterSpeed = 60.0;								// km/h	<- Speedy scooter
	static int animationSpeed = 1;									// The animation speed
	static Window application;										// The main window
	//static String inputString = new String("");						// Used to store the input for future possible functionalities
	static double startingLat = 53.38197, startingLon = -6.59274;	// The coordinates of the Apache Pizza
	static double errorMarginSearch = 1;							// Used as the error percentage estimation in the search of other possible routes Check optimization method						
	static boolean progressBarMode = true;							// Specifies if the progressBar is in speed mode or not
	static Route route;												// Holds the route and the relevant methods to the route
	static Map map;													// The map and the graphics surrounding it
	static Bar bar;													// The progress bar and the graphics surrounding it
	static JTextArea outputBox;										// The output Box. Made here in order to be accessible for experimentation from anywhere in the code
	
	public static void main(String args[]) {
		application = new Window();									// Initialize the application
	}
	
/////////////////////////////// INTERFACE GUI CREATION ////////////////////////////////
	static class Window extends JFrame implements ActionListener {	// Used for forming the GUI
		// The names of the variables below are intuitive, so no comments will be added for describing them. They represent all the elements that are part of the GUI
		JFrame startingPointWindow;
		JFrame settingsWindow;
		JFrame distanceMatWindow;
		JFrame addressesWindow;
		JFrame customRouteWindow;
		JPanel inputPanel;
		JPanel outputPanel;
		JButton computeBtn;
		JButton computeAnimationBtn;
		JButton startingPointBtn;
		JButton startingPointCloseBtn;
		JButton showDistMatBtn;
		JButton hideDistMatBtn;
		JButton customRouteBtn;
		JButton customRouteCloseBtn;
		JButton clearMapBtn;
		JButton addressesBtn;
		JButton settingsBtn;
		JButton saveSettingsBtn;
		JTextArea inputBox;
		JTextArea addressOrderBox;
		JTextArea customRouteBox;
		JTextArea distanceMatrixBox;
		JCheckBox progressBarCheck;
		JTextField startingLatBox;
		JTextField startingLonBox;
		JTextField scooterSpeedBox;
		JTextField animationSpeedBox;
		static JTextField testsConductedBox;
		static JTextField potentialCandidatesBox;
		static JTextField angryMinutesText;
		static JTextField totalDistanceText;
		
		// CONSTANTS - the labels that don't have the possibility of change are marked here
		final static JLabel angryMinutesLabel = new JLabel("Angry minutes formed: ");
		final static JLabel totalDistanceLabel = new JLabel("Route's total distance: ");
		final static JLabel routeBoxLabel = new JLabel("Route taken: ");
		final static JLabel distanceMatrixLabel = new JLabel("The distance matrix for the inserted addresses can be copied from below:");
		final static JLabel customRouteLabel = new JLabel("Please insert your custom route separating the order identifiers with a ',' or ', '");
		final static JLabel progressBarLabel = new JLabel("Progress:");
		final static JLabel extraInformationLabel = new JLabel("Verified:              Potential:");
		final static JLabel scooterSpeedLabel = new JLabel("<html><div style='text-align: left;'>Set the speed of the scooter:</div></html>");
		final static JLabel scooterInfoLabel = new JLabel("<html><div style='text-align: left;'>Modifies the speed of the magic scooter from the default 60km/h<br><br></div></html>");
		final static JLabel progressBarSetInfoLabel = new JLabel("<html><div style='text-align: left;'>Unticking the checkbox will reduce the speed of the instant compute mode by an<br>average of 50%<br><br></div></html>");
		final static JLabel progressBarSettingsLabel = new JLabel("<html><div style='text-align: left;'>Set the progress bar's activity:</div></html>");
		final static JLabel animationSpeedLabel = new JLabel("<html><div style='text-align: left;'>Set the speed of the animation:</div></html>");
		final static JLabel animationInfoLabel = new JLabel("<html><div style='text-align: left;'>The animation speed is represented in ms, 1ms being the default setting and fastest<br>animation speed. By increasing the number, the animation speed slows down.<br>Please only add integer values!<br><br></div></html>");
		final static JLabel addressesLabel = new JLabel("The delivery addresses in the most efficient route that was found:");
		
		// Non GUI variables
		static double totalDistance;	// Total distance copy
		static int totalAngryMinutes;	// Total angry minutes copy
		
		public Window() {
			// Setting up the secondary windows
			startingPointWindow = new JFrame();
			distanceMatWindow = new JFrame();
			customRouteWindow = new JFrame();
			settingsWindow = new JFrame();
			addressesWindow = new JFrame();
			
			// Setting up the map and progress bar objects and their relevant graphics
			map = new Map();
			bar = new Bar();
			
			// Setting up all the graphical elements
			testsConductedBox = new JTextField();
			potentialCandidatesBox = new JTextField();
			scooterSpeedBox = new JTextField();
			animationSpeedBox = new JTextField();
			inputPanel = new JPanel();
			outputPanel = new JPanel();
			computeBtn = new JButton("<html><div style='text-align: center;'>Instant Compute<br>(Faster)</div></html>");
			computeAnimationBtn = new JButton("<html><div style='text-align: center;'>Visualised Computing<BR>(Slower)</div></html>");
			showDistMatBtn = new JButton("Show Distance Matrix");
			hideDistMatBtn = new JButton("Hide Distance Matrix");
			startingPointBtn = new JButton("Set Starting Point");
			startingPointCloseBtn = new JButton("Save and exit");
			customRouteBtn = new JButton("Add a custom Route");
			clearMapBtn = new JButton("Clear the map");
			customRouteCloseBtn = new JButton("Save and exit");
			addressesBtn = new JButton("Show address order");
			settingsBtn = new JButton("Additional Settings");
			saveSettingsBtn = new JButton("Save and exit");
			addressOrderBox = new JTextArea();
			inputBox = new JTextArea();
			outputBox = new JTextArea();
			distanceMatrixBox = new JTextArea();
			customRouteBox = new JTextArea();
			progressBarCheck = new JCheckBox("Deactivate the Progress Bar for speed purposes in the instant compute mode");
			
			// The main window's characteristics
			this.setTitle("TSP");
			this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);
			this.setLayout(null);		// No layout manager. Doing it manually
			
			// Bottom Panel (INPUT) Settings and elements
			JPanel inputButtonPanel = new JPanel();
			inputButtonPanel.setSize(new Dimension(150, 150));
			inputButtonPanel.setLayout(new GridLayout(2, 1, 3, 3));
			computeBtn.setPreferredSize(new Dimension(150, 72));
			computeBtn.addActionListener(this);
			inputButtonPanel.add(computeBtn);
			computeAnimationBtn.setPreferredSize(new Dimension(150, 72));
			computeAnimationBtn.addActionListener(this);
			inputButtonPanel.add(computeAnimationBtn);
			inputBox.setLineWrap(true);
			inputBox.setWrapStyleWord(true);
			JScrollPane scrollPane = new JScrollPane(inputBox);
			scrollPane.setPreferredSize(new Dimension(BOTTOM_PANEL_WIDTH - 160, 150));
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			inputPanel.setBounds(3, MAP_HEIGHT, BOTTOM_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);
			
			// Right Panel Menu Bar Settings (OUTPUT and features) and elements
			angryMinutesText = new JTextField(" 0");
			angryMinutesText.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 25));
			angryMinutesText.setBackground(Color.WHITE);
			angryMinutesText.setEditable(false);
			angryMinutesText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			totalDistanceText = new JTextField(" 0");
			totalDistanceText.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 25));
			totalDistanceText.setBackground(Color.WHITE);
			totalDistanceText.setEditable(false);
			totalDistanceText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			startingPointBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			outputBox.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, ROUTE_OUTPUT_BOX_HEIGHT));
			outputBox.setWrapStyleWord(true);
			outputBox.setLineWrap(true);
			outputBox.setEditable(false);
			outputBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			outputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			outputPanel.setBounds(MAP_WIDTH, 0, MENU_PANEL_WIDTH, MENU_PANEL_HEIGHT);
			startingPointBtn.addActionListener(this);
			customRouteBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			customRouteBtn.addActionListener(this);
			clearMapBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			clearMapBtn.addActionListener(this);
			addressesBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			addressesBtn.addActionListener(this);
			settingsBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			settingsBtn.addActionListener(this);
			potentialCandidatesBox.setPreferredSize(new Dimension((MENU_PANEL_ELEMENT_WIDTH - 5) / 2, 25));
			potentialCandidatesBox.setBackground(Color.WHITE);
			potentialCandidatesBox.setEditable(false);
			testsConductedBox.setPreferredSize(new Dimension((MENU_PANEL_ELEMENT_WIDTH - 5) / 2, 25));
			testsConductedBox.setEditable(false);
			testsConductedBox.setBackground(Color.WHITE);

			// Child Window for changing the starting point
			JLabel startingPointWindowLabel = new JLabel("<HTML>The starting location for the route can be set in the text boxes<BR>below. Please settle the starting point or keep the default one.</HTML>");
			JLabel startingLatLabel = new JLabel("Starting Latitude(NCoords):    ");
			JLabel startingLonLabel = new JLabel("Starting Longitude(WCoords):");
			startingLatBox = new JTextField(Double.toString(startingLat));
			startingLonBox = new JTextField(Double.toString(startingLon));
			startingLatBox.setPreferredSize(new Dimension(150, 25));
			startingLonBox.setPreferredSize(new Dimension(150, 25));
			startingPointWindow.setTitle("Set Starting Point");
			startingPointWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			startingPointWindow.setSize(400, 180);
			startingPointWindow.setResizable(false);
			startingPointWindow.setLayout(new FlowLayout(FlowLayout.CENTER));
			startingPointWindow.setLocationRelativeTo(null);
			startingPointWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);;
			startingPointWindow.add(startingPointWindowLabel);
			startingPointWindow.add(startingLatLabel);
			startingPointWindow.add(startingLatBox);
			startingPointWindow.add(startingLonLabel);
			startingPointWindow.add(startingLonBox);
			startingPointWindow.add(startingPointCloseBtn);
			startingPointCloseBtn.addActionListener(this);
			
			// Child Window for getting a copy of the distance matrix
			showDistMatBtn.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, 50));
			distanceMatrixBox.setPreferredSize(new Dimension(880, 900));
			distanceMatrixBox.setFont(new Font("TimesRoman", Font.PLAIN, 5));
			distanceMatWindow.add(distanceMatrixLabel);
			distanceMatWindow.add(distanceMatrixBox);
			distanceMatWindow.add(hideDistMatBtn);
			distanceMatWindow.setTitle("Distance Matrix");
			distanceMatWindow.setSize(new Dimension(1000, 1000));
			distanceMatWindow.setLocationRelativeTo(null);
			distanceMatWindow.setLayout(new FlowLayout(FlowLayout.CENTER));
			distanceMatWindow.setResizable(false);
			distanceMatWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
			showDistMatBtn.addActionListener(this);
			hideDistMatBtn.addActionListener(this);
			
			// Child Window for changing the route that was found with a custom, user inputted, route. Useful for manually changing the route for experimenting
			customRouteBox.setPreferredSize(new Dimension(500, 275));
			customRouteBox.setLineWrap(true);
			customRouteBox.setWrapStyleWord(true);
			customRouteCloseBtn.addActionListener(this);
			customRouteCloseBtn.setPreferredSize(new Dimension(300, 50));
			customRouteWindow.setSize(new Dimension(600, 400));
			customRouteWindow.setTitle("Add a custom route");
			customRouteWindow.setLocationRelativeTo(null);
			customRouteWindow.setLayout(new FlowLayout(FlowLayout.CENTER));
			customRouteWindow.setResizable(false);
			customRouteWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
			customRouteWindow.add(customRouteLabel);
			customRouteWindow.add(customRouteBox);
			customRouteWindow.add(customRouteCloseBtn);
			
			// Child Window for applying the various other settings available
			progressBarCheck.setFocusable(false);
			progressBarCheck.setSelected(true);
			scooterSpeedBox.setPreferredSize(new Dimension(475, 25));
			animationSpeedBox.setPreferredSize(new Dimension(475, 25));
			saveSettingsBtn.setPreferredSize(new Dimension(475, 30));
			saveSettingsBtn.addActionListener(this);
			settingsWindow.setSize(new Dimension(500, 400));
			settingsWindow.setTitle("Settings");
			settingsWindow.setResizable(false);
			settingsWindow.setLocationRelativeTo(null);
			settingsWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
			settingsWindow.setLayout(new FlowLayout(FlowLayout.LEFT));
			progressBarSettingsLabel.setFont(new Font(progressBarSettingsLabel.getFont().getName(), Font.BOLD, 14));
			scooterSpeedLabel.setFont(new Font(scooterSpeedLabel.getFont().getName(), Font.BOLD, 14));
			animationSpeedLabel.setFont(new Font(animationSpeedLabel.getFont().getName(), Font.BOLD, 14));
			settingsWindow.add(progressBarSettingsLabel);
			settingsWindow.add(progressBarCheck);
			settingsWindow.add(progressBarSetInfoLabel);
			settingsWindow.add(scooterSpeedLabel);
			settingsWindow.add(scooterSpeedBox);
			settingsWindow.add(scooterInfoLabel);
			settingsWindow.add(animationSpeedLabel);
			settingsWindow.add(animationSpeedBox);
			settingsWindow.add(animationInfoLabel);
			settingsWindow.add(saveSettingsBtn);
			
			// Child Window for displaying the addresses that need to be visited in the correct order
			addressOrderBox.setLineWrap(true);
			addressOrderBox.setWrapStyleWord(true);
			addressOrderBox.setEditable(false);
			JScrollPane addressScrollPane = new JScrollPane(addressOrderBox);
			addressScrollPane.setPreferredSize(new Dimension(575, 500));
			addressScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			addressesWindow.setVisible(false);
			addressesWindow.setLocation(0,0);
			addressesWindow.setResizable(false);
			addressesWindow.setLayout(new FlowLayout(FlowLayout.LEFT));
			addressesWindow.setSize(new Dimension(600,600));
			addressesWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
			addressesWindow.setTitle("Address order");
			addressesWindow.add(addressesLabel);
			addressesWindow.add(addressScrollPane);
			
			// Adding everything together into the relevant panels
			inputPanel.add(inputButtonPanel);
			inputPanel.add(scrollPane);
			outputPanel.add(progressBarLabel);
			outputPanel.add(bar);
			outputPanel.add(totalDistanceLabel);
			outputPanel.add(totalDistanceText);
			outputPanel.add(angryMinutesLabel);
			outputPanel.add(angryMinutesText);
			outputPanel.add(extraInformationLabel);
			outputPanel.add(testsConductedBox);
			outputPanel.add(potentialCandidatesBox);
			outputPanel.add(routeBoxLabel);
			outputPanel.add(outputBox);
			outputPanel.add(startingPointBtn);
			outputPanel.add(clearMapBtn);
			outputPanel.add(addressesBtn);
			outputPanel.add(showDistMatBtn);
			outputPanel.add(customRouteBtn);
			outputPanel.add(settingsBtn);
			
			// Adding the panels to the main window
			this.add(inputPanel);
			this.add(map);
			this.add(outputPanel);
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {				
			Object src = e.getSource();								// Marks the button that was pressed
			
			if(src == computeBtn || src == computeAnimationBtn) {	// The buttons that compute the route. One button does it in a fast manner, while the other in a animated one
				String text = inputBox.getText();					// Getting the input from the input text box
				boolean animation = (src == computeBtn) ? false : true;	// Figuring out if the route is computed with an animation or not
				
				try {
					String input[] = text.split("\\R");				// Splitting the input by line
					route = new Route(input);						// And forming the route object that is used to hold the route and the necessary tools to work with the said route
					
					// Adjusting the search radius by the number of cities. This is the default value
					errorMarginSearch = DEFAULT_LENGTHWISE_VALUES_FOR_ERROR[(int)(route.getLocationCount()/10) + ((route.getLocationCount() > 9)?(-1):(0))];
					
					// Computing the distance matrix of the route (Always the first step)
					route.computeDistanceMatrix();
					map.repaint();									// Initial print of the matrix
					
					// Checking if the progress bar is set to speed mode in order to decrease computational time. Also makes the progress bar yellow
					if(animation == false && progressBarMode == true) {
						bar.set(PB_SPEED_MODE, 0);
						bar.paintImmediately(0,0,MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT);
					}
					
					// Forming the initial route for the given input using nearest neighbor
					route.formInitialRoute((animation == true) ? animationSpeed : 0);	// Used to get an upper bound for the angry minutes!
					updateInformation();												// Updates the relevant information
					
					// Forming the optimized route
					route.formOptimizedRoute(animation);
					// Redundancy in double checking the optimized route with a 2-opt alternative
					route.apply2OptMinutes(animation);
					updateInformation();				// Update information
					
					// If speed mode is active, make the progress bar green at the end
					if(animation == false) {
						bar.set(PB_SPEED_MODE, 1);
						bar.paintImmediately(0,0,MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT);
					}
					map.repaint();						// Final version of the map printed
				} catch (Exception err) {				// In the case of an error, it is expected that it is a wrong input
					outputBox.setText("Invalid input. Information refused");
					JOptionPane.showMessageDialog(new JFrame(), "Please make sure that everything is separated properly by a ',' and the orders are separated by a new line!", "Error",JOptionPane.ERROR_MESSAGE);
				}
			} else if(src == startingPointBtn) {		// Used to open the window that is used to change the starting location
				startingLatBox.setText(Double.toString(startingLat));	// Sets the values in the text boxes with default values
				startingLonBox.setText(Double.toString(startingLon));
				startingPointWindow.setVisible(true);
			} else if(src == startingPointCloseBtn) {	// Used to save the new starting location
				startingPointWindow.setVisible(false);						// Closing the window used for setting the starting location
				startingLon = Double.parseDouble(startingLonBox.getText());	// and saving the new value
				startingLat = Double.parseDouble(startingLatBox.getText());
			} else if(src == showDistMatBtn) {			// Shows the distance matrix if possible
				if (route.getLocationCount() > 0) {
					distanceMatrixBox.setText(route.getDistanceMatrix());
					distanceMatWindow.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Please input all the information needed!", "Error",JOptionPane.ERROR_MESSAGE);
				}
			} else if(src == hideDistMatBtn) {			// Hides the distance matrix once done with it
				distanceMatWindow.setVisible(false);
			} else if(src == customRouteBtn) {			// Used to open the window used to add a custom route to the user's input
				if(route.getLocationCount() > 0) {
					customRouteWindow.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Please insert all the addresses!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else if(src == customRouteCloseBtn) {		// Saves the route into a usable form in order to print it on the table and update the relevant information
				String temp = customRouteBox.getText();
				if(temp.equals("") == false && temp.charAt(temp.length() - 1) != ',') {
					route.order = new int[route.getLocationCount()];
					if(temp.charAt(temp.indexOf(',') + 1) == ' ')	// Works when the route numbers are separated by ',' or a ', '
						route.order = Arrays.stream(("0, " + temp).split(", ")).mapToInt(Integer :: parseInt).toArray();
					else
						route.order = Arrays.stream(("0," + temp).split(",")).mapToInt(Integer :: parseInt).toArray();
					outputBox.setText(customRouteBox.getText());

					updateInformation();					// Updates the information
					customRouteWindow.setVisible(false);	// And closes the window
				} else {
					outputBox.setText("You have inserted an INVALID route. Please add a new one or exit the window! Common mistakes: Invalid number of elements; ',' at the end;");
				}
				
				repaint();
			} else if(src == clearMapBtn) {				// Clears the map of all the points and lines
				map.clearMap();
				map.repaint();
			} else if(src == settingsBtn) {				// Shows the additional settings window
				animationSpeedBox.setText(Integer.toString(animationSpeed));	// Sets up the elements with the current information, like the current scooter speed and so on.
				scooterSpeedBox.setText(Double.toString(scooterSpeed));
				progressBarCheck.setSelected(progressBarMode);
				settingsWindow.setVisible(true);
			} else if(src == saveSettingsBtn) {			// Saves the new information from settings window
				animationSpeed = Integer.parseInt(animationSpeedBox.getText());	// Getting the values of the animation speed, the scooter speed and the progress bar's mode
				scooterSpeed = Double.parseDouble(scooterSpeedBox.getText());
				progressBarMode = progressBarCheck.isSelected();
				settingsWindow.setVisible(false);
			} else if(src == addressesBtn) {			// Displays the addresses in a nice fashion and the best order that was found
				addressesWindow.setVisible(true);		// Shows the window
				
				String temp = "";
				
				if(1 < route.getLocationCount()) 		// Displays the order of the address and the relevant address in the best route found
					for(int i = 1; i < route.getLocationCount(); i++) 
						temp = temp + route.order[i] + "\t->\t" + route.address[route.order[i]].getName() + "\n";
				
				addressOrderBox.setText(temp);
			}
		} 
		
		public static void updateInformation() {		// Updates the angry minutes and the distance displays and also the route in the output box
			angryMinutesText.setText(Double.toString(route.getAngryMinutes()));
			totalDistanceText.setText(Double.toString(route.getDistance()));
			outputBox.setText(String.join(",", Arrays.stream(Arrays.copyOfRange(route.order,1,route.order.length)).mapToObj(String::valueOf).toArray(String[]::new)));
		}
	
		public static void setAlgInfo(int testNo, int potentials) {			// Sets some technical details regarding the routes considered
			testsConductedBox.setText(Integer.toString(testNo));			// The number of routes that were considered
			potentialCandidatesBox.setText(Integer.toString(potentials));	// The number of routes that had potential and were tested
		}
	}

///////////////////////////////// THE ROUTE FORMING ///////////////////////////////////
	static class Route {								// The route class. It is used to hold the route that we are computing and all the tools necessary for it
		public static Location address[];				// The address object which holds all the relevant information of an order
		private static int locationCount;				// Holds the number of addresses
		private static double distMat[][];				// Holds the distance matrix
		public static int order[];						// Holds the order that the algorithm works on
		public static int bestOrder[];					// Holds the best order
		private static double currentBestTime;			// Holds the best time in terms of angry minutes
		public static int currentlyFound;				// Holds the count of each new neighbor for the animation
		
		Route(String []input) {							// Constructor
			locationCount = input.length + 1;			// Adds 1 to the length as we are adding the apache pizza information
			address = new Location[locationCount];		// initializing the address array and all the other default information
			order = new int[locationCount];
			bestOrder = new int[locationCount];
			currentlyFound = 0;
			currentBestTime = 0;
			
			// Preparing and initializing all the address objects
			address[0] = new Location("0,Apache Pizza Maynooth,0," + Double.toString(startingLat) + "," + Double.toString(startingLon));
			for(int pos = 1; pos < locationCount; pos++)
				address[pos] = new Location(input[pos-1]);
		}
		
		public void formInitialRoute(int waitTimeMs) {
			// This is a implementation of Nearest Neighbor
			boolean delivered[] = new boolean[locationCount];	// Used to mark if a order was added to the route
			int node = 0;										// Represents the node order we are currently on
			int top, minPos = -1; 								// Extra variables representing the last order considered and the the minimum position
			double min;											// The minimum value
			
			delivered[node] = true;								// Marking the first element in the route as Apache Pizza 
			order[node] = 0;
			node = 1;
			
			outputBox.setText("");								// Resetting the output box
			
			if(waitTimeMs != 0 || progressBarMode == false)		// Figuring out if the animation is used for the progress bar and prepare it if so
				bar.set(PB_INITIAL_ROUTE, locationCount);
			
			while(node < locationCount) {						// This is basically a neirest neighbor implementation that goes through each order and links
				top = order[node - 1];							// the closest together.
				min = Double.MAX_VALUE;
				
				for(int i = 1; i < locationCount; i++) {
					if(delivered[i] == false && distMat[top][i] < min) {
						min = distMat[top][i];
						minPos = i;
					}
				}
				
				delivered[minPos] = true;
				order[node] = minPos;
				bestOrder[node] = minPos;
				node++;
				outputBox.append(minPos + ((node < locationCount) ? "," : ""));
				currentlyFound += 1;
				
				if(waitTimeMs != 0) {							// If the the animation mode is activated, animate each neighbor found on the map
					map.paintImmediately(5,5,MAP_WIDTH - 5, MAP_HEIGHT - 5);
					
					try {
						Thread.sleep(waitTimeMs);
					} catch(InterruptedException ex1) {
						Thread.currentThread().interrupt();
					}
				}
				
				if(waitTimeMs != 0 || progressBarMode == false) {	// If the progress bar is activated, show the progress
					bar.add(PB_INITIAL_ROUTE);
					bar.paintImmediately(0,0,MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT);
				}
			}
			currentlyFound = locationCount;
			currentBestTime = getAngryMinutes();
			if(waitTimeMs == 0) {									// Making sure the final version of the map is done
				map.paintImmediately(5,5,MAP_WIDTH - 5, MAP_HEIGHT - 5);
			}
		}
		
		public void formOptimizedRoute(boolean anim) {
			// Forcing the first 2 elements in the nearest neigbor algorithm in order to force difference 
			boolean delivered[] = new boolean[locationCount];
			int tempOrder[] = new int[locationCount];
			int node;
			int top;
			double min;
			int minPos = -1;
			int testsConducted = 0;
			int potentials = 0;
			
			// Sets the animation mode of the progress bar 
			if(anim == true || progressBarMode == false)
				bar.set(PB_OPTIMIZED_ROUTE, ((locationCount - 1) * (locationCount - 2))/2);
			
			for(int i = 1; i < locationCount - 1; i++) {
				for(int j = i + 1; j < locationCount; j++) { 	// Forcing the first 2 elements
					delivered = new boolean[locationCount];		// and resetting the arrays for that
					tempOrder = new int[locationCount];
					delivered[0] = true; tempOrder[0] = 0;
					delivered[i] = true; tempOrder[1] = i;
					delivered[j] = true; tempOrder[2] = j;
					node = 3;
					testsConducted++;

					while(node < locationCount) {				// Nearest Neighbor Again
						min = Double.MAX_VALUE;
						top = tempOrder[node-1];
						
						for(int k = 1; k < locationCount; k++) {
							if(delivered[k] == false && distMat[top][k] < min) {
								min = distMat[top][k];
								minPos = k;
							}
						}
						
						delivered[minPos] = true;
						tempOrder[node] = minPos;
						
						node++;
					}
					
					// Apply the animation of the progress bar if requested
					if(anim == true || progressBarMode == false) {
						bar.add(PB_OPTIMIZED_ROUTE);
						bar.paintImmediately(0,0,MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT);
					}
					
					// If a route seems as a protential candidate(it is in errorMarginSearch % range of the current best time) then apply the following:
					double tempAM = getAngryMinutes(tempOrder);
					if(tempAM < currentBestTime + currentBestTime * errorMarginSearch) {
						potentials++;
						
						// Apply 2 opt focused on angry minutes
						order = Arrays.copyOf(tempOrder, locationCount);
						apply2OptMinutes(anim);
						if(getAngryMinutes() < currentBestTime) {
							bestOrder = Arrays.copyOf(order, locationCount);
							currentBestTime = getAngryMinutes();
						}
						
						// Apply 2 opt focused on distance
						order = Arrays.copyOf(tempOrder, locationCount);
						apply2OptDistance(anim);
						if(getAngryMinutes() < currentBestTime) {
							bestOrder = Arrays.copyOf(order, locationCount);
							currentBestTime = getAngryMinutes();
						}
						
						// Apply 2 opt focused on distance and then angry minutes
						apply2OptMinutes(anim);
						if(getAngryMinutes() < currentBestTime) {
							bestOrder = Arrays.copyOf(order, locationCount);
							currentBestTime = getAngryMinutes();
						}
						
						// And get the best of the bunch
						order = Arrays.copyOf(bestOrder, locationCount);
					}	
				}
			}
			application.setAlgInfo(testsConducted, potentials);	// Send the search information to the main window
		}
		
		// 2-Opt implementation that focuses on distance shortening
		public void apply2OptDistance(boolean animation) {
			int swaps = 1;
			double leastDistance = getDistance();

			while(swaps > 0 && locationCount >= 2) {
				swaps = 0;
				
				for(int i = 1; i < locationCount - 2; i++) {			// Go through all the codes and remove crossovers by inverting all the elements in the problematic area
					for(int j = i + 1; j < locationCount - 1; j++) {
						if(address[i].getDistance(address[i-1]) + address[j+1].getDistance(address[j]) >= address[i].getDistance(address[j+1]) + address[i-1].getDistance(address[j])) {
							reverseIndexOrder(i,j);
							if(leastDistance > getDistance()) {
								leastDistance = getDistance();
								if(animation == true) {			// Applying the check to the map
									map.paintImmediately(5,5,MAP_WIDTH - 5, MAP_HEIGHT - 5);
									try {
										Thread.sleep(animationSpeed);
									} catch(InterruptedException ex1) {
										Thread.currentThread().interrupt();
									}
								}
								swaps++;
							} else {
								reverseIndexOrder(i,j);
							}
							
						}
					}
				}
			}
			
			// Check the route and choose if it will be saves
			double tempTime = getAngryMinutes();
			if(currentBestTime > tempTime) {
				bestOrder = Arrays.copyOf(order, locationCount);
				currentBestTime = tempTime;
			}
		}
		
		// 2-Opt implementation focusing on minimising the angry minutes
		public void apply2OptMinutes(boolean animation) {
			int swaps = 1;
			double leastAngryMinutes = getAngryMinutes();
			
			while(swaps >= 0 && locationCount >= 2) {
				swaps = 0;
				
				for(int i = 1; i < locationCount - 1; i++) {
					for(int j = i + 1; j < locationCount; j++) {	// Goes through all the elements and it checks if reversing the elements between a range lowers
						// swap										// the angry minutes. If so, save the loop
						reverseIndexOrder(i, j);
						if(leastAngryMinutes > getAngryMinutes()) {
							leastAngryMinutes = getAngryMinutes();
							if(animation == true) {					// Applying the check to the map
								map.paintImmediately(5,5,MAP_WIDTH - 5, MAP_HEIGHT - 5);
								try {
									Thread.sleep(animationSpeed);
								} catch(InterruptedException ex1) {
									Thread.currentThread().interrupt();
								}
								
							}
							swaps++;
						} else {
							reverseIndexOrder(i, j);
						}
					}
				}
				swaps--;
			}
			
			// Testing the route and saving it if it is better
			double tempTime = getAngryMinutes();
			if(currentBestTime > tempTime) {
				bestOrder = Arrays.copyOf(order, locationCount);
				currentBestTime = tempTime;
			}
		}
		
		// Computes the distance matrix of the addresses that were inputted
		public void computeDistanceMatrix() {
			distMat = new double[locationCount][locationCount];
			for(int i = 1; i < locationCount; i++) {
				for(int j = 0; j < i; j++) {
					distMat[i][j] = address[i].getDistance(address[j]);
					distMat[j][i] = distMat[i][j];
				}
			}
		}
		
		// Returns the distance matrix of the input
		public String getDistanceMatrix() {
			String temp = "";
			
			for(int i = 0; i < locationCount; i++) { 
				for(int j = 0; j < locationCount; j++)
					temp = temp + Double.toString(distMat[i][j]) + " ";
				temp += "\n";
			}
			
			return temp;
		}
		
		// Computes the angry minutes to the current route with regard to scooter speed
		public static double getAngryMinutes() {
			double time = 0;
			double angryMinutes = 0;
			double speed = scooterSpeed / 60.0;
			
			for(int i = 1; i < locationCount; i++) {
				time += (distMat[order[i]][order[i-1]]) / speed;
				angryMinutes = angryMinutes + Math.max(0.0, (double)address[order[i]].getMin() + time - 30.0);
			}
			
			return angryMinutes;
		}
		
		// Computes the angry minutes to a specific array containing a route, and return the said value with regard to scooter speed
		public static double getAngryMinutes(int testRoute[]) {
			double time = 0;
			double angryMinutes = 0;
			double speed = (scooterSpeed / 60.0);
			
			for(int i = 1; i < locationCount; i++) {
				time += (distMat[testRoute[i]][testRoute[i-1]]) / speed;
				angryMinutes = angryMinutes + Math.max(0.0, (double)address[testRoute[i]].getMin() + time - 30.0);
			}
			
			return angryMinutes;
		}
		
		// Gets the total distance that is traversed in the current route
		public double getDistance() {
			double distance = 0;
			for(int i = 1; i < locationCount; i++)
				distance += distMat[order[i]][order[i-1]];
			
			return distance;
		}
		
		// Reverses all the elements in the order between to said point a and b
		private void reverseIndexOrder(int a, int b) {
			while(a < b) {
				order[a] += order[b];
				order[b] = order[a] - order[b];
				order[a] = order[a] - order[b];
				
				a += 1;
				b -= 1;
			}
		}
		
		// Gets the number of addresses
		public static int getLocationCount() {		return locationCount;	}
	}
	
/////////////////////////////// PROGRESS BAR GRAPHICS /////////////////////////////////
	static class Bar extends JPanel {
		Graphics2D g2D;											// The graphics tool
		private boolean speedMode;								// Checks if the bar is deactivated for increasing computational time
		private boolean finished;								// Checks if the initial route is done computing
		private int initialRouteTotalSteps;						// Next 4 represent all the steps until the route is computed fully
		private int initialRouteStep;
		private int optimizedRouteTotalSteps;
		private int optimizedRouteStep;
		private final static double INITIAL_ROUTE_PERCENTAGE_CAP = 0.05;	// Assigning the percentage representing the initial route
		private final static double OPTIMIZED_ROUTE_PERCENTAGE_CAP = 0.95;	// Assigning the percentage representing the optimization process
		private final static int BORDER = 2;								// Sets the border around the progress bar
		
		Bar() {			// The bar's constructor
			// The settings for the bar 
			this.setBounds(0,0,MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT);			
			this.setPreferredSize(new Dimension(MENU_PANEL_ELEMENT_WIDTH, PROGRESS_BAR_HEIGHT));
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			this.setLayout(null);
			this.setBackground(Color.DARK_GRAY);
			
			// Setting default values for the variables
			speedMode = false;
			finished = false;
			
			// Paint the bar
			this.repaint();
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	// The progress bar basically takes the step you are on together with the total number of steps, gets a ratio for that and 
			g2D = (Graphics2D) g;		// it paints a green rectangle over a dark background covering the said ratio applied to the bar's width
			if(!finished) {				
				if(!speedMode) {		// Basically this is what happens here
					g2D.setColor(Color.GREEN);
					int paintWidthInitial = (int)(((double)initialRouteStep / (double)initialRouteTotalSteps)*(INITIAL_ROUTE_PERCENTAGE_CAP * (double)MENU_PANEL_ELEMENT_WIDTH));
					int paintWidthOptimized = (int)(((double)optimizedRouteStep / (double)optimizedRouteTotalSteps)*(OPTIMIZED_ROUTE_PERCENTAGE_CAP * ((double)MENU_PANEL_ELEMENT_WIDTH - BORDER + 1)));
					
					g2D.fillRect(BORDER, BORDER, paintWidthInitial, PROGRESS_BAR_HEIGHT - 2*BORDER);
					if(initialRouteStep >= initialRouteTotalSteps - 1)	// Makes sure we are not overpassing the limit
						g2D.fillRect(paintWidthInitial, BORDER, paintWidthOptimized, PROGRESS_BAR_HEIGHT - 2*BORDER);
					System.out.println(optimizedRouteStep + " " + optimizedRouteTotalSteps);
				} if(speedMode) { 		// IF the processing happens in speed mode, the progress bar is made yellow
					g2D.setColor(Color.YELLOW);
					g2D.fillRect(BORDER, BORDER, MENU_PANEL_ELEMENT_WIDTH - 2*BORDER, PROGRESS_BAR_HEIGHT - 2*BORDER);
				}
			} else {	// Makes the progress bar green at the end showing that it finished
				g2D.setColor(Color.GREEN);
				g2D.fillRect(BORDER, BORDER, MENU_PANEL_ELEMENT_WIDTH - 2*BORDER, PROGRESS_BAR_HEIGHT - 2*BORDER);
				
				finished = false;
				speedMode = false;
			}
		}
		
		// Sets the mode of the progress bar
		public void set(int type, int count) {
			if(type == PB_INITIAL_ROUTE) {			// Means that we are working on the initial route
				initialRouteTotalSteps = count;
				initialRouteStep = 0;
			} else if(type == PB_OPTIMIZED_ROUTE) {	// Means that we are working on the optimized route
				optimizedRouteTotalSteps = count;
				optimizedRouteStep = 0;
			} else if(type == PB_SPEED_MODE) {		// Means that we are working in speed mode over the whole code
				speedMode = true;
				if(count != 0)
					finished = true;
			}
		}
		
		// Marks that we completed another step from the total number of steps
		public void add(int type) {
			if(type == PB_INITIAL_ROUTE && initialRouteStep <= initialRouteTotalSteps)
				initialRouteStep++;
			else if(type == PB_OPTIMIZED_ROUTE && optimizedRouteStep <= optimizedRouteTotalSteps)
				optimizedRouteStep++;
		}
	}
	
////////////////////////////////// MAP GRAPHICS ///////////////////////////////////////
	static class Map extends JPanel {
		final static int POINT_SIZE = 5;	// The size of the points representing addresses
		private BufferedImage IMG;			// The map image
		private static boolean clearMap;	// Check if the map should be cleared
		Graphics2D g2D;						// The graphics
		
		
		Map() {	// Default Constructor
			// The settings for the map panel
			this.setBounds(5,5,MAP_WIDTH - 5, MAP_HEIGHT - 5);
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			this.setLayout(null);
			
			// Reset the variables
			clearMap = false;
			
			// Try getting the image
			try { // TODO: REMEMBER TO CHANGE THE FILE TO SAME FOLDER AFTER
				IMG = ImageIO.read(new File("src/map.png"));
			} catch (IOException e) {
				System.out.println("ERROR");
			}
			
			repaint();
		}
		
		public void paintComponent(Graphics g) {				
			if(!clearMap) {
				super.paintComponent(g);
				g2D = (Graphics2D) g;
				
				// Draw the map as a background
				g2D.drawImage(IMG, 0, 0, MAP_WIDTH, MAP_HEIGHT, Color.BLACK, null);
				
				// Print the lines between the orders that represent the route
				int val = 0, prevVal = -1;
				for(int count = 0; count < route.currentlyFound; count++) {
					val = route.order[count];
					
					if(prevVal != -1 && count < route.currentlyFound) {
						g2D.setColor(Color.DARK_GRAY);
						g2D.setStroke(new BasicStroke(3));
						g2D.drawLine(route.address[val].latMapping(), route.address[val].lonMapping(), route.address[prevVal].latMapping(), route.address[prevVal].lonMapping());
					}
					prevVal = val;
				}
				
				// Print all the point points representing the address of an order. If it is the starting location, make a magenta dot, if not make it red
				for(int i = 0; i < route.getLocationCount(); i++) {
					if(i == 0) {
						g2D.setColor(Color.MAGENTA);
						g2D.setStroke(new BasicStroke(5));
						g2D.drawOval(route.address[i].latMapping() - POINT_SIZE, route.address[i].lonMapping() - POINT_SIZE, 2*POINT_SIZE, 2*POINT_SIZE);
					} else {
						g2D.setColor(Color.RED);
						g2D.setStroke(new BasicStroke(3));
						g2D.drawOval(route.address[i].latMapping() - POINT_SIZE/2, route.address[i].lonMapping() - POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
					}
					g2D.drawString(Integer.toString(i), route.address[i].latMapping() + 5, route.address[i].lonMapping() - 5);
				}
			} else {	// If the clearMap button is pressed, it resets the map to be empty
				super.paintComponent(g);
				clearMap = false;
				g2D = (Graphics2D) g;
				g2D.drawImage(IMG, 0, 0, MAP_WIDTH, MAP_HEIGHT, Color.BLACK, null);
			}
		}
		
		// Sets the clearMap function as true
		public void clearMap() {
			clearMap = true;
		}
	}
	
////////////////////////////// DATA STRUCTURE CLASS ///////////////////////////////////
	static class Location {
		private int orderNumber;	// Order Number
		private String name;		// Name of the location
		private int min;			// Time already waiting
		private double NCoords;		// North Coordinates
		private double WCoords;		// West Coordinates
		
		// Default constructor - Used for array initialization 
		Location() {
			orderNumber = 0;
			name = "";
			min = 0;
			NCoords = 0;
			WCoords = 0;
		}
		
		// Main Constructor	- Mainly used for implementing single objects
		Location(int orderNumber, String name, int min, double NCoords, double WCoords) {
			this.orderNumber = orderNumber;
			this.name = name;
			this.min = min;
			this.NCoords = NCoords;
			this.WCoords = WCoords;
		}
		
		// Main Constructor Overloader - For initializing using a String
		Location(String data) {
			String temp[] = data.split(",");
			this.orderNumber = Integer.parseInt(temp[0]);
			this.name = temp[1];
			this.min = Integer.parseInt(temp[2]);
			this.NCoords = Double.parseDouble(temp[3]);
			this.WCoords = Double.parseDouble(temp[4]);
		}
		
		// Method used for returning the distance between 2 addresses using Haversine formula
		public double getDistance(Location X) {
			double lat1 = Math.toRadians(NCoords);
			double lat2 = Math.toRadians(X.getNCoords());
			double lon1 = Math.toRadians(WCoords);
			double lon2 = Math.toRadians(X.getWCoords());
			
			return (double)((int)((EARTH_RADIUS * 2 * Math.asin(Math.sqrt(
						Math.pow(Math.sin((lat2 - lat1)/2), 2) +
						Math.cos(lat1) * Math.cos(lat2) *
						Math.pow(Math.sin((lon2 - lon1)/2), 2)
					)))*PRECISION)) / PRECISION;
		}
		
		// Getter methods
		public int getOrderNumber() {	return orderNumber;				}
		public double getWCoords() {	return WCoords;					}
		public double getNCoords() {	return NCoords;					}
		public int getMin() {			return min;						}	
		public String getName() {		return name;					}
		
		// Setter methods
		public void setWCoords(double WCoords) {this.WCoords = WCoords;	}
		public void setNCoords(double NCoords) {this.NCoords = NCoords;	}
		public void setMin(int min)	{			this.min = min;			}
		public void setName(String name) {		this.name = name;		}
		public void setOrderNumber(int oN) { 	this.orderNumber = oN;	}
		
		// Mapping methods (i.e Mapping a value between another set of coordinates as a percentage of the map)
		public int latMapping() {
			return (int)(((W_LIMIT - WCoords) / (W_LIMIT - E_LIMIT)) * MAP_WIDTH);
		}
		public int lonMapping() {
			return (int)(((N_LIMIT - NCoords) / (N_LIMIT - S_LIMIT)) * MAP_HEIGHT);
		}
	}
}