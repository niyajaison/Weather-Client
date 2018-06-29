package weatherCient;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.JTextArea;

import java.awt.GridLayout;


/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * The client process will connect to the server over a socket connection and request weather information for a certain location and disconnect after retrieveing the information.
 * The class will give the UI creation , HTTP connection and display the Output.
 * References:	
 * 1. Connecting to a server via HTTP: http://www.baeldung.com/java-http-request
 * 2. URL - https://graphical.weather.gov/xml/rest.php
 * 3. Adding Image into a JFrame : https://stackoverflow.com/questions/32045075/how-to-display-my-image-from-a-url-into-my-jlabel-jframe
 * 4. Parsing of XML response: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 */

public class WeatherClient extends JFrame {

	
	private static final long serialVersionUID = 1L;

	public Map<String,String> weatherInfo ;/**The map for saving the data to print it later*/

	/**Components for UI creation*/
	private JLabel weatherLb;
	private JLabel latLb;
	private Component verticalStrut;
	private Component verticalStrut_1;
	public static JTextArea weatherTxA;
	public JTextField latTxF;
	private JLabel logLb;
	public JTextField logTxF;
	public JLabel imageIconLbl;
	private JButton connectbtn;

	private Component verticalStrut_2;
	private Component verticalStrut_3;
	private Component verticalStrut_4;
	private Component verticalStrut_5;

	/**
	 * Author: Niya Jaison | UTA ID : 1001562701 
	 * The constructor is used for creating the UI of the Weather Client.
	 * Includes: Initializing the member variables for UI creation, adding the components to the frame
	 *           adding listeners to the required components
	 */

