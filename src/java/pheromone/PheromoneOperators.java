/*
 * PheromoneOperators.java
 * created 18 October 2011
 * 25 April 2012 - mu and rho passed as parameters to operators
 */

package pheromone;

import java.util.*;
import config.*;
import myUtils.Utility;
import engine.*;
import myUtils.Weights;
import pareto.ParetoOperators;
import softwareDesign.CLSClass;

/**
 * This class offers the capabilities related to Pheromone
 * i.e. pheromone deposit for a state transition in a solution path, 
 * and pheromone decay (evaporation)
 *
 * See "Ant Colony Optimisation", by Dorigo and Stutzle,
 * 2004, MIT Press.
 * 
 * In the Dorigo book, the evaporation coefficient is known
 * as "rho", which stands for "rate"
 * 
 * I've introduced a new parameter "mu" for pheromone update.
 * Pheromone is updated in proportion to coupling fitness for
 * full path designs. The updates are raised to to power of "mu".
 * 
 * @author Christopher Simons
 */

public class PheromoneOperators 
{
    // 24 April 2012 factor for elistist decay / evaporation
    private static final double ELITIST_FACTOR = 0.02;
    
    // 10 July 2012
    private static final boolean SINGLE_OBJECTIVE = false;
    
    // 20 July 2012
    /** the ludicrously high delta to freeze a class */
    private static final double FREEZE_DELTA = 1000000.0;
    
    /**
     * let evaporation of the pheromone take place
     * 19 April 2012 a touch of elitism added
     * @param reference to the pheromoneTable
     * @param rho - the rate of decay parameter
     */
    public static void evaporate( PheromoneTable pheromoneTable, double rho )
    {
        assert pheromoneTable != null;
        final int pheromoneTableSize = pheromoneTable.size( );
        assert rho >= 0.0;
        assert rho <= 1.0;
        
        // 19 April 2012 - fitness proportionate decay
        double lowest = 1000000.0;
        double highest = 0.0;
        double prob = 0.0;
        double median = 0.0;
        
        // 24 April 2012, switch to toggle elist evaporation
        if( AlgorithmParameters.evaporationElitism == true )
        {
            for( int i = 0; i < pheromoneTableSize; i++) 
            {
                for( int j = 0; j < pheromoneTableSize; j++) 
                {
                    prob = pheromoneTable.getProbabilityAt( i, j );

                    if( prob < lowest )
                    {
                        lowest = prob;
                    }

                    if( prob > highest )
                    {
                        highest = prob;
                    }
                }
            }
        
            assert highest >= lowest;
            assert highest - lowest != 0.0;
            // bug fix 7 September 2012
//            median = ( highest - lowest ) / 2.0;
            median = lowest + ( ( highest - lowest ) / 2.0 );
        }
        
        final double evaporationFactor = 1.0 - rho;
        assert evaporationFactor >= 0.0;
        assert evaporationFactor <= 1.0;
        
        for( int i = 0; i < pheromoneTableSize; i++) 
        {
            for( int j = 0; j < pheromoneTableSize; j++) 
            {
                prob = pheromoneTable.getProbabilityAt( i, j );
                double newProb = 0.0;
                
                if( AlgorithmParameters.evaporationElitism == true )
                {
                    double multiplier = 1.0;

                    if( prob > median ) // decay is proportionately less
                    {
                        double difference = highest - prob;
                        multiplier = evaporationFactor * ( 1 - ( difference / median ) * ELITIST_FACTOR );
                    }
                    else if( prob < median ) // decay is proportionately more
                    {
                        double difference = median - prob;
                        multiplier = evaporationFactor * ( 1 + ( difference / median ) * ELITIST_FACTOR );
                    }
                    else // prob == median
                    {
                        // do nothing, multiplier staus at 1.0
                    }

                    newProb = prob * multiplier;
                }
                else // uniform (normal) evaporation 
                {
                    newProb = prob * evaporationFactor;  
                }
                        
                pheromoneTable.setProbabilityAt( i , j, newProb );
            }
        }
    }
    
