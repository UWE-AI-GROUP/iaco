/*
 * DaemonOperators.java
 * created 19 October 2011
 */
package daemonActions;

import engine.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import problem.CLSDatum;
import problem.ProblemController;
import softwareDesign.CLSClass;
import softwareDesign.EleganceDesign;
import myUtils.Utility;


/**
 * @author Christopher Simons
 */


public class DaemonOperators 
{
    /**
     * calculate the coupling and average COM cohesion for
     * a design solution path i.e. a complete path
     * @param the vertices of the path
     * @param reference to useTable 
     */
    public static void calculateDesignSolutionPathFitness( 
        Path path, SortedMap< String, List< CLSDatum > > useTable )
    {
        EleganceDesign ed = constructDesignFromPath( path );
//        System.out.println( "number of classes is: " +
//           ed.getNumberOfClasses( ) );
        
//        String s = "";
//        
//        // testing, 18 October 2011
//        if( ed.getNumberOfClasses( ) == 4 )
//        {
//            Iterator< Node > it = path.iterator( );
//            while( it.hasNext( ) )
//            {
//                Node v = it.next( );
//                s += v.getNumber( ) + " " + v.getName( ) + ", ";
//                
//                if( v instanceof EndOfClass )
//                {
//                    System.out.println( "\t" + s );
//                    s = "";
//                }
//            }
//            System.out.println( );
//        }

        // determine the external coupling for that solution design path
        ed.calculateExternalCoupling( useTable );
        // and record the coupling value with the solution
        final double externalCoupling = ed.getExternalCouplingValue( );
        //System.out.println("in daemon operators calculateDesignSolutionPathFitness, coupling = " + externalCoupling);
        path.setCBO( externalCoupling );
//        System.out.println("external coupling is: " + externalCoupling );

        // determine the average COM cohesion for the solution
        // 25 April 2012 - comment out, as cohesion is not used for update
//        ed.calculateCOMFitness( useTable );
        
          // and record with the path
//        final double averageCOMCohesion = ed.getAverageCOMCohesion( ); 
//        
//        assert averageCOMCohesion >= 0.0;
//        assert averageCOMCohesion <= 1.0;
//        
//        path.setAverageCOMCohesion( averageCOMCohesion );
//        System.out.println("average COM cohesion is: " + averageCOMCohesion );
        
        // 3 May 2012
        ed.calculateEleganceNAC( );
        final double eleganceNAC = ed.getEleganceNAC( ); 
        path.setEleganceNAC( eleganceNAC );
//        System.out.println( "path NAC elegance is: " + path.getEleganceNAC( ) );
        
        ed.calculateEleganceATMR( );
        final double eleganceATMR = ed.getEleganceATMR( );
        path.setEleganceATMR( eleganceATMR ); 
//        System.out.println( "path ATMR elegance is: " + path.getEleganceATMR( ) );
        
        // 3 August 2012
        ed.calculateEleganceModularity( useTable );
        final double em = ed.getEleganceModularity( );
        path.setEleganceModularity( em );
//        System.out.println( "path elegance mod is: " + path.getEleganceModularity( ) );
        
    }
    
