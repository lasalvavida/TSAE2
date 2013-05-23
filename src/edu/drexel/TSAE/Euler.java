package edu.drexel.TSAE;


/**
 * Determines data values using an approximation based on Euler's method.
 * 
 * @author tags
 * @version 0.1
 */
public class Euler
{
    int[][] data;
    int initialPosition;
    /**
     * Interprets an array of data. Data is of the following form:
     * [[timestamp],         ,[timestamp],...]
     *  [order of derivative],[order],...    ]]
     *  The initial position is the value of f(0).
     */
    public Euler(int[][] _data, int _initialPosition) {
        data = _data;
        initialPosition = _initialPosition;
    }
    /**
     * Returns the highest order derivative in the data set.
     */
    public int highestOrder() {
        int highest = 0;
        for(int i0=0;  i0<data.length; i0++) {
            if(data[i0][1] > highest) {
                highest = data[i0][1];
            }
        }
        return highest;
    }
    /**
     * Generates an empty array of strings.
     */
    private String[] generateEmptyArray(int size) {
        String[] ret = new String[size];
        for(int i0=0; i0<size; i0++) {
            ret[i0] = "";
        }
        return ret;
    }
    public String[] generateExpressions() {
        String[] expressions = generateEmptyArray(highestOrder());
        for(int i0=0; i0<data.length; i0++) {
            expressions[data[i0][1]] += "($t-"+data[i0][0]+")";
        }
        return expressions;
    }
    public double[] getValues(int stepSize, int setSize) {
        double[] ret = new double[setSize];
        ret[0] = initialPosition;
        String[] expressions = generateExpressions();
        for (int i=1; i<setSize; i++) {
            double slope = 0;
            String expression;
            for (int l0=0; l0<expressions.length; l0++) {
                expression = expressions[l0];
                for (int l1=0; l1<l0; l1++) {
                    expression = TSAEUtilities.integrate(expression,"$t");
                }
                slope = Double.parseDouble(TSAEUtilities.eval("$t="+(stepSize*i)));
            }
            ret[i] = ret[i-1] + slope*stepSize*i;
        }
        return ret;
    }
}
