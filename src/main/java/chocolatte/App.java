package chocolatte;

import chocolatte.Domination;
import chocolatte.Museum;

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

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Chess pieces parser")
            .defaultHelp(true)
            .description("Returns a solution for the independence or domination problem for the given pieces (only rooks, knights and bishops)");
        MutuallyExclusiveGroup indom = parser.addMutuallyExclusiveGroup("Independence/Domination");
        indom.addArgument("-i").action(Arguments.storeTrue())
            .help("Solve the independence problem");
        indom.addArgument("-d").action(Arguments.storeTrue())
            .help("Solve the domination problem");
        indom.addArgument("-mk").action(Arguments.storeTrue())
            .help("Solve the minimum knights problem");
        indom.addArgument("-m").action(Arguments.storeTrue())
            .help("Solve the museum problem");
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
        parser.epilog("Usage with Gradle: \n$ gradle run -PappArgs=\"['-flag1', '-arg2 x', 'option3']\"");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args); // Fill the namespace with pass arguments
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        App app = new App();

        boolean domination = ns.getBoolean("d");
        boolean minimum_knights = ns.getBoolean("mk");
        int boardSize = ns.getInt("n");
        int rook = ns.getInt("t");
        int bishop = ns.getInt("f");
        int knight = ns.getInt("c");

        Board chessBoard = new Board(boardSize, rook, bishop, knight);
        Solution sol = null;

        if (ns.getBoolean("m")) {
            chessBoard.createMuseum();
            Museum min = new Museum(chessBoard);
            sol = min.exec();
            chessBoard.printMuseum(sol);
        } else if (minimum_knights) {
            chessBoard.createPotentialKnights();
            MinimalKnights min = new MinimalKnights(chessBoard);
            sol = min.exec();
            chessBoard.printKnightsBoard(sol);
        } else if (domination) {
            chessBoard.createPieces();
            Domination dom = new Domination(chessBoard);
            sol = dom.exec();
            chessBoard.printSolutionBoard(sol);
        } else {
            chessBoard.createPieces();
            Independence ind = new Independence(chessBoard);
            sol = ind.exec();
            chessBoard.printSolutionBoard(sol);
        }
    }
}
