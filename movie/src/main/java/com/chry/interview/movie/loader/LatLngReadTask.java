package com.chry.interview.movie.loader;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chry.interview.movie.model.LatLng;
import com.chry.interview.movie.model.Movie;
import com.chry.interview.movie.store.DataStore;
import com.chry.util.http.HttpUtil;

public class LatLngReadTask implements Runnable {
	static Logger logger = LogManager.getLogger(LatLngReadTask.class.getName());
	
	private Movie movie;
	
	public LatLngReadTask(Movie movie) {
		this.movie = movie;
	}
		
	@Override
	public void run() {
		String sUrl = "";
		int tryagain = 2;
		String street = DataStore.getStreet(movie.getLocations());
		while(tryagain > 0 && !DataStore.isLocationLoaded(movie.getLocations())) {
			tryagain--;
	        try {
	        	logger.debug("start...");
	        	String locJsonStr;
		  		sUrl = "https://maps.google.com/maps/api/geocode/json?address='" 
		  					+ URLEncoder.encode(street, "utf-8")
		  					+ "'&key=AIzaSyCOqVUyLlCpGullYCayNlpJEPFGxcrsV10";
	//	  					+ "'&language=zh-CN&sensor=false&key=AIzaSyDTCFArv3QUQJhI3MijeEeXgLa6rsEsMgs";
		  		locJsonStr = HttpUtil.accessUrl(sUrl);
		  		
		  		LatLng latLng = new LatLng(locJsonStr);
		  		DataStore.setMovieLatLng(movie, latLng);
		        DataStore.increaseLatlngReadyMovieCount();
		  		logger.debug("found location: " + movie.getLocations() + " - " + latLng.getKey());
		  		break;
	        } catch (Exception e) {
	        	if (tryagain == 0) {
		        	logger.warn("cannot find location: " + movie.getLocations() + ", sUrl=" + sUrl);
		      		DataStore.setMovieLatLng(movie);
			        DataStore.increaseLatlngReadyMovieCount();
	        	} else if(street != null && !"".equals(street)){
	        		for(int i=0;i<street.length();i++){
	    	        	street = street.substring(i);
	    	        	break;
	        		}
	        	}
	//        	System.exit(0);
	        }
		}
    	logger.debug("done");
	}	
}
