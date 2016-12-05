package com.chry.interview.movie.loader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoaderManager {
	static Logger logger = LogManager.getLogger(LoaderManager.class.getName());
	private static  ExecutorService executor = Executors.newFixedThreadPool(20);

	public static void startTask(LatLngReadTask task) {
		executor.submit(task);
	}
	
	public static void shutdown() {
		executor.shutdown();
	}

	public static void startTask(MovieLoadTask task) {
		executor.submit(task);
	}
}
