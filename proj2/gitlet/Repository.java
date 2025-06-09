package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  Deal with every commend.
 *
 *  @author YHL
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    public static void doInit() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }

        // 创建.gitlet
        createDirectory(GITLET_DIR);

        // 创建子目录
        File objects = join(GITLET_DIR, "objects");
        File refs = join(GITLET_DIR, "refs");
        File index = join(GITLET_DIR, "index");
        File HEAD = join(GITLET_DIR, "HEAD");

        createDirectory(objects);
        createDirectory(refs);
        createFile(index);
        createFile(HEAD);

        // 创建初始提交 todo: 提交过程后续可封装为方法
        Commit init = new Commit("initial commit", new Date(0L), new Commit(), new HashMap<>());
        String id = init.getId();

        File prefix = join(objects, id.substring(0, 2));
        createDirectory(prefix);

        File commitFile = join(prefix, id.substring(2));
        createFile(commitFile);

        writeObject(commitFile, init);
    }

}
