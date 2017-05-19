import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

public class App {
    public void queenl(int n) {
        Model model = new Model(n + "-queens problem");
        IntVar[] vars = new IntVar[n];

        for (int q = 0; q < n; q++) {
            vars[q] = model.intVar("Q_" + q, 1, n);
        }

        for(int i  = 0; i < n-1; i++){
            for(int j = i + 1; j < n; j++){
                model.arithm(vars[i], "!=", vars[j]).post();
                model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
                model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
            }
        }
        Solution solution = model.getSolver().findSolution();
        if (solution != null){
            System.out.println(solution.toString());
        }
    }

    private void rook_constraints(Model model, IntVar[] a, IntVar[] b) {
        model.arithm(a[0], "!=", b[0]).post();
        model.arithm(a[1], "!=", b[1]).post();
    }

    private void rooks_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            rook_constraints(model, current, other[j]);
        }
    }

    private void bishop_constraints(Model model, IntVar[] a, IntVar[] b) {
        IntVar y = a[1].sub(b[1]).abs().intVar();
        model.not(model.distance(a[0], b[0], "=", y)).post();
    }

    private void bishops_constraints(Model model, IntVar[] current, IntVar[][] other) {
        for (int j = 0; j < other.length; j++) {
            bishop_constraints(model, current, other[j]);
        }
    }

    private void knight_case_constraints(Model model, IntVar[] a, IntVar[] b, String o1, int d1, String o2, int d2) {
        model.or(
            model.arithm(a[0], "!=", b[0], o1, d1),
            model.arithm(a[1], "!=", b[1], o2, d2)
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

    public void independance(int n, int rook, int bishop, int knight) {
        Model model = new Model("Independance problem");

        // Creation of rooks
        IntVar[][] rooks = new IntVar[rook][2];
        for (int r = 0; r < rook; r++) {
            rooks[r][0] = model.intVar("R_" + r + "_x", 0, n - 1);
            rooks[r][1] = model.intVar("R_" + r + "_y", 0, n - 1);
        }

        // Creation of bishops
        IntVar[][] bishops = new IntVar[bishop][2];
        for (int b = 0; b < bishop; b++) {
            bishops[b][0] = model.intVar("B_" + b + "_x", 0, n - 1);
            bishops[b][1] = model.intVar("B_" + b + "_y", 0, n - 1);
        }

        // Creation of knights
        IntVar[][] knights = new IntVar[knight][2];
        for (int k = 0; k < knight; k++) {
            knights[k][0] = model.intVar("K_" + k + "_x", 0, n - 1);
            knights[k][1] = model.intVar("K_" + k + "_y", 0, n - 1);
        }

        // Conditions on rooks
        for (int i = 0; i < rook; i++) {
            for (int j = i + 1; j < rook; j++) {
                rook_constraints(model, rooks[i], rooks[j]);
            }

            rooks_constraints(model, rooks[i], bishops);
            rooks_constraints(model, rooks[i], knights);
        }

        // Conditions on bishops
        for (int i = 0; i < bishop; i++) {
            for (int j = i + 1; j < bishop; j++) {
                bishop_constraints(model, bishops[i], bishops[j]);
            }

            bishops_constraints(model, bishops[i], rooks);
            bishops_constraints(model, bishops[i], knights);
        }

        // Conditions on knights
        for (int i = 0; i < knight; i++) {
            for (int j = i + 1; j < knight; j++) {
                knight_constraints(model, knights[i], knights[j]);
            }

            knights_constraints(model, knights[i], rooks);
            knights_constraints(model, knights[i], bishops);
        }

        Solution solution = model.getSolver().findSolution();
        if(solution != null){
            System.out.println(solution.toString());
        }
    }

    public static void main(String[] args) {
        App app = new App();
        // app.chess(8);
        app.independance(3, 1, 1, 1);
    }
}
