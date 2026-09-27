/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javabibliotheek;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Piet
 */
public class PietsQuadTree {
    private Rectangle2D boundary;
    private int capacity = 4;
    private List<Point2D> points = new ArrayList<>();
    private PietsQuadTree nw, sw, ne, se;
    boolean divided = false;
    
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------
    
    PietsQuadTree() {
        boundary = new Rectangle2D.Double(0, 0, 1000, 1000);
    }
    
    PietsQuadTree(double x, double y, double w, double h) {
        boundary = new Rectangle2D.Double(x, y, w, h);
    }
    
    PietsQuadTree(double x, double y, double w, double h, int c) {
        this(x, y, w, h);
        this.capacity = c;
    }
    
    PietsQuadTree(Rectangle2D rect) {
        this(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), 4);
    }
    
    PietsQuadTree(int capacity) {
        this();
        this.capacity = capacity;
    }

    //--------------------------------------------------------------------------
    // public methods
    //--------------------------------------------------------------------------
    
    public boolean add(Point2D p) {
        if (!boundary.contains(p)) return false;
        if (points.size() < capacity) return points.add(p);
        divide();
        return nw.add(p) || sw.add(p) || ne.add(p) || se.add(p);
    }
    
    public int count() {
        var result = points.size();
        if (divided) result += nw.count() + sw.count() + ne.count() + se.count();
        return result;
    }
    
    public List<Point2D> query(Rectangle2D r) {
        var result = new ArrayList<Point2D>();
        if (!boundary.intersects(r)) return result;
        for (var p: points) if (r.contains(p)) result.add(p);
        if (!divided) return result;
        result.addAll(nw.query(r));
        result.addAll(sw.query(r));
        result.addAll(ne.query(r));
        result.addAll(se.query(r));
        return result;
    }
    
    public void draw(Graphics g) {
        var g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLUE);
        for (var p: points) g2d.draw(e2d(p));
        if (divided) {
            g2d.setColor(Color.black);
            var x = boundary.getX();
            var y = boundary.getY();
            var w = boundary.getWidth();
            var h = boundary.getHeight();
            g2d.draw(l2d(x + w/2, y, x + w/2, y + h));
            g2d.draw(l2d(x, y + h/2, x + w, y + h/2));  
            nw.draw(g);
            sw.draw(g);
            ne.draw(g);
            se.draw(g);
        }
        g2d.dispose();
    }
    
    @Override
    public String toString() {
        var result = """
                     boundary:
                         x: %f
                         y: %f
                         w: %f
                         h: %f
                     points:
                        %s
                     divided: %b
                     count: % d
                     """
            .formatted(boundary.getX(),
                       boundary.getY(),
                       boundary.getWidth(),
                       boundary.getHeight(),
                       points,
                       divided,
                       count()
                       )
        ;
        return result;
    }
    
    private void divide() {
        var x = boundary.getX();
        var y = boundary.getY();
        var w = boundary.getWidth();
        var h = boundary.getHeight();
        nw = new PietsQuadTree(x, y, w/2, h/2, capacity);
        sw = new PietsQuadTree(x, y + h/2, w/2, h/2, capacity);
        ne = new PietsQuadTree(x + w/2, y, w/2, h/2, capacity);
        se = new PietsQuadTree(x + w/2, y + h/2, w/2, h/2, capacity);
        divided = true;
    }
    
    private Point2D p2d(double x, double y) {
        return new Point2D.Double(x, y);
    }
    
    private Ellipse2D e2d(Point2D p) {
        var x = p.getX() - 2;
        var y = p.getY() - 2;
        return new Ellipse2D.Double(x, y, 4, 4);
    } 
    
    private Line2D l2d(double xs, double ys, double xe, double ye) {
        return new Line2D.Double(xs, ys, xe, ye);
    }
    
    private Ellipse2D e2d(double x, double y) {
        return e2d(p2d(x, y));
    }
}
