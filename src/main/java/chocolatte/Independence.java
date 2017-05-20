package chocolatte;

import chocolatte.Domination;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import chocolatte.Board;

public class Independence {
    private Board board;

    private void rook_constraints(Model model, IntVar[] a, IntVar[] b) {
        board.model.arithm(a[0], "!=", b[0]).post();
        board.model.arithm(a[1], "!=", b[1]).post();
    }

    private void rooks_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            rook_constraints(board.model, current, other[j]);
        }
    }

    private void bishop_constraints(Model model, IntVar[] a, IntVar[] b) {
        IntVar y = a[1].sub(b[1]).abs().intVar();
        board.model.not(board.model.distance(a[0], b[0], "=", y)).post();
    }

    private void bishops_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            bishop_constraints(board.model, current, other[j]);
        }
    }

    private void knight_case_constraints(Model model, IntVar[] a, IntVar[] b, String o1, int d1, String o2, int d2) {
        board.model.or(
            board.model.arithm(a[0], "!=", b[0], o1, d1),
            board.model.arithm(a[1], "!=", b[1], o2, d2)
            ).post();
    }

    private void knight_constraints(Model model, IntVar[] a, IntVar[] b) {
        board.model.or(
            board.model.arithm(a[0], "!=", b[0]),
            board.model.arithm(a[1], "!=", b[1])
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
            knight_constraints(board.model, current, other[j]);
        }
    }


    public Solution exec() {
        int rook = this.board.rook;
        int bishop = this.board.bishop;
        int knight = this.board.knight;
        // Conditions on rooks
        for (int i = 0; i < rook; i++) {
            for (int j = i + 1; j < rook; j++) {
                rook_constraints(board.model, board.rooks[i], board.rooks[j]);
            }

            rooks_constraints(board.model, board.rooks[i], board.bishops);
            rooks_constraints(board.model, board.rooks[i], board.knights);
        }

        // Conditions on bishops
        for (int i = 0; i < bishop; i++) {
            for (int j = i + 1; j < bishop; j++) {
                bishop_constraints(board.model, board.bishops[i], board.bishops[j]);
            }

            bishops_constraints(board.model, board.bishops[i], board.rooks);
            bishops_constraints(board.model, board.bishops[i], board.knights);
        }

        // Conditions on knights
        for (int i = 0; i < knight; i++) {
            for (int j = i + 1; j < knight; j++) {
                knight_constraints(board.model, board.knights[i], board.knights[j]);
            }

            knights_constraints(board.model, board.knights[i], board.rooks);
            knights_constraints(board.model, board.knights[i], board.bishops);
        }

        return board.model.getSolver().findSolution();
    }

    public Independence (Board board) {
        this.board = board;
    }

}
