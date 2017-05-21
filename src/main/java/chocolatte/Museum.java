package chocolatte;

import chocolatte.MuseumTypes;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.Collector;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.BoolVar;

public class Museum {
    static private MuseumTypes WALL = MuseumTypes.WALL;
    static private MuseumTypes EMPTY = MuseumTypes.EMPTY;
    static private MuseumTypes OUEST = MuseumTypes.OUEST;
    static private MuseumTypes EST = MuseumTypes.EST;
    static private MuseumTypes NORD = MuseumTypes.NORD;
    static private MuseumTypes SUD = MuseumTypes.SUD;

    private Board board;

    private IntVar[] get_ouest(int x, int y) {
        ArrayList<IntVar> result = new ArrayList<IntVar>();

        for (int i = x; i >= 0; --i) {
            if (!board.museumModel[i][y]) {
                break;
            } else {
                result.add(board.museum[i][y]);
            }
        }

        return result.stream().toArray(size -> new IntVar[size]);
    }

    private Constraint get_constraint_ouest(int x, int y) {
        int[] values = {EST.getValue()};
        IntVar nbVar = board.model.intVar("constraint_ouest_camera_" + x + "_" + y, 1, 1);
        IntVar[] fields = get_ouest(x, y);
        return board.model.among(nbVar, fields, values);
    }

    private IntVar[] get_est(int x, int y) {
        ArrayList<IntVar> result = new ArrayList<IntVar>();

        for (int i = x; i < board.boardSize; ++i) {
            if (!board.museumModel[i][y]) {
                break;
            } else {
                result.add(board.museum[i][y]);
            }
        }

        return result.stream().toArray(size -> new IntVar[size]);
    }

    private Constraint get_constraint_est(int x, int y) {
        int[] values = {OUEST.getValue()};
        IntVar nbVar = board.model.intVar("constraint_est_camera_" + x + "_" + y, 1, 1);
        IntVar[] fields = get_est(x, y);
        return board.model.among(nbVar, fields, values);
    }

    private IntVar[] get_nord(int x, int y) {
        ArrayList<IntVar> result = new ArrayList<IntVar>();

        for (int i = x; i >= 0; --i) {
            if (!board.museumModel[x][i]) {
                break;
            } else {
                result.add(board.museum[x][i]);
            }
        }

        return result.stream().toArray(size -> new IntVar[size]);
    }

    private Constraint get_constraint_nord(int x, int y) {
        int[] values = {SUD.getValue()};
        IntVar nbVar = board.model.intVar("constraint_nord_camera_" + x + "_" + y, 1, 1);
        IntVar[] fields = get_nord(x, y);
        return board.model.among(nbVar, fields, values);
    }

    private IntVar[] get_sud(int x, int y) {
        ArrayList<IntVar> result = new ArrayList<IntVar>();

        for (int i = x; i < board.boardSize; ++i) {
            if (!board.museumModel[x][i]) {
                break;
            } else {
                result.add(board.museum[x][i]);
            }
        }

        return result.stream().toArray(size -> new IntVar[size]);
    }

    private Constraint get_constraint_sud(int x, int y) {
        int[] values = {NORD.getValue()};
        IntVar nbVar = board.model.intVar("constraint_sud_camera_" + x + "_" + y, 1, 1);
        IntVar[] fields = get_sud(x, y);
        return board.model.among(nbVar, fields, values);
    }

    public Solution exec() {
        for (int i = 0; i < board.boardSize; ++i) {
            for (int j = 0; j < board.boardSize; ++j) {
                if (board.museumModel[i][j]) {
                    board.model.or(
                        get_constraint_ouest(i, j),
                        get_constraint_est(i, j),
                        get_constraint_nord(i, j),
                        get_constraint_sud(i, j)
                    ).post();
                } else {
                    board.model.arithm(board.museum[i][j], "=", WALL.getValue()).post();
                }
            }
        }

        // Sum ouest camera + est + nord + sud

        // Flatten matrix to list.
        // BoolVar[] vars = Arrays.stream(board.knightsLocation)
        //     .flatMap(listContainer -> Arrays.stream(listContainer))
        //     .toArray(size -> new BoolVar[size]);

        // board.model.sum(vars, "+", board.totalKnights).post();
        return board.model.getSolver().findSolution();
    }

    public Museum(Board board) {
        this.board = board;
    }
}
