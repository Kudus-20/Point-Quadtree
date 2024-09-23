import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2; 	// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	public int size = 0 ;

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */

	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */



	public void insert(E p2) {
		// TODO: YOUR CODE HERE


//			if ( point.getX()==p2.getX() && point.getY()==p2.getY()) {
//
//				// replace
//				this.point = p2;
//			}
		if(point!= null){
			if (point.getX() < p2.getX() && point.getY() > p2.getY()) {
				// insert on left (new leaf if no left)
				if (hasChild(1)) c1.insert(p2);
				else c1 = new PointQuadtree(p2, (int) point.getX(),y1, x2, (int)point.getY());
			}
			else if (point.getX() > p2.getX() && point.getY() > p2.getY()) {
				// insert on right (new leaf if no right)
				if (hasChild(2)) c2.insert(p2);
				else c2 = new PointQuadtree(p2, x1,y1, (int)point.getX(),(int)point.getY() ) ;
			}
			else if (point.getX() > p2.getX() && point.getY() < p2.getY()) {
				// insert on left (new leaf if no left)
				if (hasChild(3)) c3.insert(p2);
				else c3 = new PointQuadtree(p2, x1,(int) point.getY(), (int)point.getX(),y2);
			}
			else if (point.getX() < p2.getX() && point.getY() < p2.getY()) {
				// insert on right (new leaf if no right)
				if (hasChild(4)) c4.insert(p2);
				else c4 = new PointQuadtree(p2,(int) point.getX(), (int)point.getY(), x2,y2);
			}

			size +=1;

		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		return size;

	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		ArrayList<E> points = new ArrayList<>();
		addPoints(points);

		return points ;
	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE

		ArrayList<E> circles= new ArrayList<>();
		ArrayList<E> circleList = addCirc(cx,cy,cr, circles);

		return circleList;

	}


	public  void addPoints(ArrayList<E> pointlist){
		pointlist.add(point);
		if(hasChild(1)){
			c1.addPoints(pointlist);
		}
		if(hasChild(2)){
			c2.addPoints(pointlist);
		}
		if(hasChild(3)){
			c3.addPoints(pointlist);
		}
		if(hasChild(4)){
			c4.addPoints(pointlist);
		}
	}

	private ArrayList<E> addCirc(double cx, double cy, double cr, ArrayList<E> circList){
		if(Geometry.circleIntersectsRectangle(cx,cy,cr,x1,y1,x2,y2)){
			if(Geometry.pointInCircle(point.getX(),point.getY(), cx,cy, cr)){

				circList.add(point);
			}
			if(hasChild(1)){
				c1.addCirc(cx,cy,cr,circList);
			}
			if(hasChild(2)){
				c2.addCirc(cx,cy,cr,circList);
			}
			if(hasChild(3)){
				c3.addCirc(cx,cy,cr,circList);
			}
			if(hasChild(4)){
				c4.addCirc(cx,cy,cr,circList);
			}
		}
		return circList;

	}

	// TODO: YOUR CODE HERE for any helper methods.

}