    /**
     * Update the pheromone levels in the pheromone table.
     * then the pheromone is updated with respect to class cohesion.
     * 
     * @param reference to pheromoneTable
     * @param reference to the colony
     * @param mu - the update parameter
     * @param weights
     * @param bestInColonyCBO best path in the colony w.r.t. CBO
     * @param bestInColonyNAC best path in the colony w.r.t. NAC
     * @param bestInColonyATMR best path in the colony w.r.t. ATMR
     */
    public static void update( 
        PheromoneTable pheromoneTable, 
        List< Path > colony, 
        double mu,
        Weights weights,
        Path bestInColonyCBO,
        Path bestInColonyNAC,
        Path bestInColonyATMR )
    {
        assert pheromoneTable != null;
        assert colony != null;
        assert colony.size( ) > 0;
        assert mu >= 0.0;
        assert mu <= 10.0; // some arbitary value?
        assert weights != null;
        assert bestInColonyCBO != null;
        assert bestInColonyNAC != null;
        assert bestInColonyATMR != null;
        
        // 9 April 2013
        if( AlgorithmParameters.algorithm == AlgorithmParameters.algorithm.MMAS ) 
        {
            performMMASUpdate( 
                colony, pheromoneTable, mu, weights, bestInColonyCBO, bestInColonyNAC );
        }
        else // Simple-ACO, so every ant lays pheromone
        {
            Iterator< Path > it = colony.iterator( );

            while( it.hasNext( ) ) 
            {
                Path p = it.next( );

                if( p.getRoleDistinction( ) == Role.Distinction.design )
                {
                    layPheromoneForWholePathSolution( 
                        p, pheromoneTable, mu, weights );
                }
                else if( p.getRoleDistinction( ) == Role.Distinction.classs )
                {
                    layPheromoneForCohesion( p, pheromoneTable, mu );
                    System.out.println( "partial path present!" );
                }
                else
                {
                    assert true : "impossible role distinction";
                }

            }   // end while
        
        }   // end if

//        pheromoneTable.show();
    }
    
    /**
     * update the pheromone table with respect to design coupling
     * @param reference to path
     * @param reference to pheromoneTable
     * @param mu - the update parameter
     * @param multi-objective weights
     */
    private static void layPheromoneForWholePathSolution( 
        Path path, PheromoneTable pheromoneTable, double mu, Weights weights )
    {
        assert path != null;
        assert pheromoneTable != null;
        assert mu >= 0.0;
        assert weights != null;
        
        double delta = calculateDelta( path, mu, weights );
//        double delta = calculateDelta2( path, mu );
        
        int from = 0, to = 0;
        
        // final node must be an "end of class"
        final int finalNode = path.size( ) - 1;
            
        // and now iterate along the node list in the path
        Iterator< Node > it = path.iterator( );
        
        for( int i = 0; it.hasNext( ); i++ )
        {
            Node node = it.next( );
            
            if( i == 0 )    // the "nest"
            {    
                from = node.getNumber( );
            }
            else if( i == finalNode ) // the last "end of class" marker
            {
                // do nothing, because the probability of moving from
                // the last end of class marker is always zero
                
                assert node instanceof EndOfClass;
            }
            else
            {
                to = node.getNumber( );
            
                double probability = pheromoneTable.getProbabilityAt( from, to );

                probability += delta;
                
                if( AlgorithmParameters.algorithm == AlgorithmParameters.algorithm.MMAS )
                {
                    // In MAX-MIN Ant System, the range of pheromone levels
                    // is limited to an interval [Tmin, Tmax], which
                    // ensures a minimum degree of search diversification.
                    
                    if( probability < AlgorithmParameters.MMAS_PHEROMONE_MINIMUM ) 
                    {
                        probability = AlgorithmParameters.MMAS_PHEROMONE_MINIMUM;
                    }
                    
                    if( probability > AlgorithmParameters.MMAS_PHEROMONE_MAXIMUM ) 
                    {
                        probability = AlgorithmParameters.MMAS_PHEROMONE_MAXIMUM;
                    }
                }

                // in Simple-ACO, there is no enforcement of any range
                // of pheromone levels in the pheromone matrix
                
                pheromoneTable.setProbabilityAt( from, to, probability );
                
                // 18 April 2012 symmetrical pheromone update 
                pheromoneTable.setProbabilityAt( to, from, probability );

                // advance to next vertex
                from = to;
            }
            
        }   // end for each vertex in the solution path        
    }
    
