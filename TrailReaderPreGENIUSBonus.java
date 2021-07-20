// This program asks which park file to load.
// It then loads the file in question, and returns how many nodes
// are in the park.
// Maybe complete?

import java.util.*;
import org.graalvm.compiler.nodes.DeoptimizingNode.DeoptAfter;
import java.io.*;
import java.lang.*;

class TrailReaderPreGENIUSBonus{
    public static void main(String[] args) 
        throws FileNotFoundException, IOException {

        // Opening the scanner for user input.
        Scanner hikerEntries = new Scanner(System.in);

        // Ask hiker what the filename is, so we can find and load it.
        System.out.println("What is the file name for the park in question?");
        String fileName = hikerEntries.next();
        fileName = fileName.toLowerCase();
        fileName = (fileName + ".txt");
        System.out.println("\nLoading " + fileName + "... \n");
    
        // Look in the .txt file to see how many nodes are in the park.
        int numNodes = getNodeCount(fileName);
        
        // Ask the hiker how far they are willing to travel.
        double maxDist = assignMaxDistance(hikerEntries);
        int totDist = (int)maxDist;

        // Now, we open the existing .txt file, and fill the 2D array for this session.
        int[][] trailMap = loadTrail(hikerEntries, fileName, numNodes);

        // We create an ArrayList that will hold the recommended hike, as depicted by the series of nodes to visit.
        ArrayList<Integer> hikeTour = new ArrayList<Integer>(0);
        // We know that we will begin and end at the trailhead.
        hikeTour.add(0);
        hikeTour.add(0);

        // We create, then populate, a list of the allowed nodes.
        // As each one is crossed off, the corresponding entry is deleted.
        // So, it will never be seen as a match for an iteration of best node found.
        ArrayList <Integer> allowedNodes = new ArrayList(0);
        for(int a = 1; a < numNodes; a++) {
                allowedNodes.add(a);
        }


        // "Determine v_j in V \ T having the minimal ratio (d_0j + d_j0) / p_j and such that 
        // d_0j + d_j0 >= L."
        String firstDeparture = findDestination(allowedNodes, trailMap, numNodes, maxDist, 0, 0);
        //System.out.println(firstDeparture);
        String[] firstDep= firstDeparture.split(" ", 4);
        double testDub = Double.parseDouble(firstDep[1]);

        // "If no such route is feasible, stop."
        if (testDub >= maxDist) {
            System.out.println("I'm sorry, but no paths that fulfill your criteria exist.");
            System.out.println("Consider trying again, with a longer maximum hike length?");
            System.exit(0);
        }

        // Finds which node to add first, then adds it to our hikeTour.
        int addNode = Integer.parseInt(firstDep[0]);
        System.out.println("Let's add Node " + addNode + " to our route.");
        hikeTour.add(1, addNode);
        System.out.println("We now have a route that follows: " + hikeTour + ".\n");

        // Removes the distance used up by our first foray from our current maximum.
        maxDist = maxDist - (Double.parseDouble(firstDep[1]));
        System.out.println("Distance remaining = " + maxDist + ".");

        // Removes the mode used by our first foray from our remaining options.
        allowedNodes.remove((addNode - 1));
        System.out.println(allowedNodes + " are all still allowable nodes to visit.");
        
        // "If T = V, stop." Or, if every node has been visited, we stop our algorithm here.
        if(numNodes < 4) {
            System.out.println("Congratulations! You have visited the whole park. All two nodes of it...");
            System.exit(0);
        }

        // This method will find as many more nodes as it can squeeze into our hike.
        hikeTour = findHikeTour(trailMap, allowedNodes, hikeTour, maxDist, numNodes, addNode);
        System.out.println("We have a final tour suggestion of " + hikeTour + ".");

        String myFile = ("temp" + fileName);

        printMap(totDist, hikeTour, trailMap, myFile, numNodes);
    }

        public static void printMap (int totDist, ArrayList<Integer> hikeTour, int[][] mapMatrix, String fileName, int numNodes) 
            throws FileNotFoundException {
        
        // System.out.println(Arrays.deepToString(mapMatrix));
        String firstMatrix = Arrays.deepToString(mapMatrix);
        int lenString = firstMatrix.length();
        String printMatrix = firstMatrix.substring(2, (lenString - 2));
        printMatrix = printMatrix.replace("], [", "\n");
        printMatrix = printMatrix.replace(", ", " ");
        String hikeStrout = hikeTour.toString();
        int hikeLen = hikeStrout.length();
        String hikeStr = hikeStrout.substring(1, (hikeLen - 1));
        printMatrix = (numNodes + "\n" + totDist + "\n" + hikeStr + "\n" + printMatrix);
        printMatrix = printMatrix.replace(", ", " ");
        PrintStream freshMatrix = new PrintStream(new File(fileName));
        freshMatrix.println(printMatrix);
    }


