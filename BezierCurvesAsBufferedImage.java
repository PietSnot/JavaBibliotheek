/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Sylvia
 */
public class BezierCurvesAsBufferedImage {
    
    //==============================================
    public static BufferedImage createDonut(
            int width, int height,
            double innerRadius, double outerRadius,
            Color startcol, Color endcol,
            double tryfactor
    ) {
        BufferedImage bufim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = AffineTransformHelper.create(0, 0, width - 1, height - 1, -1, 1, 1, -1, true);
        Path2D.Double p2dd = new Path2D.Double(Path2D.WIND_EVEN_ODD);
//      Path2D.Double p2dd = new Path2D.Double(Path2D.WIND_NON_ZERO);
        p2dd.moveTo(outerRadius, 0);
        p2dd.curveTo(outerRadius, outerRadius * tryfactor, outerRadius * tryfactor, outerRadius, 0, outerRadius);
        p2dd.curveTo(-outerRadius * tryfactor, outerRadius, -outerRadius, outerRadius * tryfactor, -outerRadius, 0);
        p2dd.curveTo(-outerRadius, -outerRadius * tryfactor, -outerRadius * tryfactor, -outerRadius, 0, -outerRadius);
        p2dd.curveTo(outerRadius * tryfactor, -outerRadius, outerRadius, -outerRadius * tryfactor, outerRadius, 0);
        p2dd.closePath();
        p2dd.moveTo(innerRadius, 0);
        p2dd.curveTo(innerRadius, innerRadius * tryfactor, innerRadius * tryfactor, innerRadius, 0, innerRadius);
        p2dd.curveTo(-innerRadius * tryfactor, innerRadius, -innerRadius, innerRadius * tryfactor, -innerRadius, 0);
        p2dd.curveTo(-innerRadius, -innerRadius * tryfactor, -innerRadius * tryfactor, -innerRadius, 0, -innerRadius);
        p2dd.curveTo(innerRadius * tryfactor, -innerRadius, innerRadius, -innerRadius * tryfactor, innerRadius, 0);
        p2dd.closePath();

        Graphics2D g2d = (Graphics2D) bufim.createGraphics();
        g2d.setTransform(at);
        GradientPaint gp = new GradientPaint(
                (float) -outerRadius, (float) outerRadius, startcol,
                (float) outerRadius, (float) -outerRadius, endcol
        );
        g2d.setPaint(gp);
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2d.fill(p2dd);
        g2d.dispose();
        return bufim;
    }
    
    //===============================================================
    public static BufferedImage createBezierInSquare(
        int width, int height, double scale, double curvingFactor, Color startcol, Color endcol) {
        BufferedImage bufim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int left = -1, top = 1, right = 1, bottom = -1;
        AffineTransform at = AffineTransformHelper.create(
                0, 0, width - 1, height - 1, left, top, right, bottom, true
        );
        Path2D p2d = new Path2D.Double();
        p2d.setWindingRule(Path2D.WIND_EVEN_ODD);
//        p2d.setWindingRule(Path2D.WIND_NON_ZERO);
        p2d.moveTo(left, top);
        p2d.lineTo(right, top);
        p2d.lineTo(right, bottom);
        p2d.lineTo(left, bottom);
        p2d.lineTo(left, top);
        p2d.closePath();
        double point = curvingFactor * scale;
        p2d.moveTo(-scale, 0);
        p2d.curveTo(
                -scale, point, 
                -point, scale, 
                0, scale
        );
        p2d.curveTo(
                point, scale, 
                scale, point, 
                scale, 0
        );
        p2d.curveTo(
                scale, -point, 
                point, -scale, 
                0, -scale
        );
        p2d.curveTo(
                -point, -scale, 
                -scale, -point, 
                -scale, -0
        );
        p2d.closePath();
        Graphics2D g2d = (Graphics2D) bufim.createGraphics();
        g2d.setTransform(at);
        GradientPaint gp = new GradientPaint(
                (float) -1, (float) 1, startcol,
                (float) 1, (float) -1, endcol
        );
        g2d.setPaint(gp);
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2d.fill(p2d);
        g2d.dispose();
        return bufim;
    }
}
