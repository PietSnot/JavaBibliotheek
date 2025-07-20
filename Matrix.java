package javabibliotheek;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 *
 * @author Piet
 */
public class Matrix {
    
    public final double[][] x;
    
    public static void main(String... args) {
        var matrix = Matrix.of(new double[] {1, 2, 3});
        matrix.print();
        
        System.out.println("*************    Starting large Matrix inverse ****************************");
        
        long start, end;
        
        for (int i = 2; i <= 50; i++) {
            var m = Matrix.identity(i);
            start = System.currentTimeMillis();
            var det = m.determinant();
            end = System.currentTimeMillis();
            System.out.format("size: %d: determinant: %f  duurde: %.2f seconds%n", i, det, (end - start) / 1000.);
            start = System.currentTimeMillis();
            m.inverse();
            end = System.currentTimeMillis();
            System.out.format("size: %d: %.2f seconds%n", i, (end - start) / 1000.);
            System.out.println("******************************");
        }
        var m = Matrix.of(3, 3, 2, 0, 0, 0, 3, 0, 2, 0, 5);
        var n = m.inverse();
        m.print();
        System.out.println("***************");
        n.print();
        System.out.println("***************");
        System.out.println(n.determinant());
        System.out.println("***************");
        m.multiply(n).print();
        System.out.println("****************************");
        m = Matrix.of(3, 3, 0, 0, 1, 0, 1, 0, 1, 0, 0);
        m.print();
        System.out.println("***************");
        n = m.inverse();
        n.print();
    }
    
    private static Matrix generate(int size) {
        double[][] large = IntStream.rangeClosed(1, size)
            .mapToObj(i -> IntStream.rangeClosed(1, size).mapToDouble(j -> j).toArray())
            .toArray(double[][]::new)
        ;
        return Matrix.of(large);
    }
    
    
    //********************************************************************
    // Constructors
    //********************************************************************
    
    /**
     * @param x 2D array of double, must be rectangular
     * @return the corresponding Matrix
     */
    public static Matrix of(double[][] x) {
        return new Matrix(x);
    }
    
    //-----------------------------------------------------------
    /**
     * @param x 2D array of ints, must be rectangular
     * @return the corresponding Matrix
     */
    public static Matrix of(int[][] x) {
        return Matrix.of(convertToDouble(x));
    }
    
    //-----------------------------------------------------------
    /**
     * @param x a 2D array of longs, must be rectangular
     * @return the corresponding Matrix
     */
    public static Matrix of(long[][] x) {
        return Matrix.of(convertToDouble(x));
    }
    
    //-----------------------------------------------------------
    /**
     * @param <S> class that must extend Number
     * @param <U> class that extends Collection<S>
     * @param <T> class that extends Collection<U>
     * @param x parameter of type T
     * @return the corresponding Matrix
     * 
     * For instance:
     * x = List.of(List.of(1, 2), List.of(2, 3));
     * 
     * Beware: you can use a Set, but then the order of the elements might be
     * completely different than as specified in the Set! There's no ordering
     * in a Set...
     */
    public static 
        <S extends Number, U extends Collection<S>, T extends Collection<U>>
        Matrix of(T x) {
        return Matrix.of(convertToDouble(x));
    }
        
    //-----------------------------------------------------------
    /**
     * @param x a double[] array
     * @return the corresponding Matrix of size 1 by x.length
     */
    public static Matrix of(double[] x) {
        return Matrix.of(new double[][]{x});     
    }
    
    //-----------------------------------------------------------
    /**
     * @param x array of ints
     * @return the corresponding Matrix, of size 1 by x.length
     */
    public static Matrix of(int[] x) {
        return Matrix.of(new int[][]{x});
    }
    
    //-----------------------------------------------------------
    /**
     * @param x array of longs
     * @return the corresponding Matrix, of size 1 by x.length.
     */
    public static Matrix of(long[] x) {
        return Matrix.of(new long[][] {x});
    }
    
    //-----------------------------------------------------------
    /**
     * @param list a List of things that extend Number
     * @return the corresponding Matrix of size 1 by list.size()
     */
    public static Matrix of(List<? extends Number> list) {
        return Matrix.of(list.stream().mapToDouble(i -> i.doubleValue()).toArray());
    }
        
