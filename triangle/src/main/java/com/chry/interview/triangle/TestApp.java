package com.chry.interview.triangle;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.chry.interview.triangle.Triangle.InvalidEdgeException;
import com.chry.interview.triangle.Triangle.MissEdgeException;

@Configuration
@ComponentScan
public class TestApp {

    @Bean
    ITriangleInput triangleInput() {
    	return new TriangleInputImpl();
    };
    
    public static void main(String[] args) {
	    ApplicationContext context = new AnnotationConfigApplicationContext(TestApp.class);
	    TriangleGenrator triangleGenrator = context.getBean(TriangleGenrator.class);
	    boolean done = false;
	    while(!done) {
		    try {
				Triangle triangle = triangleGenrator.create();
				if (triangle == null) {
					System.out.println("done");
					return;
				}
				System.out.println(triangle);
			} catch (IOException e) {
				System.err.println("Error:" + e.getMessage());
			} catch (MissEdgeException e) {
				System.err.println("Error:" + e.getMessage());
			} catch (InvalidEdgeException e) {
				System.err.println("Error:" + e.getMessage());
			}
	    }
    }
}