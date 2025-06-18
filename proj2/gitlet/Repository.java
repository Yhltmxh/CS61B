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


    /**
     * 校验命令和对应参数数量是否符合要求，以及是否完成初始化
     * @param args 参数数组
     * @return 命令枚举
     */
    public static Command argsCheck(String[] args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        Command command = Command.findCommand(args[0]);
        if (command == null) {
            exitWithError("No command with that name exists.");
        } else {
            String[] paramCount = command.getParamCount().split(",");
            boolean isMatch = Arrays.stream(paramCount)
                    .anyMatch(c -> Integer.parseInt(c) == args.length);
            if (!isMatch) {
                exitWithError("Incorrect operands.");
            } else if (!args[0].equals(Command.INIT.getCommand()) && !GITLET_DIR.exists()) {
                exitWithError("Not in an initialized Gitlet directory.");
            }
        }
        return command;
    }


    public static void doInit() {
        String msg = "A Gitlet version-control system already exists in the current directory.";
        if (GITLET_DIR.exists()) {
            exitWithError(msg);
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
        // 创建初始提交
        Commit init = new Commit("initial commit", new Date(0), new ArrayList<>(), new HashMap<>());
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
        // 删除暂存区包含该文件则移除
        removeStage.remove(path);
        // 保存暂存区
        saveStage(stage);

    }


    public static void doCommit(String message) {
        if (message.isBlank()) {
            exitWithError("Please enter a commit message.");
        }

        Commit cur = getCurrentCommit();
        // 拿到暂存区对象
        Stage stage = getStage();

        List<String> parents = new ArrayList<>();
        parents.add(cur.getId());
        dealCommit(cur, stage, message, parents);
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
                restrictedDelete(rmFile);
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
        printCommitLog(cur);
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
                    modAndNotStagedList.add(file.getName() + " (deleted)");
                }
            } else {
                String blobId = sha1(file.getName(), readContents(file));
                // 2. 当前提交中已跟踪，工作目录中已更改，但未添加暂存
                if (!blobId.equals(commitBlobs.get(path)) && !addStage.containsKey(path)) {
                    modAndNotStagedList.add(file.getName() + " (modified)");
                }
            }
        }

        for (String path : addStage.keySet()) {
            File file = join(path);
            // 3. 已在添加暂存区，但在工作目录中已删除
            if (!file.exists()) {
                modAndNotStagedList.add(file.getName() + " (deleted)");
            } else {
                String blobId = sha1(file.getName(), readContents(file));
                // 4. 已在添加暂存区，但内容与工作目录中的不同
                if (!blobId.equals(addStage.get(path))) {
                    modAndNotStagedList.add(file.getName() + " (modified)");
                }
            }
        }
        sortAndPrintList(modAndNotStagedList);
        // 未跟踪（allFilesInWorkDir已是有序集合，故无需排序）
        message("=== Untracked Files ===");
        for (String path : allFilesInWorkDir) {
            if ((!commitBlobs.containsKey(path) && !addStage.containsKey(path))
                    || removeStage.containsKey(path)) {
                message(join(path).getName());
            }
        }
    }


    public static void doCheckout(String[] args) {
        if (args.length == 2) {
            // 要检出的分支为当前分支
            if (getCurrentBranch().getName().equals(args[1])) {
                exitWithError("No need to checkout the current branch.");
            }
            Commit branchHead = getBranchHeadByName(args[1]);
            if (branchHead == null) {
                // 分支不存在
                exitWithError("No such branch exists.");
            } else {
                checkoutCommit(branchHead);
                // 头指针更新
                updateHead(args[1]);
            }
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                exitWithError("Incorrect operands.");
            }
            // 获取当前提交的所有文件
            Commit currentCommit = getCurrentCommit();
            checkoutTargetBlobFromCommit(join(CWD, args[2]), currentCommit);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                exitWithError("Incorrect operands.");
            }
            Commit commit = getCommitById(args[1]);
            if (commit == null) {
                exitWithError("No commit with that id exists.");
            } else {
                checkoutTargetBlobFromCommit(join(CWD, args[3]), commit);
            }
        }
    }

    public static void doBranch(String branchName) {
        // 校验该分支是否已存在
        File branchFile = join(HEADS_DIR, branchName);
        if (branchFile.exists()) {
            exitWithError("A branch with that name already exists.");
        }
        // 创建master分支
        saveBranch(branchName, getCurrentCommit().getId());
    }


    public static void doRemoveBranch(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        // 校验该分支是否不存在或为当前分支
        if (!branchFile.exists()) {
            exitWithError("A branch with that name does not exist.");
        } else if (branchName.equals(getCurrentBranch().getName())) {
            exitWithError("Cannot remove the current branch.");
        }
        deleteFile(branchFile);
    }


    public static void doReset(String commitId) {
        Commit commit = getCommitById(commitId);
        if (commit == null) {
            exitWithError("No commit with that id exists.");
        } else {
            checkoutCommit(commit);
            updateBranch(commitId, getCurrentBranch());
        }
    }


    public static void doMerge(String branchName) {
        // 获取暂存区
        Stage stage = getStage();
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();
        if (!addStage.isEmpty() || !removeStage.isEmpty()) {
            exitWithError("You have uncommitted changes.");
        }
        Commit target = getBranchHeadByName(branchName);
        // 给定分支不存在
        if (target == null) {
            exitWithError("A branch with that name does not exist.");
        } else {
            File currentBranch = getCurrentBranch();
            // 合并自身报错
            if (currentBranch.getName().equals(branchName)) {
                exitWithError("Cannot merge a branch with itself.");
            }
            Commit current = getCurrentCommit(currentBranch);
            // 获取给定分支与当前分支分叉处的提交
            Commit splitPointCommit = getSplitPointCommit(current, target);
            if (splitPointCommit.getId().equals(target.getId())) {
                exitWithError("Given branch is an ancestor of the current branch.");
            } else if (splitPointCommit.getId().equals(current.getId())) {
                // 如果分叉点是当前分支，那么先检出给定分支，再打印消息
                checkoutCommit(target);
                updateHead(branchName);
                exitWithError("Current branch fast-forwarded.");
            }
            // 执行文件合并
            dealFileMerge(splitPointCommit, current, target, stage);
            // 进行提交
            List<String> parents = new ArrayList<>();
            parents.add(current.getId());
            parents.add(target.getId());
            String currentBranchName = currentBranch.getName();
            String message = String.format("Merged %s into %s.", branchName, currentBranchName);
            dealCommit(current, stage, message, parents);
        }
    }
}