    /**
     * calculate the delta for update
     * @param path
     * @param mu
     * @param weights
     * @return delta
     */
    private static double calculateDelta( 
        Path path, double mu, Weights weights )
    {
        assert path != null;
        assert mu >= 0.0;
        assert weights != null;
        
        double NACScale = 0.0;
        double ATMRScale = 0.0;

        // initialise scale factors according to design problem
        if( Parameters.problemNumber == Parameters.CBS )
        {
            NACScale = 6.0;
            ATMRScale = 4.0;
        }
        else if( Parameters.problemNumber == Parameters.TEST ||
                 Parameters.problemNumber == Parameters.GDP )
        {
            NACScale = 10.0;
            ATMRScale = 5.5;
        }
        else if( Parameters.problemNumber == Parameters.SC )
        {
            NACScale = 11.0;
            ATMRScale = 7.0;
        }
        else
        {
            assert false : "impossible problem parameter!";
        }
            
        double delta = 0.0;
        
        double maximisedCBO = 0.0;
        double maximisedNAC = 0.0;
        double maximisedATMR = 0.0;
        
        boolean CBO = false;
        boolean NAC = false;
        boolean ATMR = false;
        
        if( AlgorithmParameters.objectiveCBO == true )
        {
            CBO = true;
            
            double coupling = path.getCBO( ); 
            maximisedCBO = 1.0 - coupling; // transform to maximisation
            assert maximisedCBO >= 0.0;
            assert maximisedCBO <= 1.0;
        }
     
        if( AlgorithmParameters.objectiveNAC == true )
        {
            NAC = true;
            double eleganceNAC = path.getEleganceNAC( );
            assert eleganceNAC >= 0.0;
            
            // normalise...
            double temp = Math.min( eleganceNAC, NACScale );
            double normalisedNAC = ( NACScale - temp ) / NACScale;
            assert normalisedNAC <= 1.0;
            assert normalisedNAC >= 0.0;
            
            // and then maximise
            maximisedNAC = 1 - normalisedNAC;
        }
        
        if( AlgorithmParameters.objectiveATMR == true )
        {
            ATMR = true;
            double eleganceATMR = path.getEleganceATMR( );
            assert eleganceATMR >= 0.0;
            
            // normalise...
            double temp = Math.min( eleganceATMR, ATMRScale );
            double normalisedATMR = ( ATMRScale - temp ) / ATMRScale;
            assert normalisedATMR >= 0.0;
            assert normalisedATMR <= 1.0;
            
            // and then maximise
            maximisedATMR = 1 - normalisedATMR;
        }

        assert CBO == true || NAC == true || ATMR == true : "no objectives selected! ";
        
        double rawValue = 0.0;

        // as used in the multi-objective simulation
//            rawValue = calculateRawFactorWithFixedCBOWeight( 
//                maximisedCBO, normalisedNAC, normalisedATMR );

        // 17 Sept 2012
        // as used to explore and batch test multi-objective search
//        rawValue = calculateRawValueTest(
//            maximisedCBO, maximisedNAC, maximisedATMR );

        // as used in interactive multi-objective ACO
        rawValue = calculateRawFactorWithWeights( 
                  maximisedCBO, maximisedNAC, maximisedATMR, weights );

        // raise the raw factor to the power of mu
        delta = Math.pow( rawValue, mu ); 
        
        return delta;
    }

