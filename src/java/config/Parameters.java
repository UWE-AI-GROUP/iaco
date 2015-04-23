/*
 * Parameters.java
 */

package config;

/**
 * @author j4-smith
 * jim adding this line as a comment to test subversion 8-3-12
 * chris - changed problem numbers. 1 is GDP, 2 is SC
 * 25 April 2012, Chris, reset method added
 * 
 */

public class Parameters 
{
    public static final int TEST = 0;
    public static final int CBS = 1;
    public static final int GDP = 2;
    public static final int SC = 3;
    public static final int NUMBER_OF_PROBLEMS = SC + 1;
    
    private static final int MARTIN = 0;
    private static final int STEWART = 1;
    private static final int JANE = 2;
    private static final int LARRY = 3;
    private static final int JIM = 4;
    private static final int JULIA = 5;
    private static final int BARRY = 6;
    private static final int ANDY = 7;
    private static final int DANNY = 8;
    private static final int WILL = 9;
    private static final int DEHLIA = 10;
    private static final int CHRIS = 11;
    public static final int NUMBER_OF_DESIGNERS = CHRIS + 1;
    
    public static final int NAC = 0;
    public static final int ATMR = 1;
    public static final int NAC_AND_ATMR = 2;
    public static final int NUMBER_OF_SURROGATES = NAC_AND_ATMR + 1;
            
    public static final int WATER_TAP = 0;
    public static final int TRAFFIC_LIGHTS = 1;
    
   
    // the following parameters are set at the start of each design episode
    
    public static int designer;  
    
    public static int problemNumber;   
    
    public static int episodeNumber; 
            
    public static boolean freezing;
            
    public static int colourMetaphor;
    
    public static String outputFilePath;
    
}   // end of class

//----------- end of file --------------------------------------------
