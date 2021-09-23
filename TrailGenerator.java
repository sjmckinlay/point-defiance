/* @author SJ McKinlay
 * Last updated Sep 23, 2021
 */

// It asks which park to visit, then writes a new .txt file
// the number of entries are based on the number of map nodes.
// It fills the new matrix with 99,999,999 in each field.
// This way, we can quickly see if any nodes are un-entered.
// Then, we ask which nodes are connected to each node, and 
// calculate the distance between the two nodes. We enter the 
// distance as array[i][j] in the matrix, where i is the node i, 
// and j is the node j.

import java.util.*;
import java.io.*;

class TrailGenerator {
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
        // printMatrix = printMatrix.replace(", ", " ");
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
                System.out.print("The distance between " + nodeD + " and " + conNode + " is ");
                System.out.println(connectMatrix[nodeD][conNode] + " feet.");
            }
        }
        return(connectMatrix);
    }
}