    /**
     * construct a software design from the vertices in the path
     * 
     * Note that it is possible for a class within a design to 
     * contain zero attributes and/or zero methods...
     * 
     * Could ignore? repair??
     * 
     * Best to follow the ACO way, and award the worst possible
     * coupling value, so this path is unlikely to be selected
     * again.
     * 
     * @param the path from which to construct a software design
     * @return the constructed software design
     */
    public static EleganceDesign constructDesignFromPath( Path path )
    {
        assert path != null;
        assert path.isEmpty( ) == false;
//        path.show( );
        
        EleganceDesign ed = new EleganceDesign( );
        
        Iterator< Node > it = path.iterator( );
        CLSClass c = new CLSClass( );
        Node node = null;
        
        while( it.hasNext( ) )
        {
            node = it.next( );
            
            if( node instanceof Nest  )
            {
                // do nothing, just the start dummy node
            }
            else if( node instanceof Attribute )
            {
                c.add( ( Attribute ) node );
            }
            else if( node instanceof Method )
            {
                c.add( ( Method ) node );
            }
            else if( node instanceof EndOfClass )
            {
//                if( c.getNumberOfAttributes( ) == 0 )
//                {
//                    System.out.print( "no attributes " );
//                }
//                else if( c.getNumberOfMethods( ) == 0 )
//                {
//                    System.out.print( "no methods " );
//                }
                
                ed.add( c );
                c = new CLSClass( );
            }
            else
            {
                assert false: "invalid type of vertex!!";
            }
        
        }   // end while
        
//        System.out.println( );
//        System.out.println( );
        
        
        return ed;
    }
    
    
    /**
     * calculate the COM cohesion for a partial solution path 
     * i.e. a design class
     * @param the vertices of the path
     * @param reference to useTable 
     */
    public static void calculateClassPartialSolutionFitness( 
        Path partialPath, SortedMap< String, List< CLSDatum > > useTable )
    {
        CLSClass c = constructClassfromVertices( partialPath );
        // determine the cohesion of the class
        c.calculateCOMFitness( useTable );
        // and record with the partial solution
        final double cohesion = c.getCOMFitness( );
        partialPath.setCohesion( cohesion );
//        System.out.println("\t" + "cohesion is: " + cohesion );
    }
        
    
   /**
     * Construct a class (CLSClass) from vertices
     * @param reference to vertices
     * @return CLSClass
     */
    private static CLSClass constructClassfromVertices( Path v )
    {
        Iterator< Node > it = v.iterator( );
        CLSClass c = new CLSClass( );
        Node vertex = null;
        
        while( it.hasNext( ) )
        {
            vertex = it.next( );
            
            if( vertex instanceof Nest  )
            {
                // do nothing, just the start dummy node
            }
            else if( vertex instanceof Attribute )
            {
                c.add( ( Attribute ) vertex );
            }
            else if( vertex instanceof Method )
            {
                c.add( ( Method ) vertex );
            }
            else if( vertex instanceof EndOfClass )
            {
                // ?? do nothing at this stage, we're done!
                
//                if( c.getNumberOfAttributes( ) == 0 )
//                {
//                    System.out.print( "no attributes " );
//                }
//                else if( c.getNumberOfMethods( ) == 0 )
//                {
//                    System.out.print( "no methods " );
//                }
               
            }
            else
            {
                assert false: "invalid type of vertex!!";
            }
        
        }   // end while
        
//        System.out.println( );
//        System.out.println( );
        
        
        return c;
    }
    
