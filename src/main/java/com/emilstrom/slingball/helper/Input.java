package com.emilstrom.slingball.helper;

/**
 * Created by Emil on 2014-02-19.
 */
public class Input {
	public boolean pressed;
	public Vertex position;

	public Input(float xx, float yy, boolean p) {
		position = new Vertex(xx, yy);
		pressed = p;
	}
}
