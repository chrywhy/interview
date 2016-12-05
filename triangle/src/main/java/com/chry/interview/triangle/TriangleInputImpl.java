package com.chry.interview.triangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chry.interview.triangle.Triangle.MissEdgeException;

/***
 * 
 * Define a input data format from System.in: each line has 3 integer numbers split by "," as length of triangle edges,
 * if more than 3 number, only the first three number is got
 * if there invalid edge found, skip and go to next number
 * for example: 
 * "3,4,5", "3,4,5,6"  -- got edge will be 3, 4, 5
 * "3,a,4,5", "3,-4,4,5"  -- got edge will be 3, 4, 5
 *  "" -- Miss Edge
 *  "3,4" -- Miss Edge
 *  "3.1,4,5" -- Miss Edge
 *  "3, -4, 5" or "3, a, 5"-- Miss Edge
 */ 
public class TriangleInputImpl implements ITriangleInput {
	public static Logger logger = LogManager.getLogger(TriangleInputImpl.class.getName());

	private  BufferedReader _br;
	private int _startPos = 0;
    public TriangleInputImpl() {
    	 _br = new BufferedReader(new InputStreamReader(System.in));
    }

    /***
     * read the edge length
     */
    private int readOneEdge(String edgeInput, int edgeIndex) {
		boolean valid;
		int endPos;
		int edgeLength = 0;
		valid = false;
		while (!valid && _startPos < edgeInput.length()) {
			endPos = edgeInput.indexOf(",", _startPos);
			if (endPos < 0) {
				endPos = edgeInput.length();
			}
			String edge;
			edge = edgeInput.substring(_startPos, endPos);
			try {
				edgeLength = Integer.parseInt(edge);
				if (edgeLength > 0) {
					valid = true;
				}
			} catch (NumberFormatException e) {
				logger.warn("found invalid edge data, skip & ingnore");
			}
			_startPos = endPos + 1;
		}
		
		if (valid) {
			return edgeLength;
		}
		throw new MissEdgeException(edgeIndex);
    }
    
    public int[] readTriangleEdges() throws IOException {
    	int[] edges = {0,0,0};
    	_startPos = 0;
    	System.out.print("Input triangle edges:");
    	String edgeInput = _br.readLine();
    	if (edgeInput == null || edgeInput.trim().isEmpty()) { 			//end of input stream
    		return null;
    	}
		for (int edgeIndex=0; edgeIndex<3; edgeIndex++) {
			edges[edgeIndex] = readOneEdge(edgeInput, edgeIndex);
		}
		return edges;
	}
}
