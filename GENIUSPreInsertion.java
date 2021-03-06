//** Restart from building blocks 2300 on 12/12 */
// This program currently asks the hiker for the filename of the existing .txt,
// and loads the existing suggested hike tour from Program Two.
// It also displays the number of total nodes in the park, the maximum allowed 
// hiking distance, the initial suggested tour, and the length, in feet, of 
// the initial suggested tour. 
// It then chooses three vertices arbitrarily from the initial suggested tour, 
// and then pulls a random fourth vertex to add to this list.

import java.util.*;
import org.graalvm.compiler.nodes.DeoptimizingNode.DeoptAfter;
import java.io.*;
import java.lang.*;

class GENIUSPreInsertion{

    public static void main(String[] args) 
        throws FileNotFoundException, IOException {

        // Opening the scanner for user input.
        Scanner hikerEntries = new Scanner(System.in);

        // Ask hiker what the filename is, so we can find and load it.
        System.out.println("What is the file name for the park in question?");
        String fileName = ("temp" + hikerEntries.next() + ".txt");
        //fileName = ("temp" + fileName.toLowerCase() + ".txt");
        System.out.println("\nLoading " + fileName + "... \n");
        hikerEntries.close();

        // Open the previous 'temp' file (all of the custom hiker prize amounts 
        // have already been entered).
        File fFile = new File(fileName);
        Scanner fillScanner = new Scanner(fFile);

        // How many nodes does this trail have?
        int numNodes = Integer.parseInt(fillScanner.nextLine());
        System.out.println("This park has " + numNodes + " nodes.");

        // What is the maximum distance that we can travel?
        int maxDist = Integer.parseInt(fillScanner.nextLine());
        System.out.println("Our maximum allowed distance is " + maxDist + " feet.");

        // What is the route that we generated in our second program?
        String firstRoute = fillScanner.nextLine();
        System.out.println("The initial recommended route is " + firstRoute + ".");
        
        // Loading in the current matrix, generated by our second program.
        int[][] trailMap = new int[numNodes][numNodes + 1];
        trailMap = fillValues(fillScanner, trailMap, numNodes);

        // Fill the first tour's stops into an ArrayList
        ArrayList<Integer> firstTour = new ArrayList<Integer>(0);
        firstTour = fillFirstTour(firstRoute);
        int numTourNodes = firstTour.size();
        // If we don't have enough nodes, then we can't run GENIUS.
        if (numTourNodes < 4) {
            System.out.println("Sorry, but GENIUS can't be applied to this park!");
            System.exit(0);
        } 

        // We want to make a deep copy of firstTour, to keep a running tally 
        // of which nodes we have left to add.
        ArrayList<Integer> remainingNodes = new ArrayList<Integer>(0);
        remainingNodes = fillFirstTour(firstRoute);

        // Find out what the cost of the pre-GENIUS tour is.
        int costPre = getCost(firstTour, trailMap);
        System.out.println(costPre + " is the cost of the tour, in feet, from Program Two.");

        ArrayList<Integer> testFlip = new ArrayList<Integer>(0);
        testFlip = flipTour(remainingNodes);
        //System.out.println(testFlip);

        // "Step 1. Create an initial tour by selecting an arbitrary subset of three vertices."
        ArrayList<Integer> initTour = new ArrayList<Integer>(0);
        initTour = findInitial(remainingNodes);
        // Right now, the remainingNodes ArrayList has all of the first tour nodes in it.
        // Let's delete the three values that we just took for our initial tour.
        remainingNodes.remove(Integer.valueOf(initTour.get(0)));
        remainingNodes.remove(Integer.valueOf(initTour.get(1)));
        remainingNodes.remove(Integer.valueOf(initTour.get(2)));
        
        System.out.println("Our initial tour is " + initTour + ".");
        System.out.println("Our remaining vertices are " + remainingNodes + ".");
        
        // "Step 2: Arbitrarily select a vertex v not yet on the tour."
        int addlNode = findAddlNode(remainingNodes, initTour);
        //Now, addlNode is no longer in the remainingNodes ArrayList.

        if(addlNode > 9999998) {
            System.out.println("Sorry, but GENIUS can't be applied to this park!");
            System.exit(0); 
        }

        
        // a Type I Insertion method
        // a Type II Insertion method
        // a method to hold them all together

    }


    /** This method simply flips, or reverses, the current tour's direction. 
     * @param preFlip is the tour to be flipped, in ArrayList form.
     * @return flipList as an ArrayList of integers */
    // *** RIGHT NOW, THIS METHOD ONLY RETURNS THE TOUR IN THE SAME *** //
    // ** ORDER AS IT ENTERS IN ** //
    public static ArrayList<Integer> flipTour(ArrayList<Integer> preFlip) {

        int preFlipSize = preFlip.size();
        int[] flipInts = new int[preFlipSize];
        ArrayList<Integer> flipList = new ArrayList<Integer>(0);

        for(int a = 0; a < preFlipSize ; a++){
            flipInts[(preFlipSize - (a + 1))] = preFlip.get(a);
        }

        for(int b = 0; b < preFlipSize; b++) {
            flipList.add(Integer.valueOf(flipInts[b]));
        }
        return(flipList);
    }