    /**
     * AS USED IN THE MULTI-OBJECTIVE SIMULATION
     * 
     * Calculate the raw multi-objective factor with a fixed CBO weight of 0.8.
     * @param maximisedCBO - coupling
     * @param normalisedNAC 
     * @param normalisedATMR 
     * @param mu - the update parameter
     * @return rawFactor
     */
    private static double calculateRawFactorWithFixedCBOWeight( 
        double maximisedCBO, 
        double normalisedNAC,
        double normalisedATMR )
    {
        // set up the weights
        final double CBOWeight = 0.80; // applies to all design problems

        final double NACWeight = Utility.getRandomInRange( 0.0, 1.0 - CBOWeight );
        assert NACWeight >= 0.0;
        assert NACWeight <= ( 1.0 - CBOWeight );
        final double ATMRWeight = 1.0 - ( CBOWeight + NACWeight );
        assert CBOWeight + NACWeight + ATMRWeight == 1.0;

        double temp = 
           ( maximisedCBO * CBOWeight ) +
           ( normalisedNAC * NACWeight ) +
           ( normalisedATMR * ATMRWeight );
        assert temp >= 0.0 : "temp is:" + temp;
        
        return temp;
    }
    
    
    private static double calculateRawValueTest(
        double maximisedCBO, 
        double maximisedNAC, 
        double maximisedATMR )
    {
        assert maximisedCBO >= 0.0;
        assert maximisedNAC >= 0.0;
        assert maximisedATMR >= 0.0;
        
        double result = 0.0;
        
        boolean CBO = false;
        boolean NAC = false;
        boolean ATMR = false;
        
        double CBOWeight = 0.0;
        double NACWeight = 0.0;
        double ATMRWeight = 0.0;
        
        if( AlgorithmParameters.objectiveCBO == true )
        {
            CBO = true;
        }
     
        if( AlgorithmParameters.objectiveNAC == true )
        {
            NAC = true;
        }
        
        if( AlgorithmParameters.objectiveATMR == true )
        {
            ATMR = true;
        }
     
     
        // handle single objective first
    
        if( CBO == true && NAC == false && ATMR == false )
        {
            result = maximisedCBO;
            
        }
        else if( CBO == false && NAC == true && ATMR == false )
        {
            result = maximisedNAC;
            
        }
        else if( CBO == false && NAC == false && ATMR == true )
        {
            result = maximisedATMR;
            
        }
        
        // handle multi objective second
        
        else if( CBO == true && NAC == true && ATMR == false )
        {
            CBOWeight = 0.5;
            NACWeight = 0.5;
            
            result = ( maximisedCBO * CBOWeight ) * ( maximisedNAC * NACWeight );
        }
        else if( CBO == true && NAC == true && ATMR == true )
        {
            CBOWeight = 0.33;
            NACWeight = 0.33;
            ATMRWeight = 0.33;
            
            result = ( maximisedCBO * CBOWeight ) * 
                     ( maximisedNAC * NACWeight ) *
                     ( maximisedATMR * ATMRWeight );
        }
        else
        {
            assert false : "impossible objective combination!!";
        }
        
        
        return result;
    }
    
    
    /**
     * calculate the rawFactor using coefficients
     * @param maximised CBO
     * @param maximised ATMR
     * @param maximisedNAC 
     * @param the coefficients
     * @return rawFactor
     */
    private static double calculateRawFactorWithWeights( 
        double maximisedCBO, 
        double maximisedNAC,
        double maximisedATMR,
        Weights weights )
    {
        assert maximisedCBO >= 0.0 && maximisedCBO <= 1.0;
        assert maximisedNAC >= 0.0 && maximisedNAC <= 1.0;
        assert maximisedATMR >= 0.0 && maximisedATMR <= 1.0;
        assert weights != null;
        weights.checkSum( );
        
        boolean weightedSum = true;
        double rawFactor = 0.0;
        
        if( weightedSum )
        {
            rawFactor =
                ( weights.weightCBO * maximisedCBO ) +
                ( weights.weightNAC * maximisedNAC ) +
                ( weights.weightATMR * maximisedATMR ); 
        }
        else    // must be weighted product
        {
            rawFactor =
                ( weights.weightCBO * maximisedCBO ) *
                ( weights.weightNAC * maximisedNAC ) *
                ( weights.weightATMR * maximisedATMR ); 
        }
        
        return rawFactor;
    }
    
    
    /**
     * calculate the delta using the reciprocal of the weighted
     * domination count. 
     * @param path
     * @param mu 
     * @return delta - the amount by which to increase the pheromone
     */
    private static double calculateDelta2( Path path, double mu  )
    {
        assert path != null;
        assert mu >= 0.0;
        
        final int weightedDominationCount = path.getWeightedDominationCount( );
        assert weightedDominationCount >= 0;
        
        double rawFactor = 1.0 / weightedDominationCount;
        
         // raise the raw factor to the power of mu
        double delta = Math.pow( rawFactor, mu ); 
        
        return delta;
    }
    
   
    /**
     * Update the pheromone table with respect to class cohesion
     * Cohesion ranges from 0.0 (bad) to 1.0 (good). 
     * Pheromone is increased by the cohesion value 
     * 
     * @param reference to path
     * @param reference to pheromoneTable 
     */
    private static void layPheromoneForCohesion( 
        Path path, PheromoneTable pheromoneTable, double mu )
    {
        assert path != null;
        assert pheromoneTable != null;

        assert mu >= 0.0;
        // 25 April 2012, mu not used for cohesion, but it's here now
        // if needed in the future!
        
        int from = 0, to = 0;
        double cohesion = path.getCohesion( );
        assert cohesion >= 0.0;
        assert cohesion <= 1.0;
        
        // and now iterate along the nodes in the path
        Iterator< Node > it = path.iterator( );
        
        for( int i = 0; it.hasNext( ); i++ )
        {
            Node node = it.next( );
            
            if( i == 0 )
            {    
                from = node.getNumber( );
            }
            else
            {
                to = node.getNumber( );
            
                double probability = pheromoneTable.getProbabilityAt( from, to );

                probability += cohesion;

                pheromoneTable.setProbabilityAt( from, to, probability );

                // advance to next vertex
                from = to;
            }
            
        }   // end for each vertex in the solution path           
    }
    
