# Weather-Client
A client application that will connect to the National Weather Service web site using HTTP and XML and/or SOAP and display current weather conditions. Your client process will connect to the server over a socket connection and request weather information for a certain location. The National Weather Service specifies the location using latitude and longitude instead of zip code. The user should be able to enter coordinates into the client program and get a current update for that location. 

The Client module: A GUI is created using JFrame and swing components. The GUI consist of two text fields for taking the Latitude and the Longitude of the required location. A Button to connect to the National Weather service server and retrieves the weather information as an XML response. The XML data is then parsed to displays it in the Weather Information text area field in the Client GUI.