    /** This method selects a free vertex that exists outside of the HOA, er, the existing GENIUS tour.
     * @param myFirstTour is the tour generated by Program Two.
     * @param myInitTour is the tour of three randomly-selected starter vertices.
     * @return addlNode is the integer that is the next node that we will add. */
    public static int findAddlNode (ArrayList<Integer> myFirstTour, ArrayList<Integer> myInitTour) {

        ArrayList<Integer> myLeftOvers = new ArrayList<Integer>(0);
        int mFTSize = myFirstTour.size();
        for(int b = 0; b < mFTSize; b++) {
            myLeftOvers.add(Integer.valueOf(myFirstTour.get(b)));
        }
        myLeftOvers.remove(Integer.valueOf(myInitTour.get(0)));
        myLeftOvers.remove(Integer.valueOf(myInitTour.get(1)));
        myLeftOvers.remove(Integer.valueOf(myInitTour.get(2)));
        //System.out.println(leftOvers);
        Collections.shuffle(myLeftOvers);
        //System.out.println(myLeftOvers);

        int addlNode;
        if(myLeftOvers.size() >= 1) {
            addlNode = myLeftOvers.get(0);
            System.out.println("Let's add vertex " + addlNode + "!");
            myFirstTour.remove(Integer.valueOf(addlNode));
        } else {
            System.out.println("We have no more vertices to add. :( ");
            addlNode = 99999999;
        }

        return(addlNode);
    }


    /** "Step 1. Create an initial tour by selecting an arbitrary subset of three vertices."
     * @param myFirstTour0 is the tour initially suggested by Program Two, as an ArrayList.
     * @return nodeOpts is the ArrayList that holds the arbitrarily-selected three-vertex tour. */
    public static ArrayList<Integer> findInitial(ArrayList<Integer> myFirstTour0) {
        
        ArrayList<Integer> nodeOpts = new ArrayList<Integer>(0);
        myFirstTour0.remove(Integer.valueOf(0));
        Collections.shuffle(myFirstTour0);

        // First, we grab our three integers.
        int initVal0 = myFirstTour0.get(0);
        int initVal1 = myFirstTour0.get(1);
        int initVal2 = myFirstTour0.get(2);
        
        // Then, we place then into our future initial ArrayList trio.
        nodeOpts.add(0, initVal0);
        nodeOpts.add(0, initVal1);
        nodeOpts.add(0, initVal2);
    
        return(nodeOpts);
    }


    /** This method takes the String of the pre-GENIUS tour, and converts it into
     *  an ArrayList for later use.
     * @param myFirstRoute is the String of integers that show us the route that was 
     * generated by Program Two. 
     * @return myFirstTour is returned as an ArrayList type. */
    public static ArrayList<Integer> fillFirstTour(String myFirstRoute) {

        String[] firstRouteSplit = myFirstRoute.split(" ");
        ArrayList<Integer> myFirstTour = new ArrayList<Integer>(0);
        for (int a = 0; a < firstRouteSplit.length; a++) {
            myFirstTour.add(Integer.parseInt(firstRouteSplit[a]));
        }
        return(myFirstTour);
    }


    /** This method takes in the field values from the original text file. 
     * Then, it transfers them over into the blank matrix that was created 
     * in the loadTrail method, and returns the filled matrix. 
     * @param fillScan is the scanner that reads the .txt file.
     * @param fillTrailMap is the int[][] to be filled with the trail nodes and distances.
     * @param numNodes is simply the number of total nodes in the park.
     * @return fillTrailMap is returned to the 'main' method.*/
    public static int[][] fillValues(Scanner fillScan, int[][] fillTrailMap, int numNodes) 
        throws FileNotFoundException {
        
        String aPlaceHold;
        String bPlaceHold;
        int bInt;
        for (int a = 0; a < numNodes; a++){
            aPlaceHold = fillScan.nextLine();
            String[] aPlace = aPlaceHold.split(" ");
            // System.out.println(Arrays.deepToString(aPlace));
            for (int b = 0; b < (numNodes + 1); b++) {
                bPlaceHold = aPlace[b];
                bInt = Integer.parseInt(bPlaceHold);
                fillTrailMap[a][b] = bInt;
            }
        }
        return(fillTrailMap);
    }


    
    /** Given some tour, we want to find out how much that completed tour costs.
     * @param myHikeTour is the list of tour locations, in order.
     * @param myTrailMap is the trail map 2D array, with distances.
     * @return myHikeCost is the amount of feet the hiker must travel for this tour. */
    public static int getCost(ArrayList<Integer> myHikeTour, int[][] myTrailMap) {

        int myHikeCost = 0;
        int tempCost = 0;
        for (int d = 0; d < (myHikeTour.size() - 1); d++) {
            tempCost = myTrailMap[myHikeTour.get(d)][myHikeTour.get(d+1)];
            myHikeCost = myHikeCost + tempCost;
        }
        return(myHikeCost);
    }

}