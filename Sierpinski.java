/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Piet
 */
public class Sierpinski {
    
    private class Triangle {
        final Point2D x, y, z;
        final Color color;
        final Path2D p;
        
        Triangle(Point2D x, Point2D y, Point2D z, Color c) {
            this.x = x;
            this.y = y;
            this.z = z;
            color = c;
            p = new Path2D.Double();
            p.moveTo(x.getX(), x.getY());
            p.lineTo(y.getX(), y.getY());
            p.lineTo(z.getX(), z.getY());
            p.closePath();
        }
        
//        public List<Triangle> getSubTriangles() {
//            
//        }
        
        
        
        public void draw(Graphics g) {
            var g2d = (Graphics2D) g.create();
            g2d.getColor();
            g2d.fill(p);
        }
    }
    
//    private static Point2D getMid(Point2D p, Point2D q) {
//        return p.add(p).multiply(.5);
//    }
}
