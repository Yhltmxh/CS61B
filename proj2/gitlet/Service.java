package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;
import static gitlet.Utils.writeObject;

/**
 * @description: 业务处理
 * @author: Yhltmxh
 * @create: 2025-06-10 15:53
 **/
public class Service {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");




    /**
     * 保存提交对象
     * @param commit 提交对象
     */
    public static void saveCommit(Commit commit) {
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
    public static File saveBranch(String branchName, String commitId) {
        File branch = join(HEADS_DIR, branchName);
        createFile(branch);
        writeContents(branch, commitId);
        return branch;
    }

    /**
     * 普通文件转存为blob
     * @param source 源文件
     */
    public static void saveBlob(File source, String blobId) {
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
     * @param point 要指向的分支名（本项目没有分离头的状态）
     */
    public static void updateHead(String point) {
        Path base = Paths.get("refs").resolve("heads");
        writeContents(HEAD_FILE, String.format("ref: %s", base.resolve(point)));
    }

    /**
     * 更新分支
     * @param commitId 提交id
     * @param branch 分支文件
     */
    public static void updateBranch(String commitId, File branch) {
        writeContents(branch, commitId);
    }

    /**
     * 获取当前分支的文件对象
     * @return 文件对象
     */
    public static File getCurrentBranch() {
        // 获取当前head的位置
        String head = readContentsAsString(HEAD_FILE);
        return join(GITLET_DIR, head.substring(5));
    }

    /**
     * 获取所有分支文件名
     * @return 分支文件名集合
     */
    public static List<String> getAllBranches() {
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        return branches == null ? new ArrayList<>() : branches;
    }


    /**
     * 根据分支名获取分支头部的提交
     * @param branchName 分支名
     * @return 存在：提交对象，不存在：null
     */
    public static Commit getBranchHeadByName(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            return null;
        }
        String commitId = readContentsAsString(branchFile);
        File headCommit = join(COMMITS_DIR, commitId.substring(0, 2), commitId.substring(2));
        return readObject(headCommit, Commit.class);
    }


    /**
     * 获取当前提交
     * @return 当前提交对象
     */
    public static Commit getCurrentCommit() {
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
    public static Commit getCommitById(String commitId) {
        if (commitId == null || commitId.length() != UID_LENGTH) {
            return null;
        }
        File commit = join(COMMITS_DIR, commitId.substring(0, 2), commitId.substring(2));
        if (!commit.exists()) {
            return null;
        }
        return readObject(commit, Commit.class);
    }


    /**
     * 获取所有的提交id
     * @return id集合
     */
    public static List<String> getAllCommitId() {
        List<String> res = new ArrayList<>();
        // 获取commits/下所有的子目录名
        List<String> commitDir = plainDirectoryIn(COMMITS_DIR);
        if (commitDir == null) {
            return res;
        }
        for (String dir : commitDir) {
            // 获取子目录下的所有文件名
            List<String> commitFileName = plainFilenamesIn(join(COMMITS_DIR, dir));
            StringBuilder sb = new StringBuilder(dir);
            if (commitFileName == null) {
                continue;
            }
            // 将目录名与文件名进行拼接
            for (String fileName: commitFileName) {
                res.add(sb.append(fileName).toString());
            }
        }
        return res;
    }


    /**
     * 获取暂存区对象
     * @return 暂存区对象
     */
    public static Stage getStage() {
        return readObject(INDEX_FILE, Stage.class);
    }


    /**
     * 获取工作目录下所有的文件路径
     * @return 文件路径集合
     */
    public static List<String> getAllFilesInWorkDir() {
        List<String> res = new ArrayList<>();
        List<String> filenames = plainFilenamesIn(CWD);
        if (filenames == null) {
            return res;
        }
        for (String filename : filenames) {
            res.add(join(CWD, filename).getPath());
        }
        return res;
    }


    /**
     * 打印提交日志
     * @param commit 提交对象
     */
    public static void printCommitLog(Commit commit) {
        List<String> parents = commit.getParents();
        // 打印信息
        message("===");
        message("commit %s", commit.getId());
        if (parents.size() == 2) {
            message("Merge: %s %s", parents.get(0), parents.get(1));
        }
        message("Date: %s", getFormatDate(commit.getCreateTime()));
        message(commit.getMessage());
        System.out.println();
    }


    /**
     * 排序并打印集合
     * @param list 集合对象
     */
    public static void sortAndPrintList(List<String> list) {
        Collections.sort(list);
        for (String item : list) {
            message(item);
        }
        System.out.println();
    }


    /**
     * 从提交中检出目标文件
     * @param target 目标文件
     * @param commit 提交对象
     */
    public static void checkoutTargetBlobFromCommit(File target, Commit commit) {
        Map<String, String> commitBlobs = commit.getBlobs();
        if (!commitBlobs.containsKey(target.getPath())) {
            Utils.exitWithError("File does not exist in that commit.");
        }
        String blobId = commitBlobs.get(target.getPath());
        File blobFile = join(BLOBS_DIR, blobId.substring(0, 2), blobId.substring(2));
        copyFile(blobFile, target);
    }


    /**
     * 从提交中检出所有文件
     * @param commit 提交对象
     */
    public static void checkoutAllBlobFromCommit(Commit commit) {
        Map<String, String> commitBlobs = commit.getBlobs();
        for (String path : commitBlobs.keySet()) {
            String blobId = commitBlobs.get(path);
            File blobFile = join(BLOBS_DIR, blobId.substring(0, 2), blobId.substring(2));
            copyFile(blobFile, join(path));
        }
    }


    /**
     * todo: 看该方法是否需要，不需要最后删去
     * 检查暂存区中的文件是否存在，若不存在则删去
     * @param addStage 暂存区索引映射
     */
    public static void checkExist(Map<String, String> addStage) {
        for (String path : addStage.keySet()) {
            if (!Paths.get(path).toFile().exists()) {
                addStage.remove(path);
            }
        }
    }
}
