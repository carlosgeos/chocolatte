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

public class Domination {
    private Model model;
    private IntVar[][] rooks;
    private IntVar[][] bishops;
    private IntVar[][] knights;

    private Constraint rook_constraints(IntVar[] a, IntVar[] b) {
        System.out.println(b[0]);
        this.model.or(
            this.model.arithm(a[0], "!=", b[0]),
            this.model.arithm(a[1], "!=", b[1])
        ).post();

        // XOR GATE
        return this.model.or(
            this.model.and(
                this.model.arithm(a[0], "=", b[0]),
                this.model.arithm(a[1], "!=", b[1])
            ), 
            this.model.and(
                this.model.arithm(a[0], "!=", b[0]),
                this.model.arithm(a[1], "=", b[1])
            )
        );
    }

    private Constraint rooks_constraints(Model model, IntVar[] current, IntVar[][] other) {
        if (other.length > 0) {
            Constraint[] constraints = Arrays.stream(other)
                .map(x -> rook_constraints(current, x))
                .toArray(size -> new Constraint[size]);

            return this.model.or(constraints);
        }

        return null;
    }

    private void bishop_constraints(Model model, IntVar[] a, IntVar[] b) {
        model.or(
            model.arithm(a[0], "!=", b[0]),
            model.arithm(a[1], "!=", b[1])
        ).post();

        IntVar y = a[1].sub(b[1]).abs().intVar();
        model.distance(a[0], b[0], "=", y).post();
    }

    private void bishops_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            bishop_constraints(model, current, other[j]);
        }
    }

    private void knight_case_constraints(Model model, IntVar[] a, IntVar[] b, String o1, int d1, String o2, int d2) {
        model.or(
            model.arithm(a[0], "=", b[0], o1, d1),
            model.arithm(a[1], "=", b[1], o2, d2)
        ).post();
    }

    private void knight_constraints(Model model, IntVar[] a, IntVar[] b) {
        model.or(
            model.arithm(a[0], "!=", b[0]),
            model.arithm(a[1], "!=", b[1])
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
            knight_constraints(model, current, other[j]);
        }
    }

    public Solution exec() {
        for (int i = 0; i < this.rooks.length; i++) {
            if (i + 1 < this.rooks.length) {
                this.model.or(
                    rooks_constraints(this.model, this.rooks[i], Arrays.copyOfRange(this.rooks, i + 1, this.rooks.length))
                    // rooks_constraints(this.model, this.rooks[i], this.bishops),
                    // rooks_constraints(this.model, this.rooks[i], this.knights)
                ).post();
            }
        }

        return this.model.getSolver().findSolution();
    }

    public Domination(int n, int rook, int bishop, int knight) {
        this.model = new Model("Domination problem");

        // Creation of rooks
        this.rooks = new IntVar[rook][2];
        for (int r = 0; r < rook; r++) {
            this.rooks[r][0] = model.intVar("R_" + r + "_x", 0, n - 1);
            this.rooks[r][1] = model.intVar("R_" + r + "_y", 0, n - 1);
        }

        // Creation of bishops
        this.bishops = new IntVar[bishop][2];
        for (int b = 0; b < bishop; b++) {
            this.bishops[b][0] = model.intVar("B_" + b + "_x", 0, n - 1);
            this.bishops[b][1] = model.intVar("B_" + b + "_y", 0, n - 1);
        }

        // Creation of knights
        this.knights = new IntVar[knight][2];
        for (int k = 0; k < knight; k++) {
            this.knights[k][0] = model.intVar("K_" + k + "_x", 0, n - 1);
            this.knights[k][1] = model.intVar("K_" + k + "_y", 0, n - 1);
        }
    }
}
