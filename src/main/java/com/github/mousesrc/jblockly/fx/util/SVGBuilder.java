package com.github.mousesrc.jblockly.fx.util;

public class SVGBuilder {
	
	private final StringBuilder sb = new StringBuilder();
	
	public SVGBuilder moveTo(double x, double y) {
		sb.append('M').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder m(double x, double y) {
		return moveTo(x, y);
	}
	
	public SVGBuilder moveTo(int x, int y) {
		sb.append('M').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder m(int x, int y) {
		return moveTo(x, y);
	}
	
	public SVGBuilder lineTo(double x, double y) {
		sb.append('L').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder l(double x, double y) {
		return lineTo(x, y);
	}
	
	public SVGBuilder lineTo(int x, int y) {
		sb.append('L').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder l(int x, int y) {
		return lineTo(x, y);
	}
	
	public SVGBuilder horizontal(double x) {
		sb.append('H').append(x);
		return this;
	}
	
	public SVGBuilder h(double x) {
		return horizontal(x);
	}
	
	public SVGBuilder horizontal(int x) {
		sb.append('H').append(x);
		return this;
	}
	
	public SVGBuilder h(int x) {
		return horizontal(x);
	}
	
	public SVGBuilder vertical(double y) {
		sb.append('V').append(y);
		return this;
	}
	
	public SVGBuilder v(double y) {
		return vertical(y);
	}
	
	public SVGBuilder vertical(int y) {
		sb.append('V').append(y);
		return this;
	}
	
	public SVGBuilder v(int y) {
		return vertical(y);
	}
	
	public SVGBuilder curve(double x1, double y1, double x2, double y2, double x, double y) {
		sb.append('C').append(x1).append(',').append(y1).append(',')
				.append(x2).append(',').append(y2).append(',')
				.append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder c(double x1, double y1, double x2, double y2, double x, double y) {
		return curve(x1, y1, x2, y2, x, y);
	}
	
	public SVGBuilder curve(int x1, int y1, int x2, int y2, int x, int y) {
		sb.append('C').append(x1).append(',').append(y1).append(',')
				.append(x2).append(',').append(y2).append(',')
				.append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder c(int x1, int y1, int x2, int y2, int x, int y) {
		return curve(x1, y1, x2, y2, x, y);
	}
	
	public SVGBuilder smoothCurve(double x1, double y1, double x, double y) {
		sb.append('S').append(x1).append(',').append(y1).append(',').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder s(double x1, double y1, double x, double y) {
		return smoothCurve(x1, y1, x, y);
	}
	
	public SVGBuilder smoothCurve(int x1, int y1, int x, int y) {
		sb.append('S').append(x1).append(',').append(y1).append(',').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder s(int x1, int y1, int x, int y) {
		return smoothCurve(x1, y1, x, y);
	}

	public SVGBuilder quadraticBelzierCurve(double x1, double y1, double x, double y) {
		sb.append('Q').append(x1).append(',').append(y1).append(',').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder q(double x1, double y1, double x, double y) {
		return quadraticBelzierCurve(x1, y1, x, y);
	}
	
	public SVGBuilder quadraticBelzierCurve(int x1, int y1, int x, int y) {
		sb.append('Q').append(x1).append(',').append(y1).append(',').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder q(int x1, int y1, int x, int y) {
		return quadraticBelzierCurve(x1, y1, x, y);
	}
	
	public SVGBuilder smoothQuadraticBelzierCurve(double x, double y) {
		sb.append('T').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder t(double x, double y) {
		return smoothQuadraticBelzierCurve(x, y);
	}
	
	public SVGBuilder smoothQuadraticBelzierCurve(int x, int y) {
		sb.append('T').append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder t(int x, int y) {
		return smoothQuadraticBelzierCurve(x, y);
	}
	
	public SVGBuilder ellipticalArc(double rx, double ry, double rotationAngle, boolean isLargeArc, boolean sweepDirection, double x, double y) {
		sb.append('A').append(rx).append(',').append(ry).append(',').append(rotationAngle).append(',')
				.append(isLargeArc ? 1 : 0).append(',').append(sweepDirection ? 1 : 0).append(',')
				.append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder a(double rx, double ry, double rotationAngle, boolean isLargeArc, boolean sweepDirection, double x, double y) {
		return ellipticalArc(rx, ry, rotationAngle, isLargeArc, sweepDirection, x, y);
	}
	
	public SVGBuilder ellipticalArc(int rx, int ry, int rotationAngle, boolean isLargeArc, boolean sweepDirection, int x, int y) {
		sb.append('A').append(rx).append(',').append(ry).append(',').append(rotationAngle).append(',')
				.append(isLargeArc ? 1 : 0).append(',').append(sweepDirection ? 1 : 0).append(',')
				.append(x).append(',').append(y);
		return this;
	}
	
	public SVGBuilder a(int rx, int ry, int rotationAngle, boolean isLargeArc, boolean sweepDirection, int x, int y) {
		return ellipticalArc(rx, ry, rotationAngle, isLargeArc, sweepDirection, x, y);
	}
	
	public SVGBuilder closePath() {
		sb.append('Z');
		return this;
	}
	
	public SVGBuilder z() {
		return closePath();
	}
	
	public SVGBuilder clear() {
		sb.delete(0, sb.length());
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