    /** This method checks for the most optimal departure node, and where it should 
     * go to. If a value over the remaining max distance is received, it stops building 
     * the trail there, and returns the finished hikeTour array.*/
    public static ArrayList<Integer> findHikeTour (int[][] myTrailMap, ArrayList<Integer> myAllowedNodes, ArrayList<Integer> hikeTour, double myMaxDist, int numNodes, int myAddedNode) {

        int nodesLeft = myAllowedNodes.size();
        int currentInsert = 0;
        System.out.println(nodesLeft + " nodes are left.");

        int myAddNode = 0;
        double findDistLeft = 99999999;

        //Adding here in v.007
        for (int i = 0; i < nodesLeft; i++) {

            // What does our algorithm recommend, for our next departure node, 
            // and where our next addition goes to?
            String nextDeparture = findDepartureNode(myTrailMap, myAllowedNodes, hikeTour, myMaxDist, numNodes, myAddedNode);
            //System.out.println(nextDeparture);
            String[] nextDep = nextDeparture.split(" ", 4);
            findDistLeft = Double.parseDouble(nextDep[1]);

            // If we're running over the maximum distance, then we stop adding nodes
            // and return what we have so far.
            if(myMaxDist < findDistLeft){
                return(hikeTour);
            }
        
            currentInsert = (Integer.parseInt(nextDep[2]) + 1);
            myMaxDist = (myMaxDist - findDistLeft);
            myAddNode = Integer.parseInt(nextDep[0]); 
        

         
            hikeTour.add(currentInsert, myAddNode);
            myAllowedNodes.remove(Integer.valueOf(myAddNode));

            System.out.print("We have " + myMaxDist + " feet left, after adding ");
            System.out.println("node " + myAddNode + ".");
            System.out.println("Our remaining allowed nodes are " + myAllowedNodes + ".");
        }

        return(hikeTour);
    }


    /** This method finds the next ideal departure point from the existing tour, and
     * the most optimal node for it to visit. */

     public static String findDepartureNode(int[][] myTrailMap, ArrayList<Integer> myAllowedNodes, ArrayList<Integer> hikeTour, double myMaxDist, int numNodes, int myAddedNode) {

        //Added in 007
        int myDepartNode = 0;
        int addThisNode = 0;
        double myMinRatio = 99999999;
        double myMinDist = 99999999;
        int iterateHikeTour = (hikeTour.size() - 2);
        String departHere = (" ");

        for (int j = 0; j < iterateHikeTour; j++) {
            // Get all of our data from this hypothetical node to test, first.
            String findDepart = findDestination(myAllowedNodes, myTrailMap, numNodes, myMaxDist, hikeTour.get(j), hikeTour.get(j+1));
            String findDepartSplit[] = findDepart.split(" ", 4);
            int findNode = Integer.parseInt(findDepartSplit[0]);
            double findDist = Double.parseDouble(findDepartSplit[1]);
            double findRatio = Double.parseDouble(findDepartSplit[2]);

            // Test the new data against the old best option.
            if (findDist <= myMaxDist) {
                if (findRatio < myMinRatio) {
                    addThisNode = findNode;
                    myMinRatio = findRatio;
                    myMinDist = findDist;
                    myDepartNode = j;
                }
            }
        
        }

        // We want to return - which node we are inserting it after, which node we are adding, 
        // and how much distance it takes.
        departHere = (addThisNode + " " + myMinDist + " " + myDepartNode);
        return(departHere);
     }
    
// INITIAL TOUR, par 2, pp 541
    // "Determine v_j in V \ T having the minimal ratio (d_oj + d_j0) / p_j and such that d_0j + d_j0 < max length L"
    /** We want to find the best spot to visit, if we already know which point we're leaving from,
     *  and which point we're returning to. We check to make sure that each spot doesn't exceed our maximum 
     * allowed distance that remains available. */
    