    /**
     * perform an update of the pheromone table to effectively
     * freeze the class.
     * @param pheromone table
     * @param class to be frozen
     */
    public static void freezeUpdate( PheromoneTable pt, List< CLSClass > freezeList )
    {
        assert pt != null;
        assert freezeList != null;
        
        for( CLSClass c : freezeList )
        {
            List< Method > mList = c.getMethodList( );
            List< Attribute > aList = c.getAttributeList( );

            System.out.println( "class in freeze list... ");
            for( int i = 0; i < mList.size( ); i++ )
            {
                System.out.print( " method is: " + 
                                    mList.get( i ).getNumber( ) + " " +
                                    mList.get( i ).getName( ) );
            }

            for( int j = 0; j < aList.size( ); j++ )
            {
                System.out.print( " attribute is: " + 
                                    aList.get( j ).getNumber( ) + " " +
                                    aList.get( j ).getName( ) + " " );
            }
            System.out.println( "" );
        }
        
        for( CLSClass c : freezeList )
        {
            List< Method > mList = c.getMethodList( );
            List< Attribute > aList = c.getAttributeList( );

            for( int i = 0; i < mList.size( ); i++ )
            {
                final int methodNumber = mList.get( i ).getNumber( );

                for( int j = 0; j < aList.size( ); j++ )
                {
                   final int attributeNumber = aList.get( j ).getNumber( );

                   pt.setProbabilityAt( methodNumber, attributeNumber, FREEZE_DELTA );
                   // and for the symmtrical update...
                   pt.setProbabilityAt( attributeNumber, methodNumber, FREEZE_DELTA );
                }
            }
        }
         
        pt.show( );
        
    }
    
