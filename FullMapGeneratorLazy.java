// It asks which park to visit, and whether a map already exists.
// If a map exists, it prints the contents into the terminal.
// If a map does not exist, it writes a new .txt file , with the
// number of entries based upon the number of map nodes.
// It fills the new matrix with 99,999,999 in each field.
// Then it goes through and has the user input the (x,y,z) and 
// features of each node, along with how many other nodes connect 
// to that node.
// Then, we ask which nodes are connected to each node, and 
// calculate the distance between the two nodes. We enter the 
// distance as array[i][j] in the matrix, where i is the node i, 
// and j is the node j.

import java.util.*;
import java.io.*;

class FullMapGeneratorLazy {
    public static void main(String[] args) 
        throws FileNotFoundException {

        Scanner hikerEntries = new Scanner(System.in);

        System.out.println("Which park do you want to visit?");
        String parkName = hikerEntries.nextLine();
        System.out.println("You want to visit " + parkName + ".");

        System.out.println("What would you like the file name to be?");
        System.out.println("Please do not use any spaces.");
        String parkFileName = hikerEntries.next();
        String fileName = (parkFileName + ".txt");
        System.out.println("The park file will be stored at " + fileName + ".");

        int[][] mapMatrix = summonMatrix(hikerEntries, fileName);
        System.out.println(" ");
        System.out.println(" ");
        // System.out.println(Arrays.deepToString(mapMatrix));
        String firstMatrix = Arrays.deepToString(mapMatrix);
        int lenString = firstMatrix.length();
        String printMatrix = firstMatrix.substring(2, (lenString - 2));
        printMatrix = printMatrix.replace("], [", "\n");
        printMatrix = printMatrix.replace(", ", " ");
        PrintStream freshMatrix = new PrintStream(new File(fileName));
        freshMatrix.println(printMatrix);
    }

    public static int[][] summonMatrix(Scanner myHikerEntries, String myFileName) 
        throws FileNotFoundException {

        System.out.println("How many nodes are in the map for " + myFileName + "?");
        int numNodes = myHikerEntries.nextInt();
        System.out.println("There are " + numNodes + " nodes.");
        int[][] myMapMatrix = new int[numNodes][numNodes + 7];

        for (int a = 0; a < numNodes; a++) {
            for (int b = 0; b < numNodes ; b++) {
                myMapMatrix[a][b] = 99999999;
            }
            myMapMatrix[a][numNodes] = 10;
        }
        
        myMapMatrix = fillMatrixVals(myMapMatrix, numNodes);
        return(myMapMatrix);
    }

    public static int[][] fillMatrixVals (int[][] fMapMatrix, int fNumNodes) {
        Scanner fillScanner = new Scanner(System.in);
        for (int c = 0; c < fNumNodes; c++) {
            int d = c;
            System.out.println("What is node " + d + "'s x coordinate?");
            int xCoord = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 1] = xCoord;
            System.out.println("What is node " + d + "'s y coordinate?");
            int yCoord = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 2] = yCoord;
            System.out.println("What is node " + d + "'s z coordinate?");
            int zCoord = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 3] = zCoord;
            System.out.println("Is node " + d + " a viewpoint? Choose '0' for 'Yes.' ");
            int viewPt = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 4] = viewPt;
            System.out.println("Is node " + d + " a rest area? Choose '0' for 'Yes.'");
            int restArea = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 5] = restArea;
            System.out.println("Is node " + d + " a bathroom? Choose '0' for 'Yes.'");
            int bathRoom = fillScanner.nextInt();
            fMapMatrix[c][fNumNodes + 6] = bathRoom;
            System.out.println("How many nodes connect directly to node " + d + "?");
            int connectedNodes = fillScanner.nextInt();
            fMapMatrix = connectNodes(fillScanner, fMapMatrix, d, connectedNodes, fNumNodes);
        }
        return(fMapMatrix);
    }


    public static int[][] connectNodes (Scanner connectScanner, int[][] connectMatrix, int nodeD, int numCNodes, int cNumNodes) {
        System.out.println("There are " + numCNodes + " other nodes attached to node " + nodeD + ".");
        System.out.println(" ");
        for (int e = 0; e < numCNodes; e++) {
            System.out.println("What is the number of one of the nodes connected to node " + nodeD + "?");
            int conNode = connectScanner.nextInt();
            System.out.println("You said that node " + nodeD + " and " + conNode + " are connected.");
            if (connectMatrix[nodeD][conNode] > 9999999) { 
                System.out.println("What is the distance between " + nodeD + " and " + conNode + ", in feet?");
                int feetDist = connectScanner.nextInt();
                connectMatrix[nodeD][conNode] = feetDist;
                connectMatrix[conNode][nodeD] = feetDist;
            } else {
                System.out.println("The distance between " + nodeD + " and " + conNode + " is " + connectMatrix[nodeD][conNode] + " feet.");
            }
        }
        return(connectMatrix);
    }


}