package gitlet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Service.*;
import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  Deal with every commend.
 *
 *  @author Yhltmxh
 */
public class Repository {


    public static Command argsCheck(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        Command command = Command.findCommand(args[0]);
        if (command == null) {
            Utils.exitWithError("No command with that name exists.");
        } else if (args.length != command.getParamCount()) {
            Utils.exitWithError("Incorrect operands.");
        } else if (!args[0].equals(Command.INIT.getCommand()) && !GITLET_DIR.exists()) {
            Utils.exitWithError("Not in an initialized Gitlet directory.");
        }
        return command;
    }


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


    public static void doAdd(String filename) {
        File addFile = join(CWD, filename);
        if (!addFile.exists()) {
            exitWithError("File does not exist.");
        }
        String path = addFile.getPath();
        String blobId = sha1(filename, readContents(addFile));

        // 转存blob
        saveBlob(addFile, blobId);

        // 拿到暂存区对象
        Stage stage = getStage();
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
        if (message.isBlank()) {
            Utils.exitWithError("Please enter a commit message.");
        }

        Commit cur = getCurrentCommit();
        Map<String, String> blobs = cur.getBlobs();
        // 拿到暂存区对象
        Stage stage = getStage();
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
        Stage stage = getStage();
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
        Commit cur = getCurrentCommit();
        while (cur.getParents().size() > 0) {
            printCommitLog(cur);
            cur = getCommitById(cur.getParents().get(0));
        }
    }


    public static void doGlobalLog() {
        List<String> commitIds = getAllCommitId();
        for (String id : commitIds) {
            printCommitLog(getCommitById(id));
        }
    }


    public static void doFind(String commitMessage) {
        List<String> commitIds = getAllCommitId();
        // 标记是否找到匹配的提交
        boolean isFind = false;
        for (String id : commitIds) {
            Commit commit = getCommitById(id);
            if (commitMessage.equals(commit.getMessage())) {
                isFind = true;
                message(id);
            }
        }
        if (!isFind) {
            exitWithError("Found no commit with that message.");
        }
    }


    public static void doStatus() {
        // 分支展示
        message("=== Branches ===");
        String currentBranch = getCurrentBranch().getName();
        List<String> allBranches = getAllBranches();
        Collections.sort(allBranches);
        for (String branch : allBranches) {
            if (currentBranch.equals(branch)) {
                message(String.format("*%s", branch));
            } else {
                message(branch);
            }
        }
        System.out.println();

        // 获取暂存区
        Stage stage = getStage();
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();
        // 添加暂存区展示
        message("=== Staged Files ===");
        sortAndPrintList(new ArrayList<>(addStage.keySet())
                .stream().map(path -> join(path).getName()).collect(Collectors.toList()));
        // 删除暂存区展示
        message("=== Removed Files ===");
        sortAndPrintList(new ArrayList<>(removeStage.keySet())
                .stream().map(path -> join(path).getName()).collect(Collectors.toList()));

        Set<String> allTrackedFiles = getAllTrackedFiles();
        List<String> allFilesInWorkDir = getAllFilesInWorkDir();

        // 已修改但未暂存
        message("=== Modifications Not Staged For Commit ===");
        List<String> modAndNotStagedList = new ArrayList<>();
        Commit currentCommit = getCurrentCommit();
        Map<String, String> commitBlobs = currentCommit.getBlobs();
        for (String path : commitBlobs.keySet()) {
            File file = join(path);
            if (!file.exists()) {
                // 1. 当前提交中已跟踪，并已从工作目录中删除，但未在删除暂存区，
                if (!removeStage.containsKey(path)) {
                    modAndNotStagedList.add(file.getName());
                }
            } else {
                String blobId = sha1(file.getName(), readContents(file));
                // 2. 当前提交中已跟踪，工作目录中已更改，但未添加暂存
                if (!blobId.equals(commitBlobs.get(path)) && !addStage.containsKey(path)) {
                    modAndNotStagedList.add(file.getName());
                }
            }
        }

        for (String path : addStage.keySet()) {
            File file = join(path);
            // 3. 已在添加暂存区，但在工作目录中已删除
            if (!file.exists()) {
                modAndNotStagedList.add(file.getName());
            } else {
                String blobId = sha1(file.getName(), readContents(file));
                // 4. 已在添加暂存区，但内容与工作目录中的不同
                if (!blobId.equals(addStage.get(path))) {
                    modAndNotStagedList.add(file.getName());
                }
            }
        }
        sortAndPrintList(modAndNotStagedList);
        // 未跟踪（allFilesInWorkDir已是有序集合，故无需排序）
        message("=== Untracked Files ===");
        for (String path : allFilesInWorkDir) {
            if ((!allTrackedFiles.contains(path) && !addStage.containsKey(path)) || removeStage.containsKey(path)) {
                message(join(path).getName());
            }
        }
    }
}
