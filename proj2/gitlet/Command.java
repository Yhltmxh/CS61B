package gitlet;

/**
 * @description: 命令枚举
 * @author: 杨怀龙
 * @create: 2025-06-13 10:03
 **/
public enum Command {

    INIT("init", "1"),
    ADD("add", "2"),
    COMMIT("commit", "2"),
    REMOVE("rm", "2"),
    LOG("log", "1"),
    GLOBAL_LOG("global-log", "1"),
    FIND("find", "2"),
    STATUS("status", "1"),
    CHECKOUT("checkout", "2,3,4"),
    BRANCH("branch", "2"),
    REMOVE_BRANCH("rm-branch", "2"),
    RESET("reset", "2"),
    MERGE("merge", "2"),
    ADD_REMOTE("add-remote", "3"),
    REMOVE_REMOTE("rm-remote", "2"),
    PUSH("push", "3"),
    FETCH("fetch", "3"),
    PULL("pull", "3");


    private String command;

    private String paramCount;

    Command(String command, String paramCount) {
        this.command = command;
        this.paramCount = paramCount;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParamCount() {
        return paramCount;
    }

    public void setParamCount(String paramCount) {
        this.paramCount = paramCount;
    }

    public static Command findCommand(String command) {
        for (Command c : values()) {
            if (c.command.equals(command)) {
                return c;
            }
        }
        return null;
    }
}