    //-----------------------------------------------------------
    /**
     * @param rows an int specifying the number of rows
     * @param columns an int specifying the number of columns
     * @param doubles a series of values, where the first 'columns' of numbers
     * are the first row, et cetera.
     * @return the corresponding Matrix of size rows x columns
     * 
     * precondition: rows * columns must be equal to the number of the following values
     */
    public static Matrix of(int rows, int columns, double... doubles) {
        if (rows * columns != doubles.length) throw new RuntimeException
            ("rows and columns don't match number of doubles!!!");
        double[][] result = IntStream.range(0, rows)
            .mapToObj(i -> IntStream.range(0, columns)
                               .mapToDouble(j -> doubles[i * columns + j])
                               .toArray())
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //********************************************************************
    // public (static) methods
    //********************************************************************
    /**
     * @param size int, specifying the size of the Matrix
     * @return the identity matrix of size size by size
     */
    public static Matrix identity(int size) {
        double[][] d = IntStream.range(0, size)
            .mapToObj(i -> rowOfIdentity(size, i))
            .toArray(double[][]::new)
        ;
        return Matrix.of(d);
    }
    
    //-----------------------------------------------------------
    /**
     * @return the number of rows of this Matrix
     */
    public int getRowdimension() {
        return x.length;
    }
    
    //-----------------------------------------------------------
    /**
     * @return the number of columns of this Matrix
     */
    public int getColumndimension() {
        return x[0].length;
    }
        
    //-----------------------------------------------------------
    /**
     * @param row the rownumber, 0 <= rownumber < number of rows
     * @return the row as a double[] array
     */
    public double[] getRow(int row) {
        if (row < 0 || row >= getRowdimension()) throw new RuntimeException("row index out of bound!");
        return Arrays.copyOf(x[row], x[row].length);
    }
    
    //-----------------------------------------------------------
    /**
     * @param column the column number, 0 <= column nember < number of columns
     * @return the column. as double[] array
     */
    public double[] getColumn(int column) {
        if (column < 0 || column >= getColumndimension()) throw new RuntimeException(
            "Wrong column index");
        return Arrays.stream(x).mapToDouble(row -> row[column]).toArray();
    }
    
    //-----------------------------------------------------------
    /**
     * @return true this Matrix is rectaglular, flase otherwise
     */
    public boolean isSquare() {
        return x.length == x[0].length;
    }
    
    //-----------------------------------------------------------
    /**
     * @return true if this Matrix is a row vector (1 x n), false otherwise
     */
    public boolean isRowvector() {
        return x.length == 1;
    }
    
    //-----------------------------------------------------------
    /**
     * @return true if this Matrix is a columnvector (n x 1), false otherwise
     */
    public boolean isColumnvector() {
        return x[0].length == 1;
    }
    
    //-----------------------------------------------------------
    /**
     * @return true if this Matrix is either a row vecor or a column vector, false otherwise
     */
    public boolean isVector() {
        return isRowvector() || isColumnvector();
    }
    
    //-----------------------------------------------------------
    /**
     * @param m a row vecor or a column vector
     * @return the dot product of this Matrix and m
     */
    public double dotProduct(Matrix m) {
        if (!(this.isVector() && m.isVector())) throw new RuntimeException
            ("Matrices must both be a Vector!!!!");
        if (this.isRowvector()) {
            if (m.isRowvector()) return Matrix.dotProduct(this.getRow(0), m.getRow(0));
            else return Matrix.dotProduct(this.getRow(0), m.getColumn(0));
        }
        else {
            // columnvector
            if (m.isRowvector()) return Matrix.dotProduct(this.getColumn(0), m.getRow(0));
            else return Matrix.dotProduct(this.getColumn(0), m.getColumn(0));
        }
    }
    
    //-----------------------------------------------------------
    /**
     * @return returns a double[][] with all the values of this matrix,
     * first index is the row index, the second is the column index
     */
    public double[][] getData() {
        return Arrays.stream(x)
            .map(row -> Arrays.copyOf(row, row.length))
            .toArray(double[][]::new)
        ;
    }
    
    //-----------------------------------------------------------
    /**
     * @return the transpose of this matrix
     */
    public Matrix transpose() {
        double[][] result = IntStream.range(0, x[0].length)
            .mapToObj(this::getColumn)
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //-----------------------------------------------------------
    @Override
    /**
     * returns true if and only if the dimensions of this matrix and the other
     * are equal, and all the corresponding element are also equal
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Matrix)) return false;
        Matrix m = (Matrix) o;
        return IntStream.range(0, x.length)
            .allMatch(i -> Arrays.equals(x[i], m.getRow(i)))
        ;
    }

    //-----------------------------------------------------------
    /**
     * @return the hash code, code is generated by the IDE
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.deepHashCode(this.x);
        return hash;
    }
    
    //-----------------------------------------------------------
    /**
     * @return true if this Matrix is symmetric, false otherwise
     */
    public boolean isSymmetric() {
        return equals(transpose());
    }
    
    //-----------------------------------------------------------
    /**
     * @param skipRow the row to be skipped
     * @param skipColumn the column to be skipped
     * @return a Matrix that is equal to this Matrix, but with the specified
     * row and column skipped.
     */
    public Matrix minorMatrix(int skipRow, int skipColumn) {
        double[][] result = IntStream.range(0, x.length)
            .filter(i -> i != skipRow)
            .mapToObj(i -> skipElement(x[i], skipColumn))
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //-----------------------------------------------------------
    /**
     * @param skipRow
     * @param skipColumn
     * @return the determinant of the corresponding minorMatrix
     */
    public double minor(int skipRow, int skipColumn) {
        return minorMatrix(skipRow, skipColumn).determinant();
    }
    
    //-----------------------------------------------------------
    /**
     * @return the determinant of this matrix
     */
    public double determinant() {
        if (!isSquare()) throw new RuntimeException("Matrix must be square!!!");
        if (x.length == 1) return x[0][0];
        var copy = copyOfDoubleArray(x);
        int sign = 1;
        for (int i = 0; i < copy.length - 1; i++) {
            if (copy[i][i] == 0) {
                if (!switchRowsSuccesfully(copy, i)) return 0;
                sign = -sign;
            }
            for (int j = i + 1; j < copy.length; j++) {
                wipeRow(copy[i], copy[j], i);
            }
        }
        return sign * IntStream.range(0, copy.length)
            .mapToDouble(i -> copy[i][i])
            .reduce(1, (a, b) -> a * b)
        ;

    }
    
    //-----------------------------------------------------------
    /**
     * @param m the other Matrix
     * @return the matrix product of this matrix and m
     * 
     * if this matrix is N x K, then m should be K x M
     */
    public Matrix multiply(Matrix m) {
        if (this.getColumndimension() != m.getRowdimension())
            throw new RuntimeException("Matrices have incorrect dimensions to multiply!");
        double[][] y = m.transpose().getData();
        double[][] result = Arrays.stream(x)
            .map(xrow -> Arrays.stream(y).mapToDouble(yrow -> dotProduct(xrow, yrow)).toArray())
            .toArray(double[][]::new)
        ;
        return new Matrix(result);
    }
    
    //-----------------------------------------------------------
    /**
     * @param m the other matrix
     * @return the sum of this and m
     * 
     * Only valid if this matrix and m have equal dimensions
     */
    public Matrix add(Matrix m) {
        return addOrSubtract(this, m, (a, b) -> a + b);
    }
    
    //-----------------------------------------------------------
    /**
     * @param m the other matrix
     * @return the difference of this matrix and m
     * 
     * only applicable if this matrix and m have equal dimensions
     */
    public Matrix subtract(Matrix m) {
        return addOrSubtract(this, m, (a, b) -> a - b);
    }
    
    //-----------------------------------------------------------
    /**
     * @param d a double
     * @return the matrix that is the result of d x this matrix
     */
    public Matrix multiply(double d) {
        double[][] result = Arrays.stream(x)
            .map(row -> Arrays.stream(row).map(i -> d * i).toArray())
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //-----------------------------------------------------------
    /**
     * @return the matrix whereby a(i, j) is the minor(i, j) of this matrix
     */
    public Matrix coFactor() {
        if (!this.isSquare()) throw new RuntimeException("Matrix is not square!!!");
        double[][] result = IntStream.range(0, getRowdimension())
            .mapToObj(i -> IntStream.range(0, getColumndimension())
                .mapToDouble(j -> this.minor(i, j) * sign(i, j))
                .toArray()
            )
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //-----------------------------------------------------------
    /**
     * @return the inverse of this matrix, if that inverse exists
     * 
     * A runtimeException will be generated if the inverse does not exist
     */
    public Matrix inverse() {
        double det = this.determinant();
        if (Math.abs(det) < .00001) throw new RuntimeException
            ("determinant is zero or too low ( < .00001) to calculate inverse!!!!!");
        return this.coFactor().transpose().multiply(1 / det);
    }
    
    //-----------------------------------------------------------
    /**
     * @param X a double[][] array containg the independent variables
     * @param Y a double[] of the observed output
     * @return a Matrix M such that Y = MX
     * 
     * For instance, if we have this set of observations:
     * (3, 4) -> 12
     * (5, 6) -> 15
     * 
     * then 
     * X = { {3, 4}, {5, 6} }
     * Y = {12, 15}
     * 
     * M = linearRegression(X, Y)
     * 
     * if we then want to know the prediction of (2, 5) we first put a 1 to 
     * the left of (2, 5) ( => (1, 2, 5)) and then we calculate
     * y = M(1, 2, 5)
     * 
     */
    public static Matrix linearRegression(double[][] X, double[] Y) {
        Matrix m = Matrix.of(addOneToArray(X));
        Matrix mT = m.transpose();
        Matrix result = mT.multiply(m).inverse().multiply(mT).multiply(Matrix.of(Y).transpose());
        return result;
    }
    
    //-----------------------------------------------------------
    /**
     * prints this matrix, each value according to the input format string
     * @param format as used in String.format(...)
     */
    public void print(String format) {
        Arrays.stream(x).forEach( row -> {
                Arrays.stream(row).forEach(d -> System.out.format(format, d));
                System.out.println();
            }
        );
    }
    
    //-----------------------------------------------------------
    /**
     * prints the matrix, using the default format String "%8.2f"
    */    
    public void print() {
        print("%8.2f");
    }
        
    //***********************************************************************
    // private methods
    //***********************************************************************
    
    private Matrix(double[][] x) {
        if (containsNulls(x)) throw new RuntimeException("One or more nulls in the argument!!!");
        if (!isRectangular(x)) throw new RuntimeException("matrix is not rectangular!!!");
        if (!dimensionIsAtLeastOneByOne(x)) throw new RuntimeException("matrixis not a genuine matrix!!!");
        this.x = Arrays.stream(x)
            .map(row -> Arrays.copyOf(row, row.length))
            .toArray(double[][]::new)
        ;
    }

    //-----------------------------------------------------------
    private static boolean containsNulls(double[][] x) {
        return x == null || Arrays.stream(x).anyMatch(row -> row == null);
    }
    
    //-----------------------------------------------------------
    private static boolean isRectangular(double[][] x) {
        return Arrays.stream(x).mapToInt(row -> row.length).distinct().count() == 1;
    }
    
    //-----------------------------------------------------------
    private static boolean dimensionIsAtLeastOneByOne(double[][] x) {
        return x.length >= 1 && x[0].length >= 1;
    }
    
    //-----------------------------------------------------------
    private static double[][] convertToDouble(int[][] x) {
        return Arrays.stream(x)
            .map(row -> Arrays.stream(row).mapToDouble(i -> i).toArray())
            .toArray(double[][]::new)
        ;
    }
    
    //-----------------------------------------------------------
    private static double[][] convertToDouble(long[][] x) {
        return Arrays.stream(x)
            .map(row -> Arrays.stream(row).mapToDouble(i -> i).toArray())
            .toArray(double[][]::new)
        ;
    }
    
    //-----------------------------------------------------------
    private static <S extends Number, U extends Collection<S>, T extends Collection<U>>
                   double[][] convertToDouble(T collection) {
        double[][] result = collection.stream()
            .map(u -> u.stream().mapToDouble(i -> i.doubleValue()).toArray())
            .toArray(double[][]::new)
        ;
        return result;
    }
    
    //-----------------------------------------------------------
    private boolean columnIndexOK(int index) {
        return index >= 0 && index < this.getColumndimension();
    }

    //-----------------------------------------------------------
    private static double[] skipElement(double[] x, int index) {
        if (index < 0 || index > x.length) throw new RuntimeException("index out of bounds");
        return IntStream.range(0, x.length)
            .filter(i -> i != index)
            .mapToDouble(i -> x[i])
            .toArray()
        ;
    }
    
    //-----------------------------------------------------------
    private static double dotProduct(double[] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("Vectors must be of equal length");
        return IntStream.range(0, x.length)
            .mapToDouble(i -> x[i] * y[i])
            .sum()
        ;
    }
    
    //-----------------------------------------------------------
    private static double length(double[] x) {
        return Math.sqrt(dotProduct(x, x));
    }
    
    //-----------------------------------------------------------
    private static boolean matricesAreOkeToAddOrSubtract(Matrix m, Matrix n) {
        return m.getColumndimension() == n.getColumndimension() &&
               m.getRowdimension() == n.getRowdimension()
        ;
    }
    
    //-----------------------------------------------------------
    private static boolean matricesAreOkeToMultiply(Matrix m, Matrix n) {
        return m.getColumndimension() == n.getRowdimension();
    }
    
    //-----------------------------------------------------------
    
    private static double[] addOneToArray(double[] x) {
        return DoubleStream.concat(DoubleStream.of(1), Arrays.stream(x)).toArray();
    }
    
    //-----------------------------------------------------------
    private static double[][] addOneToArray(double[][] x) {
        return Arrays.stream(x).map(Matrix::addOneToArray).toArray(double[][]::new);
    }
    
    //-----------------------------------------------------------
    private static double[] applyToDoubleArrays(double[] x, double[] y, DoubleBinaryOperator dbo) {
        return IntStream.range(0, x.length)
            .mapToDouble(i -> dbo.applyAsDouble(x[i], y[i]))
            .toArray()
        ;
    }
    
    //-----------------------------------------------------------
    private static double[] rowOfIdentity(int size, int index) {
        return IntStream.range(0, size)
            .mapToDouble(i -> (i == index ? 1 : 0))
            .toArray()
        ;
    }
    
    //-----------------------------------------------------------
    private static Matrix addOrSubtract(Matrix m, Matrix n, DoubleBinaryOperator dbo) {
        if (!matricesAreOkeToAddOrSubtract(m, n)) throw new RuntimeException
            ("Matrices are of wrong dimensions to add");
        double[][] result = IntStream.range(0, m.getRowdimension())
            .mapToObj(i -> applyToDoubleArrays(m.getRow(i), n.getRow(i), dbo))
            .toArray(double[][]::new)
        ;
        return Matrix.of(result);
    }
    
    //-----------------------------------------------------------
    private static int sign(int i, int j) {
        if (i >= j) return (i - j) % 2 == 0 ? 1 : -1;
        return (j - i) % 2 == 0 ? 1 : -1;
    }
    
    //------------------------------------------------------------
    private static double[][] copyOfDoubleArray(double[][] d) {
        return Arrays.stream(d)
            .map(row -> Arrays.copyOf(row, row.length))
            .toArray(double[][]::new);
    }
    
    //------------------------------------------------------------
    private static void wipeRow(double[] source, double[] target, int index) {
        double factor = target[index] / source[index];
        for (int i = 0; i < source.length; i++) target[i] -= factor * source[i];
    }
    
    //------------------------------------------------------------
    private static boolean switchRowsSuccesfully(double[][] d, int index) {
        if (d[index][index] != 0) return true;
        for (int i = index + 1; i < d.length; i++) {
            if (d[i][index] != 0) {
                var row = d[index];
                d[index] = d[i];
                d[i] = row;
                return true;
            }
        }
        return false;
    }
}
