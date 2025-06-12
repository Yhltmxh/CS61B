package gitlet;

import java.util.Arrays;

import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length > 1) {
                    Utils.exitWithError("Incorrect operands.");
                }
                doInit();
                break;
            case "add":
                if (args.length != 2) {
                    if (args.length < 2) {
                        Utils.exitWithError("Please enter a commit message.");
                    } else {
                        Utils.exitWithError("Incorrect operands.");
                    }
                }
                if (!GITLET_DIR.exists()) {
                    Utils.exitWithError("Not in an initialized Gitlet directory.");
                }
                doAdd(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    Utils.exitWithError("Incorrect operands.");
                }
                if (!GITLET_DIR.exists()) {
                    Utils.exitWithError("Not in an initialized Gitlet directory.");
                }
                doCommit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    Utils.exitWithError("Incorrect operands.");
                }
                if (!GITLET_DIR.exists()) {
                    Utils.exitWithError("Not in an initialized Gitlet directory.");
                }
                doRemove(args[1]);
                break;
            case "log":
                if (args.length > 1) {
                    Utils.exitWithError("Incorrect operands.");
                }
                doLog();
                break;
            case "global-log":
                if (args.length > 1) {
                    Utils.exitWithError("Incorrect operands.");
                }
                doGlobalLog();
                break;
            default: Utils.exitWithError("Incorrect operands.");
        }
    }
}
