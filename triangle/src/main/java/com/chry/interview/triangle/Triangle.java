package com.chry.interview.triangle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Triangle {
	public static Logger logger = LogManager.getLogger(Triangle.class.getName());
	/***
	 * Define two application level exceptions
	 */
	public static class InvalidEdgeException extends RuntimeException {
		InvalidEdgeException() {
			super("Invalid Triangle Edges !");
		};
	}

	public static class MissEdgeException extends RuntimeException {
		public int missedEdgeIndex;
		MissEdgeException(int index) {
			super("Some edge data is missed !");
			missedEdgeIndex = index;
		}
	}

    enum Type {equilateral, isosceles, scalene};				//define triangle type
	private Type _type;
	
	private int _a;
	private int _b;
	private int _c;
	
	public Triangle(int[] edges) {
		if (edges == null) {
			throw new MissEdgeException(0);
		}
		if (edges.length < 3) {
			throw new MissEdgeException(2);
		}
		_init(edges[0], edges[1], edges[2]);
	}
	
	private void _init(int a, int b, int c) {
		if (a <= 0) {
			throw new MissEdgeException(0);
		}
		if (b <= 0) {
			throw new MissEdgeException(1);
		}
		if (c <= 0) {
			throw new MissEdgeException(2);
		}
		if (a+b > c && a+c > b && b+c > a) {
			if(a==b && a==c) {
				_type = Type.equilateral;
			} else if (a==b || a==c || b==c) {
				_type = Type.isosceles;
			} else {
				_type = Type.scalene;
			}
			_a = a;
			_b = b;
			_c = c;
		} else {
			throw new InvalidEdgeException();
		}
	}
	
	public Type getType() {
		return _type;
	}
	
	public String toString() {
		return "Triangle(" + _a + "," + _b + "," + _c + ") -- " + _type;
	}
}
