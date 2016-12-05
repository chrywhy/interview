package com.chry.interview.movie.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chry.interview.movie.loader.LoaderManager;
import com.chry.interview.movie.model.LatLng;
import com.chry.interview.movie.model.Movie;

public class DataStore {
	static Logger logger = LogManager.getLogger(DataStore.class.getName());
	public static final int MAX_LOCATION_COUNT = 2000;

	private static Map<String, Movie> movies = new HashMap<String, Movie>(); //only one thread write this
	private static Map<String, Set<Movie>> latLng2movies = new ConcurrentHashMap<String, Set<Movie>>();
	private static Map<String, Set<LatLng>> title2LatLngs = new ConcurrentHashMap<String, Set<LatLng>>();
	private static Map<String, LatLng> loc2LatLng = new ConcurrentHashMap<String, LatLng>();
	private static Map<String, String> latLng2Loc = new ConcurrentHashMap<String, String>();
	
	private static boolean movieLoadDone = false;
	private static AtomicInteger latlngReadyMovieCount = new AtomicInteger(0);
	
	private static LatLng initailLatLng = new LatLng();
	private static LatLng defaultLatLng = new LatLng();
	
	public static void addMovie(Movie movie) {
		movies.put(movie.getTitle(), movie);
		loc2LatLng.put(movie.getLocations(), initailLatLng);
		logger.info("movie loaded : " + movie.getTitle());
	}
	
	public static boolean isLocationLoaded(String location) {
		return loc2LatLng.get(location) != initailLatLng;
	}
	
	public static void setMovieLatLng(Movie movie) {
		setMovieLatLng(movie, defaultLatLng);
	}
	
	public static void setMovieLatLng(Movie movie, LatLng latLng) {
		String key = latLng.getKey();
		Set<Movie> movies = latLng2movies.get(key);
		if (movies == null) {
			movies = Collections.synchronizedSet(new HashSet<Movie>());
			latLng2movies.put(key, movies);
		}
		movies.add(movie);
		Set<LatLng> latlngs = title2LatLngs.get(movie.getTitle());
		if (latlngs == null) {
			latlngs = Collections.synchronizedSet(new HashSet<LatLng>());
			title2LatLngs.put(movie.getTitle(), latlngs);
		}
		latlngs.add(latLng);
		loc2LatLng.put(movie.getLocations(), latLng);
		latLng2Loc.put(key, movie.getLocations());
	}
	
	public static void setMovieLoadDone() {
		movieLoadDone = true;
		logger.info("movie load done : " + movies.size());
	}
	
	public static void increaseLatlngReadyMovieCount() {
		int c = latlngReadyMovieCount.incrementAndGet();
		logger.info("ready LatLng : " + c + "/" + loc2LatLng.size());
		if (isLatlngReadDone()) {
			logger.info("All movies' LatLng data is ready now.");
			LoaderManager.shutdown();
		}
	}

	public static LatLng getLocation(LatLng latLng) {
		return loc2LatLng.get(latLng.getKey());
	}
	
	public static List<Movie> getMovies() {
		List<Movie> movieList = new ArrayList<Movie>();
		Collection<Movie> c = movies.values();
		if (c != null) {
			movieList.addAll(c);
		}
		return movieList;
	}
	
	public static List<Movie> getMovies(LatLng latLng) {
		List<Movie> movieList = new ArrayList<Movie>();
		Set<Movie> s = latLng2movies.get(latLng.getKey());
		if (s != null) {
			movieList.addAll(s);
		}
		return movieList;
	}
	
	public static List<String> getMovieTitles() {
		List<String> titles = new ArrayList<String>();
		Set<String> s = movies.keySet();
		if (s != null) {
			titles.addAll(s);
		}
		return titles;
	}

	public static LatLng getlatLng(String latLngKey) {
		latLngKey = latLngKey.replace('D', '.');
    	int pos = latLngKey.indexOf("_");
    	Double lat = Double.parseDouble(latLngKey.substring(0, pos));
    	Double lng = Double.parseDouble(latLngKey.substring(pos+1));
		return new LatLng(lat, lng);
	}
	
	public static List<LatLng> getLatlngs() {
		List<LatLng> latlngs = new ArrayList<LatLng>();
		Set<String> keys = latLng2movies.keySet();
		for (String key : keys) {
			if (!key.equalsIgnoreCase(defaultLatLng.getKey())) {
				LatLng latLng = getlatLng(key);
				latlngs.add(latLng);
			}
		}
		return latlngs;
	}

	public static List<LatLng> searchLatlngs(String pattern) {
		Set<LatLng> latlngs = new HashSet<LatLng>();
		pattern = pattern.toLowerCase();
		for (String title : title2LatLngs.keySet()) {
			if (title.toLowerCase().indexOf(pattern) >= 0) {
				Set<LatLng> s = title2LatLngs.get(title);
				if (s != null) {
					latlngs.addAll(s);
				}
			}
		}
		List<LatLng> latlngList = new ArrayList<LatLng>();
		latlngList.addAll(latlngs);
		return latlngList;
	}

	public static List<LatLng> getLatlngs(String title) {
		List<LatLng> latlngs = new ArrayList<LatLng>();
		Set<LatLng> s = title2LatLngs.get(title);
		if (s != null) {
			latlngs.addAll(s);
		}
		return latlngs;
	}
	
	public static boolean isMovieLoadDone() {
		return movieLoadDone;
	}
	
	public static boolean isLatlngReadDone() {
		int c = latlngReadyMovieCount.get();		
		return movieLoadDone && (c == loc2LatLng.size() || c >= MAX_LOCATION_COUNT);
	}

	public static String getStreet(String locations) {
		String street;
		int pos = locations.indexOf(",");
		if (pos > 0) {
			street = locations.substring(pos + 1);
			return street;
		} else {
			pos = locations.indexOf("(");
			int pos2 = locations.indexOf(")");
			if (pos2 > pos) {
				street = locations.substring(pos+1, pos2);
				return street;
			} else {
				pos = locations.indexOf("at ");
				if (pos > 0) {
					street = locations.substring(pos+1);
					return street;
				}
			}
		}
		return locations;
	}
}
