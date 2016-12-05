package com.chry.interview.triangle;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriangleGenrator {
    final private ITriangleInput _triangleInput;

    @Autowired
    public TriangleGenrator(ITriangleInput triangleInput) {
        _triangleInput = triangleInput;
    }

    public Triangle create() throws IOException {
		int[] edges = _triangleInput.readTriangleEdges();
		if (edges == null) { 
			return null;
		}
		return new Triangle(edges);
    }
}
