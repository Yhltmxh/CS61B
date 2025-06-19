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
    public static final File REMOTES_DIR = join(REFS_DIR, "remotes");
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File CONFIG_FILE = join(GITLET_DIR, "config");

    static File REMOTE_CWD;
    static File REMOTE_GITLET_DIR;
    static File REMOTE_OBJECTS_DIR;
    static File REMOTE_COMMITS_DIR;
    static File REMOTE_BLOBS_DIR;
    static File REMOTE_REFS_DIR;
    static File REMOTE_HEADS_DIR;
    static File REMOTE_INDEX_FILE;
    static File REMOTE_HEAD_FILE;
    static File REMOTE_CONFIG_FILE;


    /**
     * 初始化远程目录索引
     * @param remoteDir 远程.gitlet目录
     */
    public static void initRemoteDirectory(File remoteDir) {
        REMOTE_CWD = join(remoteDir.getParent());
        REMOTE_GITLET_DIR = remoteDir;
        REMOTE_OBJECTS_DIR = join(REMOTE_GITLET_DIR, "objects");
        REMOTE_COMMITS_DIR = join(REMOTE_OBJECTS_DIR, "commits");
        REMOTE_BLOBS_DIR = join(REMOTE_OBJECTS_DIR, "blobs");
        REMOTE_REFS_DIR = join(REMOTE_GITLET_DIR, "refs");
        REMOTE_HEADS_DIR = join(REMOTE_REFS_DIR, "heads");
        REMOTE_INDEX_FILE = join(REMOTE_GITLET_DIR, "index");
        REMOTE_HEAD_FILE = join(REMOTE_GITLET_DIR, "HEAD");
        REMOTE_CONFIG_FILE = join(REMOTE_GITLET_DIR, "config");
    }


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
     * @param stage 暂存区对象
     */
    public static void saveStage(Stage stage) {
        writeObject(INDEX_FILE, stage);
    }


    /**
     * 保存配置
     * @param config 配置对象
     */
    public static void saveConfig(Config config) {
        writeObject(CONFIG_FILE, config);
    }


    /**
     * 更新HEAD
     * @param point 要指向的分支名（本项目没有分离头的状态）
     */
    public static void updateHead(String point) {
        Path base = Paths.get("refs");
        String[] s = point.split("/");
        if (s.length > 1) {
            base = base.resolve("remotes");
        } else {
            base = base.resolve("heads");
        }
        writeContents(HEAD_FILE, String.format("ref: %s", base.resolve(point)));
    }


    /**
     * 更新分支
     * @param commitId 提交id
     * @param branch 分支文件
     */
    public static void updateBranch(String commitId, File branch) {
        if (!branch.exists()) {
            createFile(branch);
        }
        writeContents(branch, commitId);
    }


    /**
     * 更新commit对象中blob的工作目录
     * @param targetCWD 目标地址
     * @param commit 提交对象
     */
    public static void updateBlobsPathInCommit(File targetCWD, Commit commit) {
        Map<String, String> newBlobs = new TreeMap<>();
        Map<String, String> blobs = commit.getBlobs();
        for (String path : blobs.keySet()) {
            String filename = join(path).getName();
            String blobId = blobs.get(path);
            newBlobs.put(join(targetCWD, filename).getPath(), blobId);
        }
        commit.setBlobs(newBlobs);
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
    public static Commit getBranchHeadByName(File headsDir, File commitsDir, String branchName) {
        File branchFile = join(headsDir, branchName);
        if (!branchFile.exists()) {
            return null;
        }
        String commitId = readContentsAsString(branchFile);
        File headCommit = join(commitsDir, commitId.substring(0, 2), commitId.substring(2));
        return readObject(headCommit, Commit.class);
    }


    public static Commit getBranchHeadByName(String branchName) {
        String[] s = branchName.split("/");
        if (s.length > 1) {
            return getBranchHeadByName(join(REMOTES_DIR, s[0]), COMMITS_DIR, s[1]);
        }
        return getBranchHeadByName(HEADS_DIR, COMMITS_DIR, branchName);
    }


    /**
     * 根据远程分支名获取分支头部的提交
     * @param branchName 分支名
     * @return 存在：提交对象，不存在：null
     */
    public static Commit getRemoteBranchHeadByName(String branchName) {
        return getBranchHeadByName(REMOTE_HEADS_DIR, REMOTE_COMMITS_DIR, branchName);
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
     * 获取当前提交
     * @param currentBranch 当前分支
     * @return 当前提交对象
     */
    public static Commit getCurrentCommit(File currentBranch) {
        String head = readContentsAsString(currentBranch);
        File currentCommit = join(COMMITS_DIR, head.substring(0, 2), head.substring(2));
        return readObject(currentCommit, Commit.class);
    }


    /**
     * 获取指定提交对象
     * @param commitId 提交id
     * @return 提交对象
     */
    public static Commit getCommitById(String commitId) {
        if (commitId == null || commitId.length() > UID_LENGTH) {
            return null;
        } else if (commitId.length() < UID_LENGTH && commitId.length() > 1) {
            // 若给定的id长度小于标准长度，则进行前缀匹配
            File dir = join(COMMITS_DIR, commitId.substring(0, 2));
            if (dir.exists()) {
                String a = commitId.substring(2);
                List<String> names = plainFilenamesIn(dir);
                if (names == null) {
                    return null;
                } else {
                    List<String> matchList = names.stream()
                            .filter(t -> t.startsWith(a)).collect(Collectors.toList());
                    if (matchList.size() != 1) {
                        return null;
                    } else {
                        commitId = commitId.substring(0, 2) + matchList.get(0);
                    }
                }

            }
        }
        File commit = join(COMMITS_DIR, commitId.substring(0, 2), commitId.substring(2));
        if (!commit.exists()) {
            return null;
        }
        return readObject(commit, Commit.class);
    }


    /**
     * 找到两分支的最近分叉点
     * @param current 当前分支头
     * @param target 目标分支头
     * @return 分叉点的提交
     */
    public static Commit getSplitPointCommit(Commit current, Commit target) {
        if (target.getId().equals(current.getId())) {
            return current;
        }
        Set<String> hash = new HashSet<>();
        Queue<Commit> queue = new ArrayDeque<>();
        // bfs 遍历当前分支，将所有节点放入哈希表
        queue.offer(current);
        while (!queue.isEmpty()) {
            Commit c = queue.poll();
            hash.add(c.getId());
            for (String parent : c.getParents()) {
                queue.offer(getCommitById(parent));
            }
        }
        // bfs 给定分支找到与当前分支的最近公共节点
        queue.offer(target);
        while (!queue.isEmpty()) {
            Commit c = queue.poll();
            if (hash.contains(c.getId())) {
                return c;
            }
            for (String parent : c.getParents()) {
                queue.offer(getCommitById(parent));
            }
        }
        return new Commit();
    }


    /**
     * 获取同一分支上的两个给定提交之间的提交
     * @param current 当前提交
     * @param target 目标提交
     * @return 提交集合
     */
    public static List<Commit> getCommitList(Commit current, Commit target) {
        List<Commit> res = new ArrayList<>();
        Queue<Commit> queue = new ArrayDeque<>();
        // bfs 遍历当前分支的历史
        queue.offer(current);
        while (!queue.isEmpty()) {
            Commit c = queue.poll();
            if (c.getId().equals(target.getId())) {
                return res;
            } else {
                res.add(c);
            }
            for (String parent : c.getParents()) {
                queue.offer(getCommitById(parent));
            }
        }
        return null;
    }


    /**
     * 获取给定提交所在分支的历史提交
     * @param current 当前提交
     * @return 提交集合
     */
    public static List<Commit> getCommitList(Commit current) {
        List<Commit> res = new ArrayList<>();
        Queue<Commit> queue = new ArrayDeque<>();
        queue.offer(current);
        while (!queue.isEmpty()) {
            Commit c = queue.poll();
            res.add(c);
            for (String parent : c.getParents()) {
                queue.offer(getCommitById(parent));
            }
        }
        return res;
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
     * 获取配置
     * @return 配置对象
     */
    public static Config getConfig() {
        return readObject(CONFIG_FILE, Config.class);
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
            message("Merge: %s %s", parents.get(0).substring(0, 7), parents.get(1).substring(0, 7));
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
     * 判断是否为远程拉取的分支
     * @param file 分支文件
     * @return true：是，false：不是
     */
    public static boolean isRemoteBranch(File file) {
        return file.getParentFile().getParentFile().equals(REMOTES_DIR);
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
     * 从提交中检出目标文件
     * @param targetPath 目标文件路径
     * @param blobId id
     */
    public static void checkoutTargetBlobFromCommit(String targetPath, String blobId) {
        File blobFile = join(BLOBS_DIR, blobId.substring(0, 2), blobId.substring(2));
        copyFile(blobFile, join(targetPath));
    }


    /**
     * 从提交中检出所有文件并清空暂存区
     * @param commit 提交对象
     */
    public static void checkoutCommit(Commit commit) {
        Map<String, String> blobs = commit.getBlobs();
        // 获取当前提交
        Commit currentCommit = getCurrentCommit();
        Map<String, String> curBlobs = currentCommit.getBlobs();
        List<String> allFilesInWorkDir = getAllFilesInWorkDir();
        // 出现未跟踪文件将被覆盖，报错信息
        String s = "There is an untracked file in the way; delete it, or add and commit it first.";
        for (String path : allFilesInWorkDir) {
            // 未跟踪的文件
            if (!curBlobs.containsKey(path) && blobs.containsKey(path)) {
                exitWithError(s);
            } else {
                restrictedDelete(path);
            }
        }
        // blob文件拷贝至工作目录
        for (String path : blobs.keySet()) {
            String blobId = blobs.get(path);
            File blobFile = join(BLOBS_DIR, blobId.substring(0, 2), blobId.substring(2));
            copyFile(blobFile, join(path));
        }
        // 清空暂存区
        saveStage(new Stage(new TreeMap<>(), new TreeMap<>()));
    }


    /**
     * 处理文件冲突
     * @param currentId 当前blob的id
     * @param targetId 指定blob的id
     * @param stage 暂存区
     * @param path 最终文件存储路径
     */
    public static void dealConflicts(String currentId, String targetId, String path, Stage stage) {
        String currentContents = "", targetContents = "";
        if (currentId != null) {
            File currentBlob = join(BLOBS_DIR, currentId.substring(0, 2), currentId.substring(2));
            currentContents = readContentsAsString(currentBlob);
        }
        if (targetId != null) {
            File targetBlob = join(BLOBS_DIR, targetId.substring(0, 2), targetId.substring(2));
            targetContents = readContentsAsString(targetBlob);
        }
        String sb = "<<<<<<< HEAD\n" + currentContents + "=======\n" + targetContents + ">>>>>>>\n";
        File res = join(path);
        writeContents(res, sb);
        String blobId = sha1(res.getName(), sb);
        // 转存blob
        saveBlob(res, blobId);
        stage.getAddStage().put(path, blobId);
        message("Encountered a merge conflict.");
    }


    /**
     * 处理提交过程
     * @param cur 当前提交
     * @param stage 暂存区对象
     * @param message 提交日志
     * @param parents 父提交集合
     */
    public static void dealCommit(Commit cur, Stage stage, String message, List<String> parents) {
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();
        Map<String, String> currentBlobs = cur.getBlobs();
        if (addStage.isEmpty() && removeStage.isEmpty()) {
            exitWithError("No changes added to the commit.");
        }
        // 将添加暂存区的索引加入blobs
        for (String path : addStage.keySet()) {
            currentBlobs.put(path, addStage.get(path));
        }
        // 将删除暂存区的索引从blobs中删除
        for (String path : removeStage.keySet()) {
            currentBlobs.remove(path);
        }
        // 保存提交
        Commit commit = new Commit(message, new Date(), parents, currentBlobs);
        saveCommit(commit);
        // 更新当前分支
        updateBranch(commit.getId(), getCurrentBranch());
        // 清空暂存区
        saveStage(new Stage(new TreeMap<>(), new TreeMap<>()));
    }


    /**
     * 处理文件合并
     * @param sp 分叉点提交
     * @param current 当前提交
     * @param target 目标提交
     * @param stage 暂存区对象
     */
    public static void dealFileMerge(Commit sp, Commit current, Commit target, Stage stage) {
        Map<String, String> addStage = stage.getAddStage();
        Map<String, String> removeStage = stage.getRemoveStage();
        Map<String, String> splitPointBlobs = sp.getBlobs();
        Map<String, String> currentBlobs = current.getBlobs();
        Map<String, String> targetBlobs = target.getBlobs();
        // 这里必须等所有错误都判断完后再执行文件的拷贝和删除
        List<String> toCheckout = new ArrayList<>();
        List<String> toDelete = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();
        // 出现未跟踪文件将被合并覆盖或删除，报错信息
        String s = "There is an untracked file in the way; delete it, or add and commit it first.";
        for (String path : splitPointBlobs.keySet()) {
            boolean currentHas = currentBlobs.containsKey(path);
            boolean targetHas = targetBlobs.containsKey(path);
            String spVal = splitPointBlobs.get(path);
            String curVal = currentBlobs.get(path);
            String tarVal = targetBlobs.get(path);
            if (currentHas && targetHas) {
                if (spVal.equals(curVal) && !spVal.equals(tarVal)) {
                    // 分叉点之后给定分支修改的文件而当前分支未修改，将文件检出并暂存。
                    toCheckout.add(path);
                } else if (!spVal.equals(curVal) && !spVal.equals(tarVal)
                        && !curVal.equals(tarVal)) {
                    // 两个文件的内容都发生了变化且与对方不同，出现冲突
                    conflicts.add(path);
                }
            } else if (currentHas) {
                // 在分支点存在的任何文件，在当前分支未经修改，且在给定分支中不存在，则应被删除（并变为未跟踪状态）。
                if (spVal.equals(curVal)) {
                    toDelete.add(path);
                } else {
                    // 一个文件的内容发生了变化而另一个文件被删除，出现冲突
                    conflicts.add(path);
                }
            } else if (targetHas && !spVal.equals(tarVal)) {
                if (join(path).exists()) {
                    exitWithError(s);
                }
                // 一个文件的内容发生了变化而另一个文件被删除，出现冲突
                conflicts.add(path);
            }
        }
        for (String path : targetBlobs.keySet()) {
            boolean currentHas = currentBlobs.containsKey(path);
            boolean splitPointHas = splitPointBlobs.containsKey(path);
            String curVal = currentBlobs.get(path);
            String tarVal = targetBlobs.get(path);
            if (!splitPointHas && !currentHas) {
                if (join(path).exists()) {
                    exitWithError(s);
                }
                // 在分叉点不存在而仅在给定分支中存在的任何文件都应被检出并暂存。
                toCheckout.add(path);
            } else if (!splitPointHas) {
                if (!tarVal.equals(curVal)) {
                    // 该文件在分叉点时不存在，而在给定分支和当前分支中具有不同的内容，出现冲突
                    conflicts.add(path);
                }
            }
        }
        // 所有校验结束，执行文件处理
        for (String path : toCheckout) {
            checkoutTargetBlobFromCommit(path, targetBlobs.get(path));
            addStage.put(path, targetBlobs.get(path));
        }
        for (String path : toDelete) {
            restrictedDelete(path);
            removeStage.put(path, currentBlobs.get(path));
        }
        for (String path : conflicts) {
            dealConflicts(currentBlobs.get(path), targetBlobs.get(path), path, stage);
        }
    }


    /**
     * 进行远程分支的检查
     * @param remoteName 远程仓库名
     */
    public static void remoteBranchCheck(String remoteName) {
        Config config = getConfig();
        Map<String, String> remoteConfig = config.getRemoteConfig();
        if (!remoteConfig.containsKey(remoteName)) {
            exitWithError("A remote with that name does not exist.");
        }
        String remoteDirectory = remoteConfig.get(remoteName);
        File dir = join(remoteDirectory);
        if (!dir.exists()) {
            exitWithError("Remote directory not found.");
        }
        initRemoteDirectory(dir);
    }


    /**
     * 文件传输
     * @param sourceCommitsDir 源地址
     * @param targetCommitsDir 目标地址
     * @param id 文件id
     */
    public static void transferFile(File sourceCommitsDir, File targetCommitsDir, String id) {
        File prefixDir = join(targetCommitsDir, id.substring(0, 2));
        File source = join(sourceCommitsDir, id.substring(0, 2), id.substring(2));
        File target = join(prefixDir, id.substring(2));
        if (!prefixDir.exists()) {
            createDirectory(prefixDir);
        }
        if (!target.exists()) {
            copyFile(source, target);
        }
    }


    /**
     * 传输提交
     * @param targetDir 目标地址
     * @param commit 提交对象
     */
    public static void transferCommit(File targetDir, Commit commit) {
        String id = commit.getId();
        File prefixDir = join(targetDir, id.substring(0, 2));
        File target = join(prefixDir, id.substring(2));
        if (!prefixDir.exists()) {
            createDirectory(prefixDir);
        }
        if (!target.exists()) {
            createFile(target);
            writeObject(target, commit);
        }
    }


    /**
     * 处理远程推送和拉取过程中的文件传输
     * @param commits 提交集合
     * @param cwd 目标工作目录
     * @param commitsDir 存储提交对象的目标地址
     * @param sourceBlobsDir 存储blob对象的源地址
     * @param blobsDir 存储blob对象的目标地址
     */
    public static void dealTransfer(List<Commit> commits, File cwd, File commitsDir,
                                    File sourceBlobsDir, File blobsDir) {
        for (Commit commit : commits) {
            updateBlobsPathInCommit(cwd, commit);
            String id = commit.getId();
            File commitFile = join(commitsDir, id.substring(0, 2), id.substring(2));
            if (commitFile.exists()) {
                continue;
            } else {
                transferCommit(commitsDir, commit);
            }
            Map<String, String> blobs = commit.getBlobs();
            for (String path : blobs.keySet()) {
                transferFile(sourceBlobsDir, blobsDir, blobs.get(path));
            }
        }
    }


    /**
     * 处理推送过程
     * @param commits 提交集合
     * @param currentId 当前分支头部提交的id
     * @param branchName 要推送的分支名
     */
    public static void dealPush(List<Commit> commits, String currentId, String branchName) {
        dealTransfer(commits, REMOTE_CWD, REMOTE_COMMITS_DIR, BLOBS_DIR, REMOTE_BLOBS_DIR);
        updateBranch(currentId, join(REMOTE_HEADS_DIR, branchName));
    }


    /**
     * 处理拉取过程
     * @param commits 提交集合
     * @param currentId 要拉取的远程分支头部提交的id
     * @param branchName 远程分支名
     * @param remoteName 远程仓库名
     */
    public static void dealFetch(List<Commit> commits, String currentId,
                                 String branchName, String remoteName) {
        dealTransfer(commits, CWD, COMMITS_DIR, REMOTE_BLOBS_DIR, BLOBS_DIR);
        updateBranch(currentId, join(REMOTES_DIR, remoteName, branchName));
    }

}