	WeatherClient(){
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		Box box = Box.createVerticalBox();
		getContentPane().add(box);

		latLb = new JLabel("Enter Latitude Value");
		latLb.setHorizontalAlignment(SwingConstants.LEFT);
		//stNameLb.setEnabled(false);
		latLb.setForeground(Color.BLUE);
		box.add(latLb);

		latTxF = new JTextField();
		box.add(latTxF);
		latTxF.setColumns(10);

		verticalStrut = Box.createVerticalStrut(5);
		box.add(verticalStrut);

		logLb = new JLabel("Enter Longitude Value");
		logLb.setHorizontalAlignment(SwingConstants.LEFT);
		logLb.setForeground(Color.BLUE);
		box.add(logLb);

		logTxF = new JTextField();
		logTxF.setColumns(10);
		box.add(logTxF);

		verticalStrut_2 = Box.createVerticalStrut(5);
		box.add(verticalStrut_2);

		connectbtn = new JButton("Weather Forecast");
		connectbtn.setAlignmentY(Component.TOP_ALIGNMENT);
		box.add(connectbtn);

		verticalStrut_3 = Box.createVerticalStrut(10);
		box.add(verticalStrut_3);

		weatherLb = new JLabel("Weather Information");
		weatherLb.setHorizontalAlignment(SwingConstants.LEFT);
		weatherLb.setForeground(Color.BLUE);
		box.add(weatherLb);
		
		verticalStrut_5 = Box.createVerticalStrut(10);
		box.add(verticalStrut_5);
		imageIconLbl=new JLabel();
		imageIconLbl.setAlignmentY(Component.TOP_ALIGNMENT);
		box.add(imageIconLbl);

		verticalStrut_4 = Box.createVerticalStrut(5);
		box.add(verticalStrut_4);

		weatherTxA = new JTextArea();
		weatherTxA.setTabSize(5);
		box.add(weatherTxA);
		weatherTxA.setRows(10);
		//weatherTxA.setTabSize(10);
		weatherTxA.setColumns(35);
		weatherTxA.setVisible(false);

		verticalStrut_1 = Box.createVerticalStrut(5);
		box.add(verticalStrut_1);
		weatherLb.setVisible(false);

		/**Adding an action listener to the button to call user defined method to connect to the server and to display the output in the UI*/
		connectbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				weatherInfo=new TreeMap();//<>();

				if(latTxF.getText().isEmpty()||logTxF.getText().isEmpty()) {/**Display the error message is latitude or longitude is not entered*/
					imageIconLbl.setText("");
					imageIconLbl.setIcon(null);
					weatherLb.setVisible(false);
					weatherTxA.setVisible(false);
					
					imageIconLbl.setText("Please Enter both Latitude and Longitude before proceeding");
					pack();
				}
				else {
					imageIconLbl.setText("");
					getWeatherInfo();/**Calling the user defined function to connect to the server and retrieve the data.*/
					displayWeather();/**Calling the user defined function to display the information retrieved from the server*/
				}
			}
		});
		setTitle("Weather Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		//setBounds(0, 0, 1000, 400);

	}
	
	/**
	 * Author: Niya Jaison | UTA ID : 1001562701 
	 * The function is used to connect to the National Weather Service and retrieve the information about the specified points */
	public void getWeatherInfo() {
		URL noaaAPIURL;
		try {

			/**Creating a URL object for the HTTP connection*/
			noaaAPIURL=new URL("https://graphical.weather.gov/xml/sample_products/browser_interface/ndfdXMLclient.php?whichClient=GmlLatLonList&gmlListLatLon="+latTxF.getText()+"%2C"+logTxF.getText()+"&featureType=Forecast_Gml2Point&product=time-series&begin=2004-01-01T00%3A00%3A00&end=2021-12-02T00%3A00%3A00&Unit=e&Submit=Submit");
			/**Using a HttpURLConnection to open a connection to the specified server*/
			HttpURLConnection APIconnect = (HttpURLConnection) noaaAPIURL.openConnection();
			APIconnect.setRequestMethod("GET");

			/**Below line of code retrieves the data by parsing the XML response received from the server.*/
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(noaaAPIURL.openStream());
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("gml:featureMember");
			if(nList.getLength()==0) {
				weatherInfo.put("Error", "No data found for the point");
			}
			else {
				Node nNode = nList.item(0);

				Element eElement = (Element) nNode;
				
				/**The below line of codes get each of the weather parameter using Element.getElementsByTagName() function and store it into a Map(Tree map)*/
				
				weatherInfo.put("Maximum Temperature", eElement.getElementsByTagName("app:maximumTemperature").item(0).getTextContent()+" F");//getTextContent());//getTextContent());
				weatherInfo.put("Minimum Temperature",eElement.getElementsByTagName("app:minimumTemperature").item(0).getTextContent()+" F");
				weatherInfo.put("Dewpoint Temperature",eElement.getElementsByTagName("app:dewpointTemperature").item(0).getTextContent()+" F");
				weatherInfo.put("Probability Of Precipitation(12hourly)",eElement.getElementsByTagName("app:probOfPrecip12hourly").item(0).getTextContent());
				weatherInfo.put("Wind Speed",eElement.getElementsByTagName("app:windSpeed").item(0).getTextContent());
				weatherInfo.put("Wind Gust",eElement.getElementsByTagName("app:windGust").item(0).getTextContent());
				weatherInfo.put("Wind Direction",eElement.getElementsByTagName("app:windDirection").item(0).getTextContent());
				weatherInfo.put("Wave Height",eElement.getElementsByTagName("app:waveHeight").item(0).getTextContent());
				weatherInfo.put("Weather Icon",eElement.getElementsByTagName("app:weatherIcon").item(0).getTextContent());
				weatherInfo.put("Sky Cover", eElement.getElementsByTagName("app:skyCover").item(0).getTextContent());
				weatherInfo.put("Relative Humidity", eElement.getElementsByTagName("app:relativeHumidity").item(0).getTextContent());
				
				/**Disconnecting the HTTP connection after data is retrieved.*/
				APIconnect.disconnect(); 
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	/**
	 * Author: Niya Jaison | UTA ID : 1001562701 
	 * The function is used to display the weather information retrieved from National Weather Service
	 */
	public void displayWeather() {
		/**The below line of codes initially enables the output part of the weather client*/
		weatherLb.setVisible(true);
		weatherTxA.setVisible(true);
		weatherTxA.setText("");
		imageIconLbl.setIcon(null);
		/**The below advanced For loop iterates over the map and displays all the retrieved information in to the UI using append()*/
		for(String key:weatherInfo.keySet()) {
			weatherTxA.append("- "+key+" :"+weatherInfo.get(key)+"\n");
		}
		/**The below lines of code is for adding the weather icon obtained in the XML response into the JFrame
		 * Reference: https://stackoverflow.com/questions/32045075/how-to-display-my-image-from-a-url-into-my-jlabel-jframe*/
		try {
			if(weatherInfo.keySet().contains("Weather Icon")) {
				URL imageURL = new URL(weatherInfo.get("Weather Icon"));
				BufferedImage imageIcon = ImageIO.read(imageURL);
				imageIconLbl.setIcon(new ImageIcon(imageIcon));
				imageIconLbl.setAlignmentX(0);
			}	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setBounds(0, 0, 1000, 600);
		//pack();
	}
/**
 * Author: Niya Jaison | UTA ID : 1001562701 
 * The main() method craetes a new instance for the class */
	public static void main(String[] args) {
		new WeatherClient();
	}
}
