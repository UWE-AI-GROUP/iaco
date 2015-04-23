/*
 * Weights.java
 * created 12 July 2012
 */

package myUtils;

/**
 *
 * @author cl-simons
 */

public class Weights 
{
    /** the weight of CBO in multi-objective search */
    public double weightCBO;
    
    /** the weight of NAC in multi-objective search */
    public double weightNAC;
    
    /** the weight of ATMR in multi-objective search */
    public double weightATMR;
    
    /** range for assertion checking */
    private static final double RANGE = 0.000001;
    
    /** 
     * c'tor - construct with a valid default i.e. 100% CBO 
     */
    public Weights( )
    {
        weightCBO = weightNAC = weightATMR = 0.0;
    }

    public Weights( double wCBO, double wNAC, double wATMR )
    {
        assert wCBO >= 0.0 && wCBO <= 1.0;
        assert wNAC >= 0.0 && wNAC <= 1.0;
        assert wATMR >= 0.0 && wATMR <= 1.0;
        
        this.weightCBO = wCBO;
        this.weightNAC = wNAC;
        this.weightATMR = wATMR;
    }
    /**
     * where set to valid and non-zero numbers,
     * check that the three weights sum to 1.0
     */
    public void checkSum( )
    {
        if( Double.isNaN( weightCBO ) || 
            Double.isNaN( weightNAC ) ||
            Double.isNaN( weightATMR ) )
        {
            return; // do nothing
        }
        else if( weightCBO == 0.0 &&
                 weightNAC == 0.0 &&
                 weightATMR == 0.0 )
        {
            System.err.println( "weights are un-initialised" );
            return; // do nothing
        }
        else
        {
            assert weightCBO + weightNAC + weightATMR > ( 1.0 - RANGE ) &&
                   weightCBO + weightNAC + weightATMR < ( 1.0 + RANGE ) :
                "MO weights do not sum to 1.0! " +
                "\n" +
                "CBO weight is: " + weightCBO +
                "NAC weight is: " + weightNAC +
                "ATMR weight is: " + weightATMR +
                "\n" +
                "sum is: " + ( weightCBO + weightNAC + weightATMR );
        }
    }
         
}   // end class

//----------- end of file -----------------------------------