    /**
     * perform pheromone update when using MMAS
     * @param colony i.e. all tours generated by the colony
     * @param pheromoneTable
     * @param mu update parameter
     * @param weights 
     */
    private static void performMMASUpdate( 
        List< Path > colony, 
        PheromoneTable pheromoneTable, 
        double mu, 
        Weights weights,
        Path bestInColonyCBO,
        Path bestInColonyNAC )
    {
        if( AlgorithmParameters.pheromoneUpdate == AlgorithmParameters.PheromoneUpdate.ParetoBased )
        {
            performParetoDominationBasedUpdate( colony, pheromoneTable, mu, weights );
        }
        else if( AlgorithmParameters.pheromoneUpdate == AlgorithmParameters.PheromoneUpdate.SO )
        {
            if( AlgorithmParameters.sOUpdate == AlgorithmParameters.SOUpdate.CBO )
            {
                performCBOUpdate( bestInColonyCBO, pheromoneTable, mu );
            }
            else if( AlgorithmParameters.sOUpdate == AlgorithmParameters.SOUpdate.NAC )
            {
                performNACUpdate( bestInColonyNAC, pheromoneTable, mu );
            }
            else
            {
                assert true : "impossible single objective update parameter";
            }
        }
        else
        {   
            assert true : "impossible pheromone update parameter";
        }
    }
    
    
    private static void performParetoDominationBasedUpdate( 
        List< Path > colony, PheromoneTable pheromoneTable, double mu, Weights weights )
    {
         //domination counts are already calculated in the deamon actions
//          ParetoOperators.calculateDominationCount2( colony );

        // MAX_MIN Ant System (MMAS), so
        // only best in colony lays pheromone
        boolean done = false;
        int domCount = 0;

        while( ! done )
        {
            List< Path > list = ParetoOperators.getPathsWithDomCount( colony, domCount );
            if( list.isEmpty( ) )
            {
                domCount++;
            }
            else
            {
                int numberOfPaths = list.size( );
                assert numberOfPaths > 0;
                // select one at random from this list
                final int randomSelection = Utility.getRandomInRange( 1, numberOfPaths );
                assert randomSelection <= numberOfPaths;
                Path path = list.get( randomSelection - 1 );


                assert path != null;

                layPheromoneForWholePathSolution( path, pheromoneTable, mu, weights );

                // if you wish to update with non-dominated solutions, use this...
//                    for( Path p : list )
//                    {
//                        layPheromoneForWholePathSolution( p, pheromoneTable, mu, weights );
//                    }

                done = true;
            }
        }
    }
    
    private static void performCBOUpdate( 
        Path bestInColonyCBO, PheromoneTable pheromoneTable, double mu )
    {
        Weights localWeights = new Weights( );
        localWeights.weightCBO = 1.0;
        localWeights.weightNAC = 0.0;
        localWeights.weightATMR = 0.0;
        
        layPheromoneForWholePathSolution( bestInColonyCBO, pheromoneTable, mu, localWeights );
    }
            
    private static void performNACUpdate( 
        Path bestInColonyNAC, PheromoneTable pheromoneTable, double mu )
    {
        Weights localWeights = new Weights( );
        localWeights.weightCBO = 0.0;
        localWeights.weightNAC = 1.0;
        localWeights.weightATMR = 0.0;
        
        layPheromoneForWholePathSolution( bestInColonyNAC, pheromoneTable, mu, localWeights );
    }
    
    
}   // end class

//------- end file ----------------------------------------

