package com.emilstrom.slingball.helper;

/**
 * Created bg Emil on 2014-02-20.
 */
public class Color {
	public double r, g, b, a;
	public Color() {
		r = 0;
		g = 0;
		b = 0;
		a = 1f;
	}

	public Color(Color c) {
		copy(c);
	}

	public Color(double rr, double gg, double bb) {
		r = rr;
		g = gg;
		b = bb;
		a = 1f;
	}

	public Color(double rr, double gg, double bb, double aa) {
		r = rr;
		g = gg;
		b = bb;
		a = aa;
	}

	public Color(int rr, int gg, int bb, int aa) {
		r = (double)rr / 255.0;
		g = (double)gg / 255.0;
		b = (double)bb / 255.0;
		a = (double)aa / 255.0;
	}

	public void add(Color v) {
		r += v.r;
		b += v.b;
		g += v.g;
		a += v.a;
	}
	public void subtract(Color v) {
		r -= v.r;
		b -= v.b;
		g -= v.g;
		a -= v.a;
	}
	public void multiply(Color v) {
		r *= v.r;
		b *= v.b;
		g *= v.g;
		a *= v.a;
	}

	public Color plus(Color v) {
		return Color.add(this, v);
	}
	public Color minus(Color v) {
		return Color.subtract(this, v);
	}
	public Color times(Color v) {
		return Color.multiply(this, v);
	}

	public void copy(Color c) {
		r = c.r;
		g = c.g;
		b = c.b;
		a = c.a;
	}

	public void copy(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void confine() {
		if (r > 1.0) r = 1.0;
		if (g > 1.0) g = 1.0;
		if (b > 1.0) b = 1.0;
		if (a > 1.0) a = 1.0;
	}

	public static Color add(Color c1, Color c2) {
		return new Color(c1.r + c2.r, c1.g + c2.g, c1.b + c2.b, c1.a + c2.a);
	}

	public static Color subtract(Color c1, Color c2) {
		return new Color(c1.r - c2.r, c1.g - c2.g, c1.b - c2.b, c1.a - c2.a);
	}

	public static Color multiply(Color c1, Color c2) {
		return new Color(c1.r * c2.r, c1.g * c2.g, c1.b * c2.b, c1.a * c2.a);
	}

	public static Color blend(Color c1, Color c2, double v) {
		double r = c1.r + (c2.r - c1.r)*v,
				g = c1.g + (c2.g - c1.g)*v,
				b = c1.b + (c2.b - c1.b)*v,
				a = c1.a + (c2.a - c1.a)*v;

		return new Color(r, g, b, a);
	}
}