package javabibliotheek;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Integer.min;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import static javabibliotheek.AffineTransformHelper.create;
import static javabibliotheek.AffineTransformHelper.pixelsize;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Piet
 */
public class Grafiek_t_xy extends JPanel {
    
    private Graaf graphPanel;
    private Map<FunctionNameColor, Path2D> map = new HashMap<>();
    private FunctionNameColor currentFNC;
    
    public static void main(String... args) throws NoninvertibleTransformException {
        
        Function<Double, Point2D> arsp = t -> new Point2D.Double(t * cos(t), t * sin(t));
        //Function<Double, Point2D> fermat = t -> new Point2D.Double(t * cos(t * t), t * sin(t * t));
        Function<Double, Point2D> souris = t -> new Point2D.Double(t * cos(t), t * sin(t / 2));
        Function<Double, Point2D> vwo2025 = t -> new Point2D.Double(3 * sin(t) * (cos(t) - 1), 3 * cos(t));
        var builder = new Grafiek_t_xy.Builder();
        var panel = builder
                .withPanelWidth(800)
                .withStartT(0)
                .withEndt(8 * PI)
                .withNrOfPoints(1000)
                .withFunctionNameColor(arsp, "arch. spiral", Color.BLUE)
                //.withFunctionNameColor(fermat, "fermat", Color.RED)
                .withFunctionNameColor(souris, "souris", Color.black)
                .withFunctionNameColor(vwo2025, "vwo 2025", Color.RED)
                .build()
        ;

        var f = new JFrame("grafiek");
        f.setContentPane(panel);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);         
    }

    private Grafiek_t_xy(int panelWidth, double tmin, double tmax, int nrOfPoints,
                    List<FunctionNameColor> list) throws NoninvertibleTransformException {
        
        this.setLayout(new BorderLayout());
        graphPanel = new Graaf(panelWidth, tmin, tmax, nrOfPoints, list);
        
        var buttonPanel = new JPanel(new GridLayout(0, 3));
        for (var key: map.keySet()) {
            var button = new JButton(key.name);
            button.setForeground(key.color());
            button.addActionListener(ae -> {
                currentFNC = key; 
                graphPanel.changeFNC();}
            );
            buttonPanel.add(button);
        }
        var snapshot = new JButton("snapshot");
        snapshot.addActionListener(ae -> processSnapshot());
        buttonPanel.add(snapshot);
        
        this.add(graphPanel, BorderLayout.PAGE_START);
        this.add(buttonPanel, BorderLayout.PAGE_END);
        }
    
        private void processSnapshot() {
            graphPanel.saveGraphs();
    }
        
    //*****************************************************************
    
    private class Graaf extends JPanel {
        private final BufferedImage buf; 
        private final int panelWidth;
        private final int panelHeight;
        private Point2D userTopLeft;
        private Point2D userBottomRight;
        private final double tmin;
        private final double tmax;
        private final AffineTransform fromUserToPanel;
        private final AffineTransform fromPanelToUser;
        double[] fromPanelToUserMatrix = new double[6];
        private final Path2D xAxis;
        private final Path2D yAxis;
        private double ellipseRadius;
        private double pixelsize;
        
        public Graaf(int panelWidth, 
                    double tmin, double tmax, int nrOfPoints, 
                    List<FunctionNameColor> list) throws NoninvertibleTransformException {
            
            this.panelWidth = panelWidth;
            this.tmin = tmin;
            this.tmax = tmax;
            determineTopLeftAndBottomRight(nrOfPoints, list);
            currentFNC = list.getFirst();
            
            var deltaY = userBottomRight.getY() - userTopLeft.getY();
            var deltaX = userBottomRight.getX() - userTopLeft.getX();
            this.panelHeight = (int) (panelWidth * abs(deltaY / deltaX));
            
            buf = new BufferedImage(this.panelWidth, this.panelHeight, BufferedImage.TYPE_INT_ARGB);
            
            fromUserToPanel = create(new Point2D.Double(0, 0), new Point2D.Double(panelWidth - 1, panelHeight - 1),
                                 userTopLeft, userBottomRight, true
            );
            fromPanelToUser = fromUserToPanel.createInverse();
            fromPanelToUser.getMatrix(fromPanelToUserMatrix);
            pixelsize = (float) pixelsize(fromUserToPanel);
            ellipseRadius = pixelsize * 5;
            
            // cresating x and y axis
            xAxis = new Path2D.Double();
            xAxis.moveTo(userTopLeft.getX(), 0);
            xAxis.lineTo(userBottomRight.getX(), 0);
            yAxis = new Path2D.Double();
            yAxis.moveTo(0, userTopLeft.getY());
            yAxis.lineTo(0, userBottomRight.getY());
            
            // create buf
            setBuf();
            
            // add mouseadapter
//            var adapter = new MouseAdapter() {
//                public void mousePressed(MouseEvent m) {
//                    currentMouseX = m.getX();
//                }
//                public void mouseDragged(MouseEvent m) {
//                    var x = m.getX();
//                    currentX += (x - currentMouseX) * pixelsize;
//                    currentMouseX = x;
//                    System.out.format("(%.3f, %.3f)%n", currentX, currentFNC.f().apply(currentX));
//                    repaint();
//                }
//            };
//            this.addMouseListener(adapter);
//            this.addMouseMotionListener(adapter);
        }
        
        private void determineTopLeftAndBottomRight(int nrOfPoints, List<FunctionNameColor> list) {
            
            var tCollection = IntStream.rangeClosed(0, nrOfPoints)
                .mapToDouble(i -> (tmin * (nrOfPoints - i) + tmax * i) / nrOfPoints)
                .boxed()
                .toList()
            ;
            
            var xmin = Double.MAX_VALUE; 
            var ymin = Double.MAX_VALUE;
            var xmax = Double.MIN_VALUE;
            var ymax = Double.MIN_VALUE;
            
            for (var f: list) {
                var path = new Path2D.Double();
                var first = true;
                for (var t: tCollection) {
                    var p = f.f().apply(t);
                    if (first) {
                        path.moveTo(p.getX(), p.getY());
                        first = false;
                    }
                    else path.lineTo(p.getX(), p.getY());
                    xmin = Double.min(xmin, p.getX());
                    ymin = Double.min(ymin, p.getY());
                    xmax = Double.max(xmax, p.getX());
                    ymax = Double.max(ymax, p.getY());
                }
                map.put(f, path);
            }
            userTopLeft = new Point2D.Double(xmin, ymax);
            userBottomRight = new Point2D.Double(xmax, ymin);
        }
        
        public void changeFNC() {
            setBuf();
            repaint();
        }
        
        private void setBuf() {
            var g2d = buf.createGraphics();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, this.panelWidth, this.panelHeight);
            g2d.setTransform(fromUserToPanel);
            var pixelsize = (float) pixelsize(fromUserToPanel);
            g2d.setStroke(new BasicStroke(1.0f * pixelsize));
            g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.draw(xAxis);
            g2d.draw(yAxis);
            g2d.setStroke(new BasicStroke(2f * pixelsize));
            map.entrySet().forEach(e -> {g2d.setColor(e.getKey().color());g2d.draw(e.getValue());});
            g2d.setColor(currentFNC.color());
            g2d.draw(map.get(currentFNC));
            g2d.dispose();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(buf, 0, 0, null);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(this.panelWidth, this.panelHeight);
        }
        
        private void saveGraphs() {
            var file = new File("G:/TestMap/grafiek.png");
            try{
                ImageIO.write(buf, "png", file);
            }
            catch (IOException e) {
                // we hoeven niks te doen
            }
        }
    }
 
    //********************************************************    
    
    public static class Builder {
        // all given default values
        private int panelWidth = 400;
        private double graphStartT = 0;
        private double graphEndT = 100;
        private Function<Double, Point2D> function = d -> new Point2D.Double(d, d);
        private Color color = Color.BLUE;
        private String name = "f";
        private int nrOfPoints = 100;
        private List<FunctionNameColor> list = new ArrayList<>();
    
        public Builder withPanelWidth(int w) {
            panelWidth = w;
            return this;
        }
        
        public Builder withStartT(double t) {
            graphStartT = t;
            return this;
        }
        
        public Builder withEndt(double t) {
            graphEndT = t;
            return this;
        }
        
        public Builder withFunctionNameColor(Function<Double, Point2D> f, String name, Color color) {
            list.add(new FunctionNameColor(f, name, color));
            return this;
        }
        
        public Builder withNrOfPoints(int n) {
            this.nrOfPoints = n;
            return this;
        }
        
        public Grafiek_t_xy build() throws NoninvertibleTransformException {
            return new Grafiek_t_xy(this.panelWidth,
                               this.graphStartT, this.graphEndT,
                               this.nrOfPoints, this.list
                              )
            ;
        }
    }
    
    //***************************************************************
    
    private record FunctionNameColor(Function<Double, Point2D> f, String name, Color color) {
        public Path2D createPath(double tmin, double tmax, int nrOfPoints) {
            var result = new Path2D.Double();
            var start = f.apply(tmin);
            result.moveTo(start.getX(), start.getY());
            for (int i = 1; i <= nrOfPoints; i++) {
                var t = (tmin * (nrOfPoints - i) + tmax * i) / nrOfPoints;
                var p = f.apply(t);
                result.lineTo(p.getX(), p.getY());
            }
            return result;
        }
    }
    
    //*******************************************************************
}
