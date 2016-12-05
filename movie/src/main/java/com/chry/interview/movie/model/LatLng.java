package com.chry.interview.movie.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.chry.interview.movie.store.DataStore;

public class LatLng {
	private Double lat;
	private Double lng;
	
	public LatLng() {
		lat = 37.747837D;
		lng = -122.4442064D;
	}
	
	public LatLng(Double lat, Double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public LatLng(String locJsonStr) {
		JSONObject json = new JSONObject(locJsonStr);
		JSONArray results = json.getJSONArray("results");
		JSONObject item0 = results.getJSONObject(0);
		JSONObject geometry = item0.getJSONObject("geometry");
		JSONObject location = geometry.getJSONObject("location");
		lat = location.getDouble("lat");
		lng = location.getDouble("lng");
	}
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	
	public List<Movie> getMovies() {
		return DataStore.getMovies(this);
	}
	
	public String getKey() {
		return (lat + "_" + lng).replace('.', 'D');		//'.' can not be supported by rest path parameter
	}	
}
