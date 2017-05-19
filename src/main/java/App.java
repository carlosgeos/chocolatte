import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

// Argparse
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.impl.Arguments;


public class App {
    private Model model;
    private IntVar[][] rooks;
    private IntVar[][] bishops;
    private IntVar[][] knights;

    private void rook_constraints(Model model, IntVar[] a, IntVar[] b) {
        this.model.arithm(a[0], "!=", b[0]).post();
        this.model.arithm(a[1], "!=", b[1]).post();
    }

    private void rooks_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            rook_constraints(this.model, current, other[j]);
        }
    }

    private void bishop_constraints(Model model, IntVar[] a, IntVar[] b) {
        IntVar y = a[1].sub(b[1]).abs().intVar();
        this.model.not(this.model.distance(a[0], b[0], "=", y)).post();
    }

    private void bishops_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            bishop_constraints(this.model, current, other[j]);
        }
    }

    private void knight_case_constraints(Model model, IntVar[] a, IntVar[] b, String o1, int d1, String o2, int d2) {
        this.model.or(
            this.model.arithm(a[0], "!=", b[0], o1, d1),
            this.model.arithm(a[1], "!=", b[1], o2, d2)
        ).post();
    }

    private void knight_constraints(Model model, IntVar[] a, IntVar[] b) {
        this.model.or(
            this.model.arithm(a[0], "!=", b[0]),
            this.model.arithm(a[1], "!=", b[1])
        ).post();

        knight_case_constraints(model, a, b, "+", 2, "+", 1);
        knight_case_constraints(model, a, b, "+", 2, "-", 1);
        knight_case_constraints(model, a, b, "-", 2, "-", 1);
        knight_case_constraints(model, a, b, "-", 2, "+", 1);

        knight_case_constraints(model, a, b, "+", 1, "+", 2);
        knight_case_constraints(model, a, b, "+", 1, "-", 2);
        knight_case_constraints(model, a, b, "-", 1, "-", 2);
        knight_case_constraints(model, a, b, "-", 1, "+", 2);
    }

    private void knights_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            knight_constraints(this.model, current, other[j]);
        }
    }

    // public void domination(int n, int rook, int bishop, int knight) {
    //     this.model = new Model("Domination problem");
    // }

    public void createPieces(int n, int rook, int bishop, int knight) {
        this.model = new Model(n + "-independance problem");
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

    public Solution independance(int n, int rook, int bishop, int knight) {
        // Conditions on rooks
        for (int i = 0; i < rook; i++) {
            for (int j = i + 1; j < rook; j++) {
                rook_constraints(this.model, this.rooks[i], this.rooks[j]);
            }

            rooks_constraints(this.model, this.rooks[i], this.bishops);
            rooks_constraints(this.model, this.rooks[i], this.knights);
        }

        // Conditions on bishops
        for (int i = 0; i < bishop; i++) {
            for (int j = i + 1; j < bishop; j++) {
                bishop_constraints(this.model, this.bishops[i], this.bishops[j]);
            }

            bishops_constraints(this.model, this.bishops[i], this.rooks);
            bishops_constraints(this.model, this.bishops[i], this.knights);
        }

        // Conditions on knights
        for (int i = 0; i < knight; i++) {
            for (int j = i + 1; j < knight; j++) {
                knight_constraints(this.model, this.knights[i], this.knights[j]);
            }

            knights_constraints(this.model, this.knights[i], this.rooks);
            knights_constraints(this.model, this.knights[i], this.bishops);
        }

        return this.model.getSolver().findSolution();
    }

    public Solution domination(int n, int rook, int bishop, int knight) {
        return new Solution(null);
    }

    public void print_solution_board(int board_size, int rook, int bishop, int knight, Solution chocosolution) {
        if(chocosolution != null){
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
            for (int i = 0; i < board_size; i++) {
                for (int j = 0; j < board_size; j++) {

                    if (chessboard.contains(i, j)) {
                        System.out.print(chessboard.get(i, j));
                    } else {
                        System.out.print("* ");
                    }
                    if (j == board_size - 1) {
                        System.out.println();
                    }
                }
            }
        } else {
            System.out.println("NULL SOLUTION");
        }
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Chess pieces parser")
            .defaultHelp(true)
            .description("Returns a solution for the independence or domination problem for the given pieces (only rooks, knights and bishops)");
        MutuallyExclusiveGroup indom = parser.addMutuallyExclusiveGroup("Independence/Domination");
        indom.addArgument("-i").action(Arguments.storeTrue())
            .help("Solve the independence problem");
        indom.addArgument("-d").action(Arguments.storeTrue())
            .help("Solve the domination problem");
        parser.addArgument("-n")
            .type(Integer.class)
            .help("Generate a chess board NxN")
            .setDefault(5);
        parser.addArgument("-t")
            .type(Integer.class)
            .help("Number of rooks to place on board")
            .setDefault(2);
        parser.addArgument("-f")
            .type(Integer.class)
            .help("Number of bishops to place on board")
            .setDefault(2);
        parser.addArgument("-c")
            .type(Integer.class)
            .help("Number of knights to place on board")
            .setDefault(2);
        parser.epilog("Usage with Gradle: \n$ gradle run -PappArgs['-flag1', '-arg2 x', 'option3']");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        App app = new App();

        boolean domination = ns.getBoolean("d");
        int board_size = ns.getInt("n");
        int rook = ns.getInt("t");
        int bishop = ns.getInt("f");
        int knight = ns.getInt("c");

        app.createPieces(board_size, rook, bishop, knight);
        Solution sol = null;
        if (domination) {
            sol = app.domination(board_size, rook, bishop, knight);
        } else {
            sol = app.independance(board_size, rook, bishop, knight);
        }
        app.print_solution_board(board_size, rook, bishop, knight, sol);
    }
}
