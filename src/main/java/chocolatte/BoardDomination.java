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

public class BoardDomination {
    private int size;
    private Model model;
    private BoolVar[][] board;

    public Solution exec() {
        IntVar total = model.intVar("total", 0, size*size);

        // Flatten matrix to list.
        BoolVar[] vars = Arrays.stream(this.board)
            .flatMap(listContainer -> Arrays.stream(listContainer))
            .toArray(size -> new BoolVar[size]);

        this.model.sum(vars, "+", total);
        return this.model.getSolver().findOptimalSolution(total, Model.MINIMIZE);
    }

    public BoardDomination(int n) {
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
