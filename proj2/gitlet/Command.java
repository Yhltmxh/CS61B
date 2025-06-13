package gitlet;

/**
 * @description: 命令枚举
 * @author: 杨怀龙
 * @create: 2025-06-13 10:03
 **/
public enum Command {

    INIT("init", 1),
    ADD("add", 2),
    COMMIT("commit", 2),
    REMOVE("rm", 2),
    LOG("log", 1),
    GLOBAL_LOG("global-log", 1),
    FIND("find", 2),
    STATUS("status", 1),
    ;


    private String command;

    private int paramCount;

    Command(String command, int paramCount) {
        this.command = command;
        this.paramCount = paramCount;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getParamCount() {
        return paramCount;
    }

    public void setParamCount(int paramCount) {
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
