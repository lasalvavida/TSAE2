package edu.drexel.TSAE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Generates a single polynomial expression from a set of points describing
 * its derivatives.
 * 
 * @author tags 
 * @version 0.1
 */
public class SinglePolynomial
{
    int[][] data;
    int initialPosition;
    /**
     * Interprets an array of data. Data is of the following form:
     * [[timestamp],         ,[timestamp],...]
     *  [order of derivative],[order],...    ]]
     *  The initial position is the value of f(0).
     */
    public SinglePolynomial(int[][] _data, int _initialPosition) {
        data= _data;
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
     * Returns the lowest order derivative in the data set.
     */
    public int lowestOrder() {
        int lowest = data[0][1];
        for(int i0=0;  i0<data.length; i0++) {
            if(data[i0][1] < lowest) {
                lowest = data[i0][1];
            }
        }
        return lowest;
    }

    /**
     * Returns the order that the resulting polynomial must be.
     */
    public int requiredOrder() {
        int order = 0;
        for(int i0=0; i0<data.length; i0++) {
            order += data[i0][1]+1;
        }
        return order;
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

    /**
     * When passed an integer, translates that integer to a unique identifying String that is not used by symja's interpreting engine.
     * TODO: Implement a mutating string algorithm here
     */
    private String uniqueString(int num) { 
        String ret = "";
        for(int i=0; i<=num/TSAEUtilities.allowedChars.length; i++) {
            if(i==num/TSAEUtilities.allowedChars.length) {
                ret += "" + TSAEUtilities.allowedChars[num%TSAEUtilities.allowedChars.length];
            }
            else {
                ret += "a";
            }
            ret += "E";
        }
        return ret;
    }
    
    /**
     * Creates a template polynomial for the data
     */
    private String templatePolynomial() {
        String ret = "";
        for(int i=requiredOrder(); i>0; i--) {
            ret += uniqueString(i)+"*t^"+i+"+";
        }
        ret += uniqueString(0);
        return ret;
    }

    /**
     * Creates a system of equations whose coefficients need solving.
     */
    public String[] generateSystem() {
        String[] expressions = generateEmptyArray(data.length);
        for(int i0=0; i0<expressions.length; i0++) {
            expressions[i0] += "0==";
            int n = 0;
            for(n=requiredOrder(); n>data[i0][1]; n--) {
                expressions[i0] += "("+uniqueString(n)+"*"+data[i0][0]+"^("+(n-data[i0][1])+")*("+n+"!"+"/("+n+"-"+data[i0][1]+")!)"+")+";
            }
            expressions[i0] += ""+uniqueString(n);
        }
        return expressions;
    }

    /**
     * Find a single polynomial P(t) to satisfy the data.
     */
    public String solve() {
        //Build the reference strings
        String[] system = generateSystem();
        HashMap<String,Double> coefficients = new HashMap<String,Double>();
        //build the unlisted coefficients
        for(int n=0; n<lowestOrder(); n++) {
            coefficients.put(uniqueString(n), new Double(0));
        }
        String[] ref = new String[(system.length+1)/2];
        for(int n=0; n<ref.length; n++) {
            ref[n] = ""+uniqueString(requiredOrder()-n);
        }
        String[] solutions = TSAEUtilities.solve(system, ref);
        //symja has a hard time solving when it has determined the value of one variable, so this does the substitution for it
        int dropCount = 0;
        boolean drop;
        for(int i0=0; i0<solutions.length; i0++) {
            drop = false;
            for(int i1=i0+1; i1<solutions.length; i1++) {
                if(solutions[i1].contains(solutions[i0].split("==")[1])) {
                    solutions[i1] = solutions[i1].replace((solutions[i0].split("=="))[1],(solutions[i0].split("=="))[0]);
                    drop = true;
                }
            }
            if(drop) {
                coefficients.put((solutions[i0].split("=="))[1], new Double((solutions[i0].split("=="))[0]));
                solutions = drop(solutions, solutions[i0]);
                dropCount++;
                i0--;
            }
        }
        //resolve unshared terms
        for(int i0=0; i0<requiredOrder(); i0++) {
            String coe = uniqueString(i0);
            boolean flag = false;
            for(String eq : solutions) {
                if(!eq.contains(coe)) {
                    flag = true;
                }
            }
            if(flag && coefficients.get(coe) == null) {
                for(int i1 = 0; i1<solutions.length; i1++) {
                    solutions[i1] = solutions[i1].replace(coe, "0");
                }
                coefficients.put(coe, new Double(0));
                dropCount++;
            }
        }
        //solve the simplified system
        ref = null;
        ref = new String[solutions.length];
        int index = 0;
        for(int n=0; n<ref.length; n++) {
            if(Arrays.toString(solutions).contains(uniqueString(index))) {
                ref[n] = uniqueString(requiredOrder()-index);
            }
            else {
                n--;
            }
            index++;
        }
        solutions = TSAEUtilities.solve(solutions, ref);
        //assign values of 1 to coefficients that are independent
        for(String ans : solutions) {
            for(int n=0; n<requiredOrder(); n++) {
                coefficients.put(uniqueString(n), new Double(1));
                
            }
        }
        for(String ans : solutions) {
            for(String key : coefficients.keySet()) {
                ans = ans.replaceAll(key, "" + coefficients.get(key));
            }
            coefficients.put(ans.split("->")[0], new Double(TSAEUtilities.evalN(ans.split("->")[1])));
        }
        String poly = templatePolynomial();
        for(String key : coefficients.keySet()) {
            poly = poly.replaceAll(key, "" + coefficients.get(key));
        }
        return poly;
    }
    
    /**
     * Drop a string out of a string array
     */
    public String[] drop(String[] arr, String term) {
        String[] ret = new String[arr.length-1];
        int index = 0;
        for(int i=0; i<arr.length; i++) {
            if(!arr[i].equals(term)) {
                ret[index] = arr[i];
                index ++;
            }
        }
        return ret;
    }
}