package javabibliotheek;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Integer.min;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import static java.util.stream.Collectors.toMap;
import java.util.stream.DoubleStream;
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
public class Grafiek extends JPanel {
    
    private Graaf graphPanel;
    private Map<FunctionNameColor, Path2D> map;
    private FunctionNameColor currentFNC;
    private double currentX;
    public static Map<Integer, BigInteger> fac = new HashMap<>();
    private static int MaxFac = 50;
    
    static {
        fac.put(0, BigInteger.ONE);
        for (int i = 1; i <= MaxFac ; i++) fac.put(i, BigInteger.valueOf(i).multiply(fac.get(i - 1)));
    }
    
    public static void main(String... args) throws NoninvertibleTransformException {
        
//        var panel = builder
//                .withPanelWidth(500)
//                .withUserTopLeft(-2, 2)
//                .withUserBottomRight(2, -2)
//                .withFunctionNameColor(x -> pow(x, 2), "f", Color.RED)
//                .withFunctionNameColor(x -> x, "g", Color.MAGENTA)
//                .withFunctionNameColor(x -> pow(x, 5) - 3 * pow(x, 3.0/2), "h", Color.BLUE)
//                .withGraphStartX(-2)
//                .withGraphEndX(2)
//                .build()
//        ;

        Function<Double, Double> onoff = x -> abs(Math.floor(x / PI)) % 2 == 0 ? 0d : 1d;
        var fs = FourierSeries.getInstance()
                .withConstant(1)
                .withSinTerm((x, n) -> n % 2 == 0 ? 0d : sin(n * x) * -2 / (n * PI))
        ;

        var start = PI;
        var builder = new Grafiek.Builder();
        var panel = builder
                .withPanelWidth(800)
                .withUserTopLeft(-start, 1.5)
                .withUserBottomRight(start, -1.5)
                .withFunctionNameColor(onoff, "on/off", Color.MAGENTA)
                .withFunctionNameColor(x -> fs.apply(x, 0), "0", Color.GREEN)  // 0 means no sin/cos terms
                .withFunctionNameColor(x -> fs.apply(x, 1), "1", Color.RED)    // constant + one sin and one cos term
                .withFunctionNameColor(x -> fs.apply(x, 5), "5", Color.BLUE)
                .withFunctionNameColor(x -> fs.apply(x, 50), "50", Color.BLACK)
                .withGraphStartX(-start)
                .withGraphEndX(start)
                .build()
        ;

            var f = new JFrame("grafiek");
            f.setContentPane(panel);
            f.pack();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);         
        }

    private Grafiek(int panelWidth, 
                    Point2D userTopLeft, Point2D userBottomRight,
                    double graphStartX, double graphEndX, 
                    List<FunctionNameColor> list) throws NoninvertibleTransformException {
        
        this.currentX = (graphEndX + graphStartX) / 2;
        this.setLayout(new BorderLayout());
        graphPanel = new Graaf(panelWidth,
                              userTopLeft, userBottomRight,
                              graphStartX, graphEndX,
                              list)
        ;
        
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
        
    private static double fac(int n) {
        return fac.get(min(n, MaxFac)).doubleValue();
    }
    
    //*****************************************************************
    
    private class Graaf extends JPanel {
        private final BufferedImage buf; 
        private final int panelWidth;
        private final int panelHeight;
        private final Point2D userTopLeft;
        private final Point2D userBottomRight;
        private final double graphStartX;
        private final double graphEndX;
        private final AffineTransform fromUserToPanel;
        private final AffineTransform fromPanelToUser;
        double[] fromPanelToUserMatrix = new double[6];
        private final Path2D xAxis;
        private final Path2D yAxis;
        private double ellipseRadius;
        private double pixelsize;
        private double currentMouseX;
        
        public Graaf(int panelWidth, 
                    Point2D userTopLeft, Point2D userBottomRight,
                    double graphStartX, double graphEndX, 
                    List<FunctionNameColor> list) throws NoninvertibleTransformException {
            
            this.panelWidth = panelWidth;
            var deltaY = userBottomRight.getY() - userTopLeft.getY();
            var deltaX = userBottomRight.getX() - userTopLeft.getX();
            this.panelHeight = (int) (panelWidth * abs(deltaY / deltaX));
            buf = new BufferedImage(this.panelWidth, this.panelHeight, BufferedImage.TYPE_INT_ARGB);
            this.userTopLeft = userTopLeft;
            this.userBottomRight = userBottomRight;
            this.graphStartX = graphStartX;
            this.graphEndX = graphEndX;
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
            
            // creating the Path2D's that represent the functions
            int start = (int) fromUserToPanel.transform(new Point2D.Double(graphStartX, 0), null).getX();
            int end = (int) fromUserToPanel.transform(new Point2D.Double(graphEndX, 0), null).getX();
            var nrOfPoints = end - start + 1;
            map = list.stream().collect(toMap(f -> f, f -> f.createPath(graphStartX, graphEndX, nrOfPoints)));
            currentFNC = list.get(0);
            
            // create buf
            setBuf();
            
            // add mouseadapter
            var adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent m) {
                    currentMouseX = m.getX();
                }
                public void mouseDragged(MouseEvent m) {
                    var x = m.getX();
                    currentX += (x - currentMouseX) * pixelsize;
                    currentMouseX = x;
                    System.out.format("(%.3f, %.3f)%n", currentX, currentFNC.f().apply(currentX));
                    repaint();
                }
            };
            this.addMouseListener(adapter);
            this.addMouseMotionListener(adapter);
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
            map.entrySet().stream().forEach(f ->  {
                g2d.setColor(f.getKey().color()); 
                g2d.draw(f.getValue());
            });
            g2d.setColor(currentFNC.color());
            g2d.draw(map.get(currentFNC));
            g2d.dispose();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(buf, 0, 0, null);
            var g2d = (Graphics2D) g;
            g2d.setTransform(fromUserToPanel);
            var y = currentFNC.f().apply(currentX);
            g2d.setColor(Color.BLACK);
            var ellips = new Ellipse2D.Double(currentX - ellipseRadius, y - ellipseRadius, ellipseRadius * 2, ellipseRadius * 2);
            g2d.fill(ellips);
            g2d.dispose();
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
        private Point2D topLeft = new Point2D.Double(0,0);
        private Point2D bottomRight = new Point2D.Double(panelWidth, panelWidth);
        private double graphStartX = 0;
        private double graphEndX = panelWidth;
        private Function<Double, Double> function = Function.identity();
        private Color color = Color.BLUE;
        private String name = "f";
        private List<FunctionNameColor> list = new ArrayList<>();
    
        public Builder withPanelWidth(int w) {
            panelWidth = w;
            return this;
        }
        
        public Builder withUserTopLeft(Point2D p) {
            topLeft = p;
            return this;
        }
        
        public Builder withUserTopLeft(double x, double y) {
            return withUserTopLeft(new Point2D.Double(x, y));
        }
        
        public Builder withUserBottomRight(Point2D p) {
            this.bottomRight = p;
            return this;
        }
        
        public Builder withUserBottomRight(double x, double y) {
            return withUserBottomRight(new Point2D.Double(x, y));
        }
        
        public Builder withGraphStartX(double x) {
            graphStartX = x;
            return this;
        }
        
        public Builder withGraphEndX(double x) {
            graphEndX = x;
            return this;
        }
        
        public Builder withFunctionNameColor(Function<Double, Double> f, String name, Color color) {
            list.add(new FunctionNameColor(f, name, color));
            return this;
        }
        
        public Grafiek build() throws NoninvertibleTransformException {
            return new Grafiek(this.panelWidth,
                               this.topLeft, this.bottomRight,
                               this.graphStartX, this.graphEndX,
                               this.list
                              )
            ;
        }
    }
    
    //***************************************************************
    
    private record FunctionNameColor(Function<Double, Double> f, String name, Color color) {
        public Path2D createPath(double xmin, double xmax, int nrOfPoints) {
            var result = new Path2D.Double();
            result.moveTo(xmin, f.apply(xmin));
            for (int i = 1; i <= nrOfPoints; i++) {
                var x = (xmin * (nrOfPoints - i) + xmax * i) / nrOfPoints;
                result.lineTo(x, f.apply(x));
            }
            return result;
        }
    }
    
    //*******************************************************************
    
    private final static class FourierSeries {
    
        private double constant = 0;
        private Mouski cosTerm = (x, n) -> 0d;
        private Mouski sinTerm = (x, n) -> 0d;
        
        private FourierSeries() {}
        
        public static FourierSeries getInstance() {
            return new FourierSeries();
        }
        
        public FourierSeries withConstant(double constant) {
            this.constant = constant;
            return this;
        }
        
        public FourierSeries withCosTerm(Mouski cosTerm) {
            this.cosTerm = cosTerm;
            return this;
        }
        
        public FourierSeries withSinTerm(Mouski sinterm) {
            this.sinTerm = sinterm;
            return this;
        }
        
        public double apply(double x, int nrOfCosSinTerms) {
            var result = IntStream.rangeClosed(1, nrOfCosSinTerms)
                    .boxed()
                    .flatMapToDouble(i -> DoubleStream.of(cosTerm.apply(x, i), sinTerm.apply(x, i)))
                    .sum()
                    + constant / 2
            ;
            return result;
        }
        
        private static interface Mouski extends BiFunction<Double, Integer, Double> {}
    }
}
