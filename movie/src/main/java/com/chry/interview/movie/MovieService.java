package com.chry.interview.movie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.chry.interview.movie.loader.LoaderManager;
import com.chry.interview.movie.loader.MovieLoadTask;
import com.chry.util.http.HttpUtil;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MovieService {
	static Logger logger = LogManager.getLogger(MovieLoadTask.class.getName());
    public static void main(String[] args) {
    	HttpUtil.loadProxy();
    	MovieLoadTask task = new MovieLoadTask();
    	LoaderManager.startTask(task);
    	logger.info("Now, Service is up !");
    	SpringApplication.run(MovieService.class, args);
    }
}  
  