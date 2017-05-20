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
import org.chocosolver.solver.variables.BoolVar;

public class MinimalKnights {
    private int size;
    private Model model;
    private BoolVar[][] board;

    private BoolVar knight_case_constraints(int i, int j, int d1, int d2) {
        if ((i + d1 >= 0 && i + d1 < this.size)
                && (j + d2 >= 0 && j + d2 < this.size)) {
            return this.board[i + d1][j + d2];
        } else {
            return this.board[i][j];
        }
    }

    public Solution exec() {
        IntVar total = model.intVar("total", 0, size*size);

        for (int i = 0; i < this.size; ++i) {
            for (int j = 0; j < this.size; ++j) {
                this.model.or(
                    this.board[i][j],
                    knight_case_constraints(i, j, 2, 1),
                    knight_case_constraints(i, j, 2, -1),
                    knight_case_constraints(i, j, -2, -1),
                    knight_case_constraints(i, j, -2, 1),
                    knight_case_constraints(i, j, 1, 2),
                    knight_case_constraints(i, j, 1, -2),
                    knight_case_constraints(i, j, -1, -2),
                    knight_case_constraints(i, j, -1, 2)
                ).post();
            }
        }

        // Flatten matrix to list.
        BoolVar[] vars = Arrays.stream(this.board)
            .flatMap(listContainer -> Arrays.stream(listContainer))
            .toArray(size -> new BoolVar[size]);

        this.model.sum(vars, "+", total).post();
        return this.model.getSolver().findOptimalSolution(total, Model.MINIMIZE);
    }

    public MinimalKnights(int n) {
        this.size = n;
        this.model = new Model("Board Domination problem");

        this.board = new BoolVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.board[i][j] = model.boolVar("k_" + i + "_" + j);
            }
        }
    }
}
