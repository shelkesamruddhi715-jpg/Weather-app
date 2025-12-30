package com.mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class WeatherServlet
 */
@WebServlet("/WeatherServlet")
public class WeatherServlet extends HttpServlet {
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
//	api setup
		
	String apikey="c1cc97b02c0da09cec7ca189bed1eaea";
	String city=request.getParameter("city");
	System.out.println("user input: "+city);
	String apiurl =
		    "https://api.openweathermap.org/data/2.5/weather?q="
		    + URLEncoder.encode(city, "UTF-8")
		    + "&appid="
		    + apikey
		    + "&units=metric";
	
	URL url=new URL(apiurl);
	HttpURLConnection connection =(HttpURLConnection)url.openConnection();
	connection.setRequestMethod("GET");
//	reading the data from network
	InputStream input=connection.getInputStream();
	InputStreamReader reader=new InputStreamReader(input);
	
//	to store the string
	StringBuilder responsecontent=new StringBuilder();
	Scanner sc=new Scanner(reader);
	while(sc.hasNext()) {
		responsecontent.append(sc.nextLine());
	}
	sc.close();
	
	 Gson gson = new Gson();
     JsonObject jsonObject = gson.fromJson(responsecontent.toString(), JsonObject.class);
	System.out.println(jsonObject);
	long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
    String date = new Date(dateTimestamp).toString();
    
    //Temperature
    double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
    int temperatureCelsius = (int) (temperatureKelvin - 273.15);
   
    //Humidity
    int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
    
    //Wind Speed
    double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
    
    //Weather Condition
    String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").toString();
    
    request.setAttribute("date", date);
    request.setAttribute("city", city);
    request.setAttribute("temperature", temperatureCelsius);
    request.setAttribute("weatherCondition", weatherCondition); 
    request.setAttribute("humidity", humidity);    
    request.setAttribute("windSpeed", windSpeed);
    request.setAttribute("weatherData", responsecontent.toString());
    
    connection.disconnect();
    request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
