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

import chocolatte.Board;

public class Domination {
    private Board board;

    private Constraint rook_constraints(IntVar[] a, IntVar[] b) {
        // Ensure the rook is not on the same case as the other piece.
        board.model.or(
            board.model.arithm(a[0], "!=", b[0]),
            board.model.arithm(a[1], "!=", b[1])
            ).post();

        return board.model.or(
            board.model.arithm(a[0], "=", b[0]),
            board.model.arithm(a[1], "=", b[1])
            );
    }

    private Constraint rooks_constraints(IntVar[] current, IntVar[][] other) {
        if (other.length > 0) {
            Constraint[] constraints = Arrays.stream(other)
                .map(x -> rook_constraints(current, x))
                .toArray(size -> new Constraint[size]);

            return board.model.or(constraints);
        }

        return board.model.falseConstraint();
    }

    private Constraint bishop_constraints(IntVar[] a, IntVar[] b) {
        board.model.or(
            board.model.arithm(a[0], "!=", b[0]),
            board.model.arithm(a[1], "!=", b[1])
            ).post();

        IntVar y = a[1].sub(b[1]).abs().intVar();
        return board.model.distance(a[0], b[0], "=", y);
    }

    private Constraint bishops_constraints(IntVar[] current, IntVar[][] other) {
        if (other.length > 0) {
            Constraint[] constraints = Arrays.stream(other)
                .map(x -> bishop_constraints(current, x))
                .toArray(size -> new Constraint[size]);

            return board.model.or(constraints);
        }

        return board.model.falseConstraint();
    }

    private Constraint knight_case_constraints(IntVar[] a, IntVar[] b, String o1, int d1, String o2, int d2) {
        return board.model.and(
            board.model.arithm(a[0], "=", b[0], o1, d1),
            board.model.arithm(a[1], "=", b[1], o2, d2)
            );
    }

    private Constraint knight_constraints(IntVar[] a, IntVar[] b) {
        board.model.or(
            board.model.arithm(a[0], "!=", b[0]),
            board.model.arithm(a[1], "!=", b[1])
            ).post();

        return board.model.or(
            knight_case_constraints(a, b, "+", 2, "+", 1),
            knight_case_constraints(a, b, "+", 2, "-", 1),
            knight_case_constraints(a, b, "-", 2, "-", 1),
            knight_case_constraints(a, b, "-", 2, "+", 1),
            knight_case_constraints(a, b, "+", 1, "+", 2),
            knight_case_constraints(a, b, "+", 1, "-", 2),
            knight_case_constraints(a, b, "-", 1, "-", 2),
            knight_case_constraints(a, b, "-", 1, "+", 2)
            );
    }

    private Constraint knights_constraints(IntVar[] current, IntVar[][] other) {
        if (other.length > 0) {
            Constraint[] constraints = Arrays.stream(other)
                .map(x -> knight_constraints(current, x))
                .toArray(size -> new Constraint[size]);

            return board.model.or(constraints);
        }

        return board.model.falseConstraint();
    }

    public Solution exec() {
        for (int i = 0; i < board.rooks.length; i++) {
            if (i + 1 < board.rooks.length) {
                board.model.or(
                    rooks_constraints(board.rooks[i], Arrays.copyOfRange(board.rooks, i + 1, board.rooks.length)),
                    rooks_constraints(board.rooks[i], board.bishops),
                    rooks_constraints(board.rooks[i], board.knights)
                    ).post();
            } else if (board.knights.length > 0 || board.bishops.length > 0){
                board.model.or(
                    rooks_constraints(board.rooks[i], board.bishops),
                    rooks_constraints(board.rooks[i], board.knights)
                    ).post();
            }
        }

        for (int i = 0; i < board.bishops.length; i++) {
            if (i + 1 < board.bishops.length) {
                board.model.or(
                    bishops_constraints(board.bishops[i], Arrays.copyOfRange(board.bishops, i + 1, board.bishops.length)),
                    bishops_constraints(board.bishops[i], board.rooks),
                    bishops_constraints(board.bishops[i], board.knights)
                    ).post();
            } else if (board.knights.length > 0 || board.rooks.length > 0){
                board.model.or(
                    bishops_constraints(board.bishops[i], board.rooks),
                    bishops_constraints(board.bishops[i], board.knights)
                    ).post();
            }
        }

        for (int i = 0; i < board.knights.length; i++) {
            if (i + 1 < board.knights.length) {
                board.model.or(
                    knights_constraints(board.knights[i], Arrays.copyOfRange(board.knights, i + 1, board.knights.length)),
                    knights_constraints(board.knights[i], board.rooks),
                    knights_constraints(board.knights[i], board.bishops)
                    ).post();
            } else if (board.bishops.length > 0 || board.rooks.length > 0){
                board.model.or(
                    knights_constraints(board.knights[i], board.rooks),
                    knights_constraints(board.knights[i], board.bishops)
                    ).post();
            }
        }

        return board.model.getSolver().findSolution();
    }

    public Domination(Board board) {
        this.board = board;
    }
}
