package edu.drexel.TSAE;

import java.util.HashMap;

/**
 * A piecewise function of SinglePolynomials.
 * 
 * @author tags
 * @version 0.1
 */
public class MultiplePolynomial
{
    HashMap<Long, SinglePolynomial> functions;
    public MultiplePolynomial() {
        functions = new HashMap<Long, SinglePolynomial>();
    }
    public void addFunction(SinglePolynomial func, long offset) {
        functions.put(new Long(offset),func);
    }
    public double eval(long time) {
        Long prev = new Long(0);
        for(Long val : functions.keySet()) {
            if (time < val.longValue()) {
                return Double.parseDouble(
                    TSAEUtilities.evalN(
                        functions.get(prev).solve().replaceAll("t",""+time)
                    )
                );
            }
            prev = val;
        }
        return 0;
    }
}
