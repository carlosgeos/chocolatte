import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

public class App {
    public void chess(int n) {
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

    public void independance(int n, int rook, int bishop, int knight) {
        Model model = new Model("Independance problem");

        IntVar[][] rooks = new IntVar[rook][2];
        for (int r = 0; r < rook; r++) {
            rooks[r][0] = model.intVar("R_" + r + "_x", 1, n);
            rooks[r][1] = model.intVar("R_" + r + "_y", 1, n);
        }

        IntVar[][] bishops = new IntVar[bishop][2];
        for (int b = 0; b < bishop; b++) {
            bishops[b][0] = model.intVar("B_" + b + "_x", 1, n);
            bishops[b][1] = model.intVar("B_" + b + "_y", 1, n);
        }

        // Conditions on rooks
        for (int i = 0; i < rook; i++) {
            for (int j = i + 1; j < rook; j++) {
                model.arithm(rooks[i][0], "!=", rooks[j][0]).post();
                model.arithm(rooks[i][1], "!=", rooks[j][1]).post();
            }

            for (int j = 0; j < bishop; j++) {
                model.arithm(rooks[i][0], "!=", bishops[j][0]).post();
                model.arithm(rooks[i][1], "!=", bishops[j][1]).post();
            }
        }

        // Conditions on bishops
        for (int i = 0; i < bishop; i++) {
            for (int j = i + 1; j < bishop; j++) {
                IntVar x = model.intVar("x", 1, n);
                model.absolute(x, bishops[i][0].sub(bishops[j][0]).intVar());

                IntVar y = model.intVar("y", 1, n);
                model.absolute(y, bishops[i][1].sub(bishops[j][1]).intVar());

                model.arithm(x, "!=", y).post();
            }

            for (int j = 0; j < rook; j++) {
                IntVar x = bishops[i][0].sub(rooks[j][0]).intVar();
                // model.absolute(x, bishops[i][0].sub(rooks[j][0]).intVar());
                model.absolute(x, x);
                System.out.println(x);

                IntVar y = bishops[i][1].sub(rooks[j][1]).intVar();
                // model.absolute(y, bishops[i][1].sub(rooks[j][1]).intVar());
                model.absolute(y, y);
                System.out.println(y);

                model.artith(x, "!=", y).post()

                // model.arithm(bishops[i][0].sub(rooks[j][0]).intVar(), "!=", bishops[i][1].sub(rooks[j][1]).intVar()).post();
            }

            // for (int j = 0; j < bishop; j++) {
            //     model.arithm(rooks[i][0], "!=", bishops[j][0]).post();
            //     model.arithm(rooks[i][1], "!=", bishops[j][1]).post();
            // }
        }


        // IntVar[] knights = new IntVar[knight];
        // for (int k = 0; k < knight; k++) {
        //     knights[b] = model.intVar("K_" + k, 1, knight);
        // }

        // for(int i  = 0; i < n-1; i++){
        //     for(int j = i + 1; j < n; j++){
        //         model.arithm(vars[i], "!=", vars[j]).post();
        //         model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
        //         model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
        //     }
        // }

        Solution solution = model.getSolver().findSolution();
        if(solution != null){
            System.out.println(solution.toString());
        }
    }

    public static void main(String[] args) {
        App app = new App();
        // app.chess(8);
        app.independance(3, 2, 1, 0);
    }
}
