package edu.drexel.TSAE;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;

import edu.jas.kern.ComputerThreads;

/**
 * TSAEUtilities contains access methods for symbolic interpretation
 * and other conveniences to make the code easier to read in other places.
 * 
 * @author tags
 * @version 0.1
 */
public final class TSAEUtilities
{
    private static boolean init = false;
    private static boolean VERBOSE = true;
    private static EvalUtilities util = new EvalUtilities();
    public static String[] allowedChars = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    /**
     * Initializes the symja engine.
     */
    public static void init() {
        F.initSymbols(null);
        init = true;
    }
    /**
     * Expands and simplifies an expression.
     */
    public static String expand(String expression) {
        return eval("Expand["+expression+"]");
    }
    /**
     * Integrates an expression with respect to a given variable.
     */
    public static String integrate(String expression, String var) {
        return eval("Integrate["+expression+","+var+"]");
    }
    /**
     * Integrates an expression with respect to a given variable and with the given limits.
     */
    public static String integrate(String expression, String var, String lowBound, String highBound) {
        return eval("Integrate["+expression+","+var+","+lowBound+","+highBound+"]");
    }
    /**
     * Derives an expression with respect to a given variable.
     */
    public static String derive(String expression, String var) {
        return eval("D["+expression+","+var+"]");
    }
    /**
     * Factor an expression.
     */
    public static String factor(String expression) {
        return eval("Factor["+expression+"]");
    }
    /**
     * Splits the expressions as far apart as possible.
     */
    public static String apart(String expression) {
        return eval("Apart["+expression+"]");
    }
    /**
     * Returns a String[] containing the roots of the given expression.
     */
    public static String[] roots(String expression) {
        String roots = eval("Roots["+expression+"]");
        roots = roots.substring(1,roots.length()-1);
        return roots.split(",");
    }
    /**
     * Solve a system of equations for the specified variables.
     */
    public static String[] solve(String[] system, String[] vars) {
        String sys = "{";
        for(int i=0; i<system.length-1; i++) {
            sys += system[i] + ",";
        }
        sys += system[system.length-1] + "}";
         
        String v = "{";
        for(int i=0; i<vars.length-1; i++) {
            v += vars[i] + ",";
        }
        v += vars[vars.length-1] + "}";
        String ret = eval("Solve["+sys+","+v+"]");
        if(ret.indexOf("{{") < 0) {
            return (ret.split("\\},\\{")[0]).substring((ret.split("\\},\\{")[0]).indexOf("{")+1).split(",");
        }
        else {
            return ret.substring(2, ret.length()-2).split(",");
        }
    }
    /**
     * Evaluate some expression numerically.
     */
    public static String evalN(String expression) {
        return eval("N["+expression+"]");
    }
    /**
     * Evaluate some expression using the symja engine.
     */
    public static String eval(String expression) {
        if(!init) {
            init();
        }
        if(VERBOSE) {
            System.out.println("IN: "+expression);
        }
        try {
            StringBufferWriter buf = new StringBufferWriter();
            IExpr result = util.evaluate(expression);
            OutputFormFactory.get().convert(buf, result);
            if(VERBOSE) {
                System.out.println("OUT: "+buf.toString());
            }
            return buf.toString();
        }
        catch (Exception e) {
            System.err.println("Error while evaluating expression "+expression+" : "+e.getMessage());
        }
        finally {
            ComputerThreads.terminate();
        }
        return null;
    }
}