        /** this method begins the initial tour, by assuming that we are going from the trailhead, back again,
        * with no intermediate stops before we calculate our answer. It checks for duplicate uses of a single node. */
    public static String findDestination (ArrayList<Integer> destOptions, int[][] trimMatrix, int numNodes, double distLeft, 
        int entryNode, int exitNode) {

        int minNode = 0;
        double minDist = 99999999;
        double minRatio = 99999999;
        double tempDist = 99999999;
        double tempRatio = 99999999;

        for(int a = 0; a < numNodes; a++) {
            double tempNumer = trimMatrix[entryNode][a];
            double tempDeno = trimMatrix[a][exitNode];
            // System.out.println(tempDist);
            tempDist = (tempNumer + tempDeno);

            // First, check if we have enough distance left for this move.
            // Also, check that the tempNode is a viable option
            // (it hasn't already been used)
            if(tempDist < distLeft){
                
                // Check if this node is still allowed / hasn't been used yet
                if(destOptions.contains(a)) {

                    // Calculate the ratio if we complete the current move.
                    double tempDenom = trimMatrix[a][numNodes];
                    tempRatio = ((tempDist)/(tempDenom));

                    // Check if the current ratio is better than the existing minimum, and if so, update the minimum.
                    if(tempRatio < minRatio) {
                        minNode = a;
                        minRatio = tempRatio;
                        minDist = tempDist;
                    }
                }
            } 
        }
    
        // We return a String with the node to insert, the total distance, and the ratio of distance-to-prize.
        // The departure point choosing method will pick apart the String and convert the individual values 
        // into the pair of ints and the double that it needs.
        String testDest = (minNode + " " + minDist + " " + minRatio);
        return(testDest);
    }


    
    /** This method creates a 2D array of doubles, and fills it with the values from our .txt file. 
     * It then returns the filled matrix back to our main method. */
    public static int[][] loadTrail (Scanner myHikerEntries, String txtName, int numNodes) 
        throws FileNotFoundException {

        int[][] myTrailMap = new int[numNodes][(numNodes + 7)];
        myTrailMap = fillValues(myTrailMap, txtName, numNodes);
        myTrailMap = addPoints(myHikerEntries, myTrailMap, numNodes);

        return(myTrailMap);
    }



    /** This method looks at the raw text file, and extracts the number 
     * of total nodes in the entire park. */
    public static int getNodeCount (String myTxtName) throws FileNotFoundException {
        File nodeFile = new File(myTxtName);
        Scanner nodeCountScan = new Scanner(nodeFile);
        String firstLine = nodeCountScan.nextLine();
        StringTokenizer strToken = new StringTokenizer(firstLine);
        int getNumNodes = strToken.countTokens();
        getNumNodes = (getNumNodes - 7);
        nodeCountScan.close();
        System.out.println("We have " + getNumNodes + " nodes in this park. \n");

        return(getNumNodes);
    }




    /** This method takes in the field values from the original text file. 
     * Then, it transfers them over into the blank matrix that was created 
     * in the loadTrail method, and returns the filled matrix. */
    public static int[][] fillValues(int[][] fillTrailMap, String fTxtName, int fillNumNodes) 
        throws FileNotFoundException {
        File fFile = new File(fTxtName);
        Scanner fillScanner = new Scanner(fFile);
        String aPlaceHold;
        String bPlaceHold;
        int bInt;
        for (int a = 0; a < fillNumNodes; a++){
            aPlaceHold = fillScanner.nextLine();
            String[] aPlace = aPlaceHold.split(" ");
            // System.out.println(Arrays.deepToString(aPlace));
            for (int b = 0; b < (fillNumNodes + 7); b++) {
                bPlaceHold = aPlace[b];
                bInt = Integer.parseInt(bPlaceHold);
                fillTrailMap[a][b] = bInt;
            }
        }
        fillScanner.close();
        return(fillTrailMap);
    }



    /** This method takes in the matrix from loadTrail, after the .txt file values have 
     * all been filled in. It changes the default prize point value to 1, to avoid /0 errors.
     * addPoints then asks the hiker to input their desired prize 
     * point values to be awarded for visiting each node. */
    public static int[][] addPoints (Scanner myHikerEntries, int[][] myMatrix, int numNodes) {

        // Assign all "prize" values to 1
        for (int c = 0; c < numNodes; c++) {
            myMatrix[c][numNodes] = 1;
        }
        System.out.println("How many specific nodes do you desire?");
        int wantNodes = myHikerEntries.nextInt();
        int dNode;
        int dImp;

        // This question assumes that we are starting at Node 0, and our last node 
        // is at Node (numNodes - 1). The hiker will enter "Node 1" as one of the 
        // first nodes reachable from their starting node of Node 0, for example.
        for(int d = 0; d < wantNodes; d++) {
            System.out.println("What is the number of one of the nodes that you desire?");
            dNode = myHikerEntries.nextInt();
    
            System.out.println("How important is it to visit Node " + dNode + "?");
            System.out.println("5 = 'nice,' 10 = 'want to,' 50 = 'very important,' and 1000 = 'MUST be in the hike.'");
            dImp = myHikerEntries.nextInt();
            myMatrix[dNode][numNodes] = dImp;
        }

        return(myMatrix);
    }


    /** assignMaxDistance just asks the hiker how far they are willing to travel, in miles.
     *  It converts the measurement to feet, and returns that to the main method. */
    public static double assignMaxDistance (Scanner genHikerEntries) {
        System.out.println("What is the maximum distance, in miles, that you wish to hike?");
        double maxMiles = genHikerEntries.nextDouble();
        double maxFeet = (maxMiles * 5280);
        System.out.println("\nYou want to travel " + maxFeet + " feet.");
        return(maxFeet);
    }


}