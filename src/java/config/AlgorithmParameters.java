/*
 * AlgorithmParameters.java
 */

package config;

/**
 * @author Chris Simons
 */

public class AlgorithmParameters 
{
    public enum Algorithm { SimpleACO, MMAS }
    public static Algorithm algorithm = Algorithm.MMAS;
    
    public static final double MMAS_PHEROMONE_MINIMUM = 0.5;
    public static final double MMAS_PHEROMONE_MAXIMUM = 3.5;
    
    public enum PheromoneUpdate { SO, ParetoBased };
//    public static PheromoneUpdate pheromoneUpdate = PheromoneUpdate.ParetoBased;
    public static PheromoneUpdate pheromoneUpdate = PheromoneUpdate.SO;
    
    public enum SOUpdate { CBO, NAC };
    public static SOUpdate sOUpdate = SOUpdate.CBO;
//    public static SOUpdate sOUpdate = SOUpdate.NAC;
    
    // used in PheromoneOperators to calculate delta
    public static boolean objectiveCBO = true;
    public static boolean objectiveNAC = true;
    public static boolean objectiveATMR = true;
    
    public static boolean evaporationElitism = true;
    public static boolean replacementElitism = true;
    
    public static boolean constraintHandling = true;
    
    
    private static final double DEFAULT_ALPHA = 1.5; 
    public static double ALPHA = DEFAULT_ALPHA; 
    
    private static final double DEFAULT_MU = 3.0; 
    public static double MU = DEFAULT_MU;   
    
    public static final double SimpleACO_RHO = 0.1;  // for S-ACO
    // for MMAS. Dorogo and Stutzle, page 91, suggest 0.02
    public static final double MMAS_RHO = 0.035; 
    public static double RHO = MMAS_RHO;
    
    private static final int DEFAULT_NUMBER_OF_ANTS = 100;
    public static int NUMBER_OF_ANTS = DEFAULT_NUMBER_OF_ANTS;
    
    private static final int MAX_ITERATIONS = 1000;
    public static int NUMBER_OF_ITERATIONS = MAX_ITERATIONS;
    
    // 9 April 2013
    // public static final double INTERACTIVE_INTERVAL_CONSTANT = 100;
    public static final double INTERACTIVE_INTERVAL_CONSTANT = 200;
    
    
    // for start of dynamic interactive multi-objective search
    public static final double INITIAL_wCBO = 1.0;
    public static final double INITIAL_wNAC = 0.0;
    public static final double INITIAL_wATMR = 0.0;
    
    // testing in batch mode, 19 September 2012
    // set these values when in pareto based pheromone update mode
    public static double weightCBO = 0.0;
    public static double weightNAC = 0.0;
    public static double weightATMR = 0.0;
    
    
    /** for batch mode */
    public static int NUMBER_OF_RUNS = 50;
    
    // 15 January 2013
//    public static final int HEURISTICS_OFF = 0;
//    public static final int HEURISTICS_CBO_ONLY = 1;
//    public static final int HEURISTICS_NAC_ONLY = 2;
//    public static final int HEURISTICS_BOTH = 3;
//    
    // flag to signal exploitation of heuristic information
    public static boolean heuristics = false;
    
    // parameters controlling influence of heuristic information
    public static double BETA_CBO = 1.0;
    public static double BETA_NAC = 1.0;
    
}   // end class

// ------ end of file -----------------------------------------
