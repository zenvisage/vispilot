package edu.uiuc.viz.lattice;
public class Tuple {
	public Integer x;
	public Integer y;
	public Tuple(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public String toString() {
	    return "[" +getX()+"-->"+getY()+"]";
	}
}
