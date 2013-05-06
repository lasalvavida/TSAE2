package edu.drexel.TSAE;

/**
 * Contains tests for the TSAE library.
 * 
 * @author tags 
 * @version 0.1
 */
public class Test
{
    public static void main(String[] args) {
        /**
         * Make sure that factored polynomials are being generated properly.
         */
        int[][] data = new int[5][2];
        data[0][0] = 0;
        data[0][1] = 1;
        data[1][0] = 4;
        data[1][1] = 2;
        data[2][0] = 6;
        data[2][1] = 1;
        data[3][0] = 20;
        data[3][1] = 0;
        data[4][0] = 10;
        data[4][1] = 3;
        
        SinglePolynomial poly = new SinglePolynomial(data,0);
        System.out.println(poly.solve());
    }
}
