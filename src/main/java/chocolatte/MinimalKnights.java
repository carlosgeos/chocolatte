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
    private Board board;

    private BoolVar knight_case_constraints(int i, int j, int d1, int d2) {
        if ((i + d1 >= 0 && i + d1 < board.boardSize)
                && (j + d2 >= 0 && j + d2 < board.boardSize)) {
            return board.knightsLocation[i + d1][j + d2];
        } else {
            return board.knightsLocation[i][j];
        }
    }

    public Solution exec() {
        for (int i = 0; i < board.boardSize; ++i) {
            for (int j = 0; j < board.boardSize; ++j) {
                board.model.or(
                    board.knightsLocation[i][j],
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
        BoolVar[] vars = Arrays.stream(board.knightsLocation)
            .flatMap(listContainer -> Arrays.stream(listContainer))
            .toArray(size -> new BoolVar[size]);

        board.model.sum(vars, "+", board.totalKnights).post();
        return board.model.getSolver().findOptimalSolution(board.totalKnights, Model.MINIMIZE);
    }

    public MinimalKnights(Board board) {
        this.board = board;
    }
}
