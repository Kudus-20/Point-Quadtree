import java.awt.*;
import java.util.List;

import javax.swing.*;

/**
 * Driver for interacting with a quadtree:
 * inserting points, viewing the tree, and finding points near a mouse press
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for dots
 * @author CBK, Fall 2016, generics, dots, extended testing
 */
public class DotTreeGUI extends DrawingGUI {
	private static final int width=800, height=600;        // size of the universe
	private static final int dotRadius = 5;                // to draw dot, so it's visible
	private static final Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
	// to color different levels differently

	private PointQuadtree<Dot> tree = null;            // holds the dots
	private char mode = 'a';                        // 'a': adding; 'q': querying with the mouse
	private int mouseX, mouseY;                        // current mouse location, when querying
	private int mouseRadius = 10;                    // circle around mouse location, for querying
	private boolean trackMouse = false;                // if true, then print out where the mouse is as it moves
	private List<Dot> found = null;                    // who was found near mouse, when querying

	public DotTreeGUI() {
		super("dottree", width, height);
	}

	/**
	 * DrawingGUI method, here keeping track of the location and redrawing to show it
	 */
	@Override
	public void handleMouseMotion(int x, int y) {
		if (mode == 'q') {
			mouseX = x; mouseY = y;
			repaint();
		}
		if (trackMouse) {
			System.out.println(x+","+y);
		}
	}

	/**
	 * DrawingGUI method, here either adding a new point or querying near the mouse
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (mode == 'a') {
			// Add a new dot at the point
			// TODO: YOUR CODE HERE
			if(tree != null) {
				tree.insert(new Dot(x, y));
			}else{
				tree= new PointQuadtree<>(new Dot(x,y), 0,0,width, height);
			}

		} else if (mode == 'q') {
			// Set "found" to what tree says is near the mouse press
			// TODO: YOUR CODE HERE
			if(tree != null) {
				found = tree.findInCircle(x, y, mouseRadius);
			}
		} else {
			System.out.println("clicked at "+x+","+y);
		}
		repaint();
	}

	/**
	 * A simple testing procedure, making sure actual is expected, and printing a message if not
	 * @param x        query x coordinate
	 * @param y        query y coordinate
	 * @param r        query circle radius
	 * @param expectedCircleRectangle    how many times Geometry.circleIntersectsRectangle is expected to be called
	 * @param expectedInCircle            how many times Geometry.pointInCircle is expected to be called
	 * @param expectedHits                how many points are expected to be found
	 * @return  0 if passed; 1 if failed
	 */
	private int testFind(int x, int y, int r, int expectedCircleRectangle, int expectedInCircle, int expectedHits) {
		Geometry.resetNumInCircleTests();
		Geometry.resetNumCircleRectangleTests();
		int errs = 0;
		int num = tree.findInCircle(x, y, r).size();
		String which = "("+x+","+y+")@"+r;
		if (Geometry.getNumCircleRectangleTests() != expectedCircleRectangle) {
			errs++;
			System.err.println(which+": wrong # circle-rectangle, got "+Geometry.getNumCircleRectangleTests()+" but expected "+expectedCircleRectangle);
		}
		if (Geometry.getNumInCircleTests() != expectedInCircle) {
			errs++;
			System.err.println(which+": wrong # in circle, got "+Geometry.getNumInCircleTests()+" but expected "+expectedInCircle);
		}
		if (num != expectedHits) {
			errs++;
			System.err.println(which+": wrong # hits, got "+num+" but expected "+expectedHits);
		}
		return errs;
	}

