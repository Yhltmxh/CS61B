package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  Deal with every commend.
 *
 *  @author YHL
 */
public class Repository {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static void doInit() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }

        // 创建目录
        createDirectory(GITLET_DIR);
        createDirectory(OBJECTS_DIR);
        createDirectory(COMMITS_DIR);
        createDirectory(BLOBS_DIR);
        createDirectory(REFS_DIR);
        createDirectory(HEADS_DIR);
        createFile(INDEX_FILE);
        createFile(HEAD_FILE);

        // index文件初始化
        saveStage(new Stage(new HashMap<>(), new HashMap<>()));

        // 创建初始提交 todo: 提交过程后续可封装为方法
        Commit init = new Commit("initial commit", new Date(0L), new ArrayList<>(), new HashMap<>());
        saveCommit(init);

        // 创建master分支
        File master = saveBranch("master", init.getId());

        // 更新HEAD
        updateHead(master.getName());
    }

    public static void doAdd(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            exitWithError("File does not exist.");
        }
        String path = addFile.getPath();
        String blobId = sha1(fileName, readContents(addFile));

        // 转存blob
        saveBlob(addFile, blobId);

        // 拿到暂存区对象
        Stage stage = readObject(INDEX_FILE, Stage.class);
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();

        // 获取当前提交
        Commit cur = getCurrentCommit();
        Map<String, String> commitBlobs = cur.getBlobs();

        // 若当前版本与提交版本一致，则移除暂存区中的文件索引
        if (commitBlobs.containsKey(path) && commitBlobs.get(path).equals(blobId)) {
            addStage.remove(path);
        } else {
            addStage.put(path, blobId);
        }
        // todo: 文件add后重命名

        // 删除暂存区包含该文件则移除
        removeStage.remove(path);

        // 保存暂存区
        saveStage(stage);

    }

    public static void doCommit(String message) {
        Commit cur = getCurrentCommit();
        Map<String, String> blobs = cur.getBlobs();
        // 拿到暂存区对象
        Stage stage = readObject(INDEX_FILE, Stage.class);
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();

        if (addStage.isEmpty()) {
            exitWithError("No changes added to the commit.");
        }

        // 将添加暂存区的索引加入blobs
        for (String path : addStage.keySet()) {
            blobs.put(path, addStage.get(path));
        }

        // 将删除暂存区的索引从blobs中删除
        for (String path : removeStage.keySet()) {
            blobs.remove(path);
        }

        // 保存提交
        Commit commit = new Commit(message, new Date(), cur.getId(), blobs);
        saveCommit(commit);

        // 更新当前分支
        updateBranch(commit.getId(), getCurrentBranch());

        // 清空暂存区
        saveStage(new Stage(new HashMap<>(), new HashMap<>()));
    }

    public static void doRemove(String fileName) {
        File rmFile = join(CWD, fileName);
        String path = rmFile.getPath();
        // 拿到暂存区对象
        Stage stage = readObject(INDEX_FILE, Stage.class);
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();
        // 获取当前提交
        Commit cur = getCurrentCommit();
        Map<String, String> commitBlobs = cur.getBlobs();
        // 当前提交中有该文件的索引，则加入删除暂存区并在工作目录中删除
        if (commitBlobs.containsKey(path)) {
            removeStage.put(path, commitBlobs.get(path));
            if (rmFile.exists()) {
                deleteFile(rmFile);
            }
        } else if (addStage.containsKey(path)) {
            // 若添加暂存区存在该文件则删除
            addStage.remove(path);
        } else {
            exitWithError("No reason to remove the file.");
        }
        saveStage(stage);
    }


    public static void doLog() {
        // 获取当前提交
        Commit cur = getCurrentCommit();
        while (cur.getParents().size() > 0) {
            List<String> parents = cur.getParents();
            // 打印信息
            message("===");
            message("commit %s", cur.getId());
            if (parents.size() == 2) {
                message("Merge: %s %s", parents.get(0), parents.get(1));
            }
            message("Date: %s", getFormatDate(cur.getCreateTime()));
            message(cur.getMessage());
            System.out.println();
            // commit跳转
            cur = getCommitById(parents.get(0));
        }
    }








    /**
     * 保存提交对象
     * @param commit 提交对象
     */
    private static void saveCommit(Commit commit) {
        String id = commit.getId();

        File prefix = join(COMMITS_DIR, id.substring(0, 2));
        createDirectory(prefix);

        File commitFile = join(prefix, id.substring(2));
        createFile(commitFile);
        // 文件内容
        writeObject(commitFile, commit);
    }

    /**
     * 保存分支
     * @param branchName 分支名
     * @param commitId 提交id
     * @return 生成的分支对象
     */
    private static File saveBranch(String branchName, String commitId) {
        File branch = join(HEADS_DIR, branchName);
        createFile(branch);
        writeContents(branch, commitId);
        return branch;
    }

    /**
     * 普通文件转存为blob
     * @param source 源文件
     */
    private static void saveBlob(File source, String blobId) {
        File dir = join(BLOBS_DIR, blobId.substring(0, 2));
        File blobFile = join(dir, blobId.substring(2));
        // 前缀目录不存在就先创建目录
        if (!dir.exists()) {
            createDirectory(dir);
        }
        // 当前blob不存在则创建新blob
        if (!blobFile.exists()) {
            copyFile(source, blobFile);
        }
    }

    /**
     * 保存暂存区
     * @param stage
     */
    public static void saveStage(Stage stage) {
        writeObject(INDEX_FILE, stage);
    }

    /**
     * 更新HEAD
     * @param point 要更新的指向（本项目没有分离头的状态）
     */
    private static void updateHead(String point) {
        Path base = Paths.get("refs").resolve("heads");
        writeContents(HEAD_FILE, String.format("ref: %s", base.resolve(point)));
    }

    /**
     * 更新分支
     * @param commitId 提交id
     * @param branch 分支文件
     */
    private static void updateBranch(String commitId, File branch) {
        writeContents(branch, commitId);
    }

    private static File getCurrentBranch() {
        // 获取当前head的位置
        String head = readContentsAsString(HEAD_FILE);
        return join(GITLET_DIR, head.substring(5));
    }


    /**
     * 获取当前提交
     * @return 当前提交对象
     */
    private static Commit getCurrentCommit() {
        // 获取当前head的位置
        String head = readContentsAsString(HEAD_FILE);
        File branch = join(GITLET_DIR, head.substring(5));
        head = readContentsAsString(branch);
        File currentCommit = join(COMMITS_DIR, head.substring(0, 2), head.substring(2));
        return readObject(currentCommit, Commit.class);
    }

    /**
     * 获取指定提交对象
     * @param commitId 提交id
     * @return 提交对象
     */
    private static Commit getCommitById(String commitId) {
        File commit = join(COMMITS_DIR, commitId.substring(0, 2), commitId.substring(2));
        return readObject(commit, Commit.class);
    }

    /**
     * 检查暂存区中的文件是否存在，若不存在则删去
     * @param addStage 暂存区索引映射
     */
    private static void checkExist(Map<String, String> addStage) {
        for (String path : addStage.keySet()) {
            if (!Paths.get(path).toFile().exists()) {
                addStage.remove(path);
            }
        }
    }
}
