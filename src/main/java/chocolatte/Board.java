package chocolatte;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collector;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class Board {
    /* Public attributes, accessible to our solvers */
    public Model model;
    public int boardSize;
    // Number of each piece to place
    public int rook;
    public int bishop;
    public int knight;
    // Actual problem variables (chess pieces)
    public IntVar[][] rooks;
    public IntVar[][] bishops;
    public IntVar[][] knights;

    public void createPieces(int n, int rook, int bishop, int knight) {
        // Creation of rooks
        this.rooks = new IntVar[rook][2];
        for (int r = 0; r < rook; r++) {
            this.rooks[r][0] = this.model.intVar("R_" + r + "_x", 0, n - 1);
            this.rooks[r][1] = this.model.intVar("R_" + r + "_y", 0, n - 1);
        }

        // Creation of bishops
        this.bishops = new IntVar[bishop][2];
        for (int b = 0; b < bishop; b++) {
            this.bishops[b][0] = this.model.intVar("B_" + b + "_x", 0, n - 1);
            this.bishops[b][1] = this.model.intVar("B_" + b + "_y", 0, n - 1);
        }

        // Creation of knights
        this.knights = new IntVar[knight][2];
        for (int k = 0; k < knight; k++) {
            this.knights[k][0] = this.model.intVar("K_" + k + "_x", 0, n - 1);
            this.knights[k][1] = this.model.intVar("K_" + k + "_y", 0, n - 1);
        }
    }

    public void printSolutionBoard(Solution chocosolution) {
        if(chocosolution != null) {
            System.out.println(chocosolution.toString());
            System.out.println();

            Table<Integer, Integer, String> chessboard = HashBasedTable.create();

            for (int r = 0; r < rook; r++) {
                int column = chocosolution.getIntVal(this.rooks[r][0]);
                int row = chocosolution.getIntVal(this.rooks[r][1]);
                chessboard.put(row, column, "T ");
            }

            for (int b = 0; b < bishop; b++) {
                int column = chocosolution.getIntVal(this.bishops[b][0]);
                int row = chocosolution.getIntVal(this.bishops[b][1]);
                chessboard.put(row, column, "F ");

            }

            for (int k = 0; k < knight; k++) {
                int column = chocosolution.getIntVal(this.knights[k][0]);
                int row = chocosolution.getIntVal(this.knights[k][1]);
                chessboard.put(row, column, "C ");
            }

            // Print based on Table chessboard
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {

                    if (chessboard.contains(i, j)) {
                        System.out.print(chessboard.get(i, j));
                    } else {
                        System.out.print("* ");
                    }
                    if (j == boardSize - 1) {
                        System.out.println();
                    }
                }
            }
        } else {
            System.out.println("NO SOLUTION TO THE GIVEN PROBLEM");
        }
    }

    public Board (int boardSize, int rook, int bishop, int knight) {
        this.model = new Model(boardSize + "-size chess problem");
        this.boardSize = boardSize;
        this.rook = rook;
        this.bishop = bishop;
        this.knight = knight;
        createPieces(boardSize, rook, bishop, knight);
    }
}