    /**
     * calculate fitness values for a design solution path
     * using the graph structure and the use matrix
     * 
     * IMPORTANT: a path is made up of a list of nodes,
     * but the number of the node is the number of the element 
     * (attribute or method) PLUS ONE FOR THE NEST
     * 
     * So when indexing into the use matrix, we subtract one from
     * the value of the number
     * 
     * @param path
     * @param problem controller
     */
    public static void calculateDesignSolutionPathFitness(
        Path path, ProblemController problemController )
    {
        assert path != null;
        assert problemController != null;
        
        final int[ ][ ] useMatrix = problemController.getUseMatrix( );
        assert useMatrix != null;

        final int numberOfAttributes = problemController.getNumberOfUniqueData( );
        assert numberOfAttributes > 0;
        
        final int numberOfMethods = problemController.getNumberOfUniqueActions( );
        assert numberOfMethods > 0;
        
        // for calculation of CBO
        final int numberOfUses = problemController.getNumberOfUses( );
        assert numberOfUses > 0;
        int internalUses = 0;
        
        // for calculation of NAC     
        final int numberOfClasses = problemController.getNumberOfClasses( );
        assert numberOfClasses > 0;
        int[ ] elementCounts = new int[ numberOfClasses ];
        int classCounter = 0;
        
        // for calculation of ATMR
        double[ ] ratios = new double[ numberOfClasses ];
        
        List< Method > classMethodList = new ArrayList< >( );
        List< Attribute > classAttributeList = new ArrayList< >( );
        
        // now iterate along the nodes in the solution path
        Iterator< Node > it = path.iterator( );
        
        while( it.hasNext( ) )
        {
            Node n = it.next( );
            
            if( n instanceof Nest )
            {
                // do nothing
            }
            else if( n instanceof Method )
            {
                classMethodList.add( (Method) n );
            }
            else if( n instanceof Attribute )
            {
                classAttributeList.add( (Attribute) n );
            }
            else if( n instanceof EndOfClass )
            {
                // we have a class, so calculate fitness
                
                int[ ] methodNumbers = new int[ classMethodList.size( ) ];
                int[ ] attributeNumbers = new int[ classAttributeList.size( ) ];
                
                int mIndex = 0;
                int aIndex = 0;
                
                for( Method m : classMethodList )
                {
                    int number = m.getNumber( );
                    number -= numberOfAttributes;
                    assert number >= 0;
                    assert number <= numberOfMethods;
                    
                    methodNumbers[ mIndex ] = number - 1;   // minus one for the nest
                    mIndex++;
                }    
                
                for( Attribute a : classAttributeList )
                {
                    int number = a.getNumber( );
                    attributeNumbers[ aIndex ] = number - 1; // minus one for the nest
                    aIndex++;
                }    
                
                if( mIndex > 0 && aIndex > 0 ) // are there both attributes and methods?
                {
                    // for calculation of CBO
                    for( int i = 0; i < methodNumbers.length; i++ )
                    {
                        for( int j = 0; j < attributeNumbers.length; j++ )
                        {
                            if( useMatrix[ methodNumbers[ i ] ][ attributeNumbers[ j ] ] == 1 )
                            {
                                internalUses++; // must be an internal use
                            }
                        }
                    }
                    
                    // for calculation of ATMR
                    ratios[ classCounter ] =
                        (double) classAttributeList.size( ) / (double) classMethodList.size( );
                }
                
                // check post-conditions for this class
                assert classAttributeList.size( ) == attributeNumbers.length;
                assert classMethodList.size( ) == methodNumbers.length;
                
                // for calculation of NAC 
                // (doesn't matter if class lacks attributes or methods)  
                elementCounts[ classCounter ] = 
                    classAttributeList.size( ) + classMethodList.size( );
                
                classCounter++;
                
                // lastly, clear out the lists for next class
                classMethodList.clear( );
                classAttributeList.clear( );
            }
            else
            {
                assert true : "impossible node type!!";
            }
             
        }   // end while there's another node in the solution path
        
        // establish post-conditions for the solution path
        assert classCounter == numberOfClasses;
        if( internalUses == numberOfUses )
        {
//            System.out.println( "suspect path is: ---------------------");
//            Iterator< Node > it2 = path.iterator( );
//            while( it2.hasNext( ) )
//            {
//                Node n = it2.next( );
//                System.out.println( "\t" + n.getNumber( ) + " " + n.getName( ) );
//            }
        }
        assert internalUses <= numberOfUses :
            "internal uses is: " + internalUses +
            ", number of uses is: " + numberOfUses;
        
        // calculate CBO as a minimisation function       
        final double CBO = 1.0 - ( (double) internalUses / (double) numberOfUses );
        assert CBO >= 0.0;
        assert CBO <= 1.0;

        path.setCBO( CBO );
        
        // calculate NAC
        final double NAC = Utility.standardDeviation( elementCounts );
        assert NAC >= 0.0;
        
        path.setEleganceNAC( NAC );
        
        // calculate ATMR
        final double ATMR = Utility.standardDeviation( ratios );
        assert ATMR >= 0.0;
        
        path.setEleganceATMR( ATMR );
    }
    
}   // end class

//------- end file ----------------------------------------

