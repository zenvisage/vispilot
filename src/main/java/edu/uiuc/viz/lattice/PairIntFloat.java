package edu.uiuc.viz.lattice;
public class PairIntFloat {
	public Integer x;
	public Float y;
	public PairIntFloat(Integer x, Float y) {
		this.x = x;
		this.y = y;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Float getY() {
		return y;
	}
	public void setY(Float y) {
		this.y = y;
	}
	public String toString() {
	    return "[" +getX()+"-->"+getY()+"]";
	}
}
