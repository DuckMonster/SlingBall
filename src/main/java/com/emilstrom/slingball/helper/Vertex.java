package com.emilstrom.slingball.helper;

import com.emilstrom.slingball.GameMath;

/**
 * Created by Emil on 2014-02-20.
 */
public class Vertex {
	public double x, y;
	public Vertex() {
		x = 0;
		y = 0;
	}
	public Vertex(double xx, double yy) {
		x = xx;
		y = yy;
	}
	public Vertex(Vertex v) {
		x = v.x;
		y = v.y;
	}

	public void add(Vertex v) {
		x += v.x;
		y += v.y;
	}
	public void subtract(Vertex v) {
		x -= v.x;
		y -= v.y;
	}
	public void multiply(Vertex v) {
		x *= v.x;
		y *= v.y;
	}
	public void multiply(double d) {
		x *= d;
		y *= d;
	}

	public Vertex plus(Vertex v) {
		return Vertex.add(this, v);
	}
	public Vertex minus(Vertex v) {
		return Vertex.subtract(this, v);
	}
	public Vertex times(Vertex v) {
		return Vertex.multiply(this, v);
	}
	public Vertex times(double d) {
		return Vertex.multiply(this, d);
	}

	public void copy(Vertex v) { x = v.x; y = v.y; }

	public double getLength() { return getLength(this); }
	public double getDirection() { return getDirection(this); }

	public boolean compare(Vertex v) { return (x == v.x && y == v.y); }

	public static Vertex add(Vertex a, Vertex b) {
		return new Vertex(a.x + b.x, a.y + b.y);
	}

	public static Vertex subtract(Vertex a, Vertex b) {
		return new Vertex(a.x - b.x, a.y - b.y);
	}

	public static Vertex multiply(Vertex a, Vertex b) {
		return new Vertex(a.x * b.x, a.x * b.y);
	}

	public static Vertex multiply(Vertex a, double d) {
		return new Vertex(a.x * d, a.y * d);
	}

	public static Vertex normalize(Vertex v) {
		double l = Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
		return new Vertex(v.x / l, v.y / l);
	}

	public static Vertex getDirectionVertex(Vertex a, Vertex b) {
		return normalize(subtract(b, a));
	}

	public static double getLength(Vertex a, Vertex b) {
		Vertex v = subtract(a, b);

		return v.getLength();
	}

	public static double getDirection(Vertex a, Vertex b) {
		return GameMath.getDirection(a.x, a.y, b.x, b.y);
	}

	public static double getLength(Vertex v) {
		return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
	}

	public static double getDirection(Vertex v) {
		return GameMath.getDirection(v.x, v.y, 0, 0);
	}
}