	/**
	 * test tree 0 -- first three points from figure in handout
	 * hardcoded point locations for 800x600
	 */
	private void test0() {
		found = null;
		tree = new PointQuadtree<Dot>(new Dot(400,300), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450)); // B
		tree.insert(new Dot(250,550)); // C
		int bad = 0;
		bad += testFind(0,0,900,3,3,3);        // rect for all; circle for all; find all
		bad += testFind(400,300,10,3,2,1);    // rect for all; circle for A,B; find A
		bad += testFind(150,450,10,3,3,1);    // rect for all; circle for all; find B
		bad += testFind(250,550,10,3,3,1);    // rect for all; circle for all; find C
		bad += testFind(150,450,200,3,3,2);    // rect for all; circle for all; find B, C
		bad += testFind(140,440,10,3,2,0);    // rect for all; circle for A,B; find none
		bad += testFind(750,550,10,2,1,0);    // rect for A,B; circle for A; find none
		if (bad==0) System.out.println("test 0 passed!");
	}

	/**
	 * test tree 1 -- figure in handout
	 * hardcoded point locations for 800x600
	 */
	private void test1() {
		found = null;
		tree = new PointQuadtree<Dot>(new Dot(300,400), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450)); // B
		tree.insert(new Dot(250,550)); // C
		tree.insert(new Dot(450,200)); // D
		tree.insert(new Dot(200,250)); // E
		tree.insert(new Dot(350,175)); // F
		tree.insert(new Dot(500,125)); // G
		tree.insert(new Dot(475,250)); // H
		tree.insert(new Dot(525,225)); // I
		tree.insert(new Dot(490,215)); // J
		tree.insert(new Dot(700,550)); // K
		tree.insert(new Dot(310,410)); // L
		int bad = 0;
		bad += testFind(150,450,10,6,3,1);     // rect for A [D] [E] [B [C]] [K]; circle for A, B, C; find B
		bad += testFind(500,125,10,8,3,1);    // rect for A [D [G F H]] [E] [B] [K]; circle for A, D, G; find G
		bad += testFind(300,400,15,10,6,2);    // rect for A [D [G F H]] [E] [B [C]] [K [L]]; circle for A,D,E,B,K,L; find A,L
		bad += testFind(495,225,50,10,6,3);    // rect for A [D [G F H [I [J]]]] [E] [B] [K]; circle for A,D,G,H,I,J; find H,I,J
		bad += testFind(0,0,900,12,12,12);    // rect for all; circle for all; find all
		if (bad==0) System.out.println("test 1 passed!");
	}

	/**
	 * DrawingGUI method, here toggling the mode between 'a' and 'q'
	 * and increasing/decresing mouseRadius via +/-
	 */
	@Override
	public void handleKeyPress(char key) {
		if (key == 'a' || key=='q') mode = key;
		else if (key=='+') {
			mouseRadius += 10;
		} else if (key=='-') {
			mouseRadius -= 10;
			if (mouseRadius < 0) mouseRadius=0;
		} else if (key=='m') {
			trackMouse = !trackMouse;
		} else if (key=='0') {
			test0();
		} else if (key=='1') {
			test1();
		}

		// TODO: YOUR CODE HERE -- your test cases


		repaint();
	}

	/**
	 * DrawingGUI method, here drawing the quadtree
	 * and if in query mode, the mouse location and any found dots
	 */
	@Override
	public void draw(Graphics g) {
		if (tree != null) drawTree(g, tree, 0);
		if (mode == 'q') {
			g.setColor(Color.BLACK);
			g.drawOval(mouseX-mouseRadius, mouseY-mouseRadius, 2*mouseRadius, 2*mouseRadius);
			if (found != null) {
				g.setColor(Color.BLACK);
				for (Dot d : found) {
					g.fillOval((int)d.getX()-dotRadius, (int)d.getY()-dotRadius, 2*dotRadius, 2*dotRadius);
				}
			}
		}
	}

	/**
	 * Draws the dot tree
	 * @param g        the graphics object for drawing
	 * @param tree    a dot tree (not necessarily root)
	 * @param level    how far down from the root qt is (0 for root, 1 for its children, etc.)
	 */
	public void drawTree(Graphics g, PointQuadtree<Dot> tree, int level) {
		// Set the color for this level
		g.setColor(rainbow[level % rainbow.length]);
		// Draw this node's dot and lines through it
		// TODO: YOUR CODE HERE
		if (tree != null){
			g.fillOval((int)tree.getPoint().getX()-dotRadius, (int)tree.getPoint().getY()-dotRadius, 2*dotRadius, 2*dotRadius);
//			g.drawLine((int)tree.getPoint().getX(), tree.getY1(), tree.getX2(), (int)tree.getPoint().getY());

//vertical line
			g.drawLine((int)tree.getPoint().getX(), tree.getY1(),(int)tree.getPoint().getX() , tree.getY2());

//			horizontal
			g.drawLine( tree.getX1(),(int)tree.getPoint().getY(), tree.getX2(), (int)tree.getPoint().getY());

		}
		// Recurse with children

		for (int i = 1; i <=4; i ++){
			if(tree.hasChild(i)){
				drawTree(g, tree.getChild(i), level+1);
			}
		}
		// TODO: YOUR CODE HERE


	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DotTreeGUI();
			}
		});
	}
}
