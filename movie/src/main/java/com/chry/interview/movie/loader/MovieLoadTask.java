package com.chry.interview.movie.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chry.interview.movie.model.Movie;
import com.chry.interview.movie.store.DataStore;
import com.chry.util.http.HttpUtil;

public class MovieLoadTask implements Runnable {
	private static Logger logger = LogManager.getLogger(MovieLoadTask.class.getName());
	
	public MovieLoadTask() {
	}
		
	@Override
	public void run() {
        try {
        	logger.info("start...");
        	String jsonMovies;
  		  	String sUrl = "http://data.sfgov.org/resource/wwmu-gmzc.json";
  		  	jsonMovies = HttpUtil.accessUrl(sUrl);
  		  	readJson2Movies(jsonMovies);
        } catch (Exception e) {
        	logger.warn("can not load movies !", e);
        } finally {
        	DataStore.setMovieLoadDone();
        	logger.info("done");
        }
	}	
	
	public static void readJson2Movies(String jsonStr) {
		JSONArray jsonArr = new JSONArray(jsonStr);
		for (int i=0; i<DataStore.MAX_LOCATION_COUNT && i<jsonArr.length(); i++) {
			JSONObject jsonMovie = jsonArr.getJSONObject(i);
			Movie movie = new Movie(jsonMovie);
			DataStore.addMovie(movie);
			LatLngReadTask task = new LatLngReadTask(movie);
			LoaderManager.startTask(task);
		}
	}
}
