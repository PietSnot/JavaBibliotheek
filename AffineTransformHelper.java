package javabibliotheek;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Piet
 */
public class AffineTransformHelper {
   public static void main(String... args) {
      int width = 500, height = 500;
      AffineTransform at = create(0, 0, width - 1, height - 1, -1, 1, 1, 2, false);
      System.out.println(at);
      System.out.println(pixelsize(at));
   }
 
       /**
     * This method creates an AffineTransform that will help to
     * translate between two coordinatesets. The coordinateset to which
     * the inputcoordinates should be translated, is specified by two
     * inputparameters: Point2D ToTopLeft and Point2D ToBottomRight.
     * Likewise, the coordinatesystem from which the coordinates should
     * be transformed are specified by two Point2D as well, and are called
     * FromTopLeft and FromBottomRight.
     * 
     * An example of its usage: say you have a JPanel with topleft 
     * coordinates (0, 0) and bottomright (400, 400).
     * 
     * And say that we want to use usercoordinates in a rectangle (0, 1) 
     * (topleft) and (2,0) bottomright.
     * 
     * Now, the aspectratio of both (height / width) differ: for the panel it
     * is Par = 1, and for the userarea it is Uar = 1 / 2 = .5.
     * If we do nothing about this, then a circle in userplane will become
     * an ellips in the panel. There is a fifth parameter that indicates
     * whether we should take this into account or not. This parameter
     * is the boolean 'keepAspectratio. If true, then a circle in userplane
     * will be a circle as well in the panel.
     * In 'main' an example is given
     * 
     * @param ToTopLeft a Point2D indicating the topleft point of the TO area
     * @param ToBottomRight a Point2D indicating the bottomleft point of the TO area
     * @param FromTopLeft a Point2D indicating the topleft point of the FROM area
     * @param FromBottomRight a Point2D indicating the bottomright point of the FROM area
     * @param keepAspectratio boolean. If true then a coorection will be made
     *                        in case of different aspectratios
     * @return an AffineTransform to do the coordinate change
     */
    public static AffineTransform create(
            Point2D ToTopLeft, Point2D ToBottomRight,
            Point2D FromTopLeft, Point2D FromBottomRight,
            boolean keepAspectratio
        ) {
        
        double TL = ToTopLeft.getX(),   TR = ToBottomRight.getX(), 
               TT = ToTopLeft.getY(),   TB = ToBottomRight.getY(),
               FL = FromTopLeft.getX(), FR = FromBottomRight.getX(),
               FT = FromTopLeft.getY(), FB = FromBottomRight.getY()
        ;
        
        if (keepAspectratio) {
            double Par = abs((TT - TB) / (TR - TL));
            double Uar = abs((FT - FB) / (FR - FL));
            if (Uar > Par) {
                // then increase FL and FR
                double lengthToBe = Uar / Par * abs(FR - FL);
                double correction = (lengthToBe - (FR - FL)) / 2;
                FR += correction;
                FL -= correction;
            }
            else {
                // increase FT and FB
                double lengthToBe = Par / Uar * abs(FT - FB);
                double correction = (lengthToBe - (FT - FB)) / 2;
                FT += correction;
                FB -= correction;
            }
        }
        double alpha = (TL - TR) / (FL - FR);
        double beta = 0, gamma = 0;
        double delta = (TT - TB) / (FT - FB);
        double e = TL - alpha * FL;
        double f = TB - delta * FB;
        return new AffineTransform(alpha, gamma, beta, delta, e, f);
    }
    
    /**
     * like the create constructor above, only the parameters are not points
     * but the individual coordinates of these points
     * @param toLeft          the left x coordinate of the TO area
     * @param toTop           the upper y coordinate of the TO area
     * @param toRight         the right x coordinate of the TO area
     * @param toBottom        the bottom y coordinate of the TO area
     * @param fromLeft        the left x coordinate of the FROM area
     * @param fromTop         the upper y coordinate of the FROM area
     * @param fromRight       the right x coordinate of the FROM area
     * @param fromBottom      the bottom y coordinate of the FROM area
     * @param keepAspectratio if true, takes care that TO and FROM have 
     *                        the same aspectratio
     * @return 
     */
    public static AffineTransform create( 
        double toLeft, double toTop, double toRight, double toBottom,
        double fromLeft, double fromTop, double fromRight, double fromBottom,
        boolean keepAspectratio
        ) {
        var toTopLeft = new Point2D.Double(toLeft, toTop);
        var toBottomRight = new Point2D.Double(toRight, toBottom);
        var fromTopLeft = new Point2D.Double(fromLeft, fromTop);
        var fromBottomRight = new Point2D.Double(fromRight, fromBottom);        
        return create(toTopLeft, toBottomRight, fromTopLeft, fromBottomRight, keepAspectratio);      
    }
    
    /**
    * gives the pixelsize of a unit after AffineTransform 'at' is applied
    * @param at the AffineTransform in question
    * @return doube number of pixels of one unit in 'at' coordinates
    */
    public static double pixelsize(AffineTransform at) {
        double scalex = Math.abs(at.getScaleX());
        double scaley = Math.abs(at.getScaleY());
        if (scalex != 0.0) return 1 / scalex;
        else if (scaley != 0.0) return 1 / scaley;
        else return 0;
    }
    
   /**
    * given an AffineTransform at, calculates another AffineTransform to be used
    * in deriving a font tha is suitable for the scaling in at.
    * Say, we have an at, and a font f. Now, the usage is:
    * AffineTrnansform atfont = AffineTransform.createFontTransform(at);
    * fontToUse = f.deriveFont(atfont);
    * and use fontToUse in your drawing routines
    * @return see above
    */
    public static AffineTransform createFontTransform(AffineTransform at) {
        double scaleX = at.getScaleX(), scaleY = at.getScaleY();
        return AffineTransform.getScaleInstance(1 / scaleX, 1 / scaleY);
    }

}

