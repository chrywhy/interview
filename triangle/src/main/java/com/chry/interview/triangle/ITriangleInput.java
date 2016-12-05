package com.chry.interview.triangle;

import java.io.IOException;

/***
 * Triangle data might come from different data source, 
 * Application shall implement this interface according to different data source, 
 */
public interface ITriangleInput {
	public int[] readTriangleEdges() throws IOException;
}
