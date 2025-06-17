package gitlet;


import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yhltmxh
 */
public class Main {
    public static void main(String[] args) {
        switch (argsCheck(args)) {
            case INIT -> doInit();
            case ADD -> doAdd(args[1]);
            case COMMIT -> doCommit(args[1]);
            case REMOVE -> doRemove(args[1]);
            case LOG -> doLog();
            case GLOBAL_LOG -> doGlobalLog();
            case FIND -> doFind(args[1]);
            case STATUS -> doStatus();
            case CHECKOUT -> doCheckout(args);
            case BRANCH -> doBranch(args[1]);
            case REMOVE_BRANCH -> doRemoveBranch(args[1]);
            case RESET -> doReset(args[1]);
            case MERGE -> doMerge(args[1]);
        }
    }
}
