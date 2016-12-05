package com.chry.interview.movie.controller;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chry.interview.movie.model.LatLng;
import com.chry.interview.movie.model.Movie;
import com.chry.interview.movie.store.DataStore;

@RestController
public class ServiceController {
	static Logger logger = LogManager.getLogger(ServiceController.class.getName());
	
	@RequestMapping(value="/movieTitles", method=RequestMethod.GET)
    public List<String> getMovieTitles() {
		try {
	    	logger.info("get all movie titles");
	    	return DataStore.getMovieTitles();
		} catch (Exception e) {
			logger.error("failed to handle the request", e);
			return null;
		}
    }

	@RequestMapping(value="/movies", method=RequestMethod.GET)
    public List<Movie> getMovies() {
		try {
	    	logger.info("get all movies");
	    	return DataStore.getMovies();
		} catch (Exception e) {
			logger.error("failed to handle the request", e);
			return null;
		}
    }
	/***
	 * 
	 * @param latLngStr  format: <lat>_<lng>, dot "." is replaced with "D" in URL
	 * @return
	 */
	@RequestMapping(value="/movies/latlngs/{latLngKey}", method=RequestMethod.GET)
    public List<Movie> getMoviesAtLatLng(@PathVariable String latLngKey) {
    	try {
	    	logger.info("get all movies at specific location");
			LatLng latLng = DataStore.getlatLng(latLngKey);
			return DataStore.getMovies(latLng);
    	} catch (Exception e) {
    		logger.error("failed to handle the location - " + latLngKey, e);
    		return null;
    	}
    }

	@RequestMapping(value="/latlngs", method=RequestMethod.GET)
    public List<LatLng> getLatLngs() {
    	try {
	    	logger.info("get all locations with movies");
	    	return DataStore.getLatlngs();
    	} catch (Exception e) {
    		logger.error("failed to get locations !", e);
    		return null;
    	}
    }

	@RequestMapping(value="/latlngs/movies/{titlePattern}", method=RequestMethod.GET)
    public List<LatLng> getLatLngOfMovie(@PathVariable String titlePattern) {
    	try {
	    	logger.info("get all locations shown in specific movies");
	    	return DataStore.searchLatlngs(titlePattern);
    	} catch (Exception e) {
    		logger.error("failed to handle the movie - " + titlePattern , e);
    		return null;
    	}
    }
}