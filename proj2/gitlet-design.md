# Gitlet Design Document

## 一、文件结构

.gitlet目录参照.git目录的结构进行精简

```
.git/
├── HEAD
├── config
├── description
├── hooks/
├── info/
├── objects/
├── refs/
│   ├── heads/
│   ├── remotes/
│   └── tags/
├── index
└── logs/
    ├── HEAD
    └── refs/
    
    
    
.gitlet/
├── HEAD
├── objects/
├── refs/
│   └── heads/
└── index

```

### 1. objects目录

用于存储commits对象和blob对象，项目使用SHA-1哈希值作为id，为了避免文件过多堆积在一个目录下，效仿git使用前两个字符作为目录名，剩下的字符作为文件名。

### 2. refs目录

用于存储分支引用信息，目前只有heads（本地分支）一个子目录，但为了扩展性保留了内层heads目录，后期可参考git添加远程分支引用的目录。存储方式为：以分支名（如master，main）作为文件名，文件内容存储每个分支末端的commits的id。

### 3. index文件

用于存储暂存区中文件的索引，在通过add命令添加文件时，会先在objects目录下创建该文件的blob对象（将原文件内容写入），然后将blob的id和文件路径存入index中，同时要存入一个标记用以区分add和rm（暂定）。

### 4. HEAD文件

用于存储head指针的位置。当head指向某分支时存储分支文件路径，例如：`ref: refs/heads/main`。当head处于分离状态时（detached HEAD）就存储head当前指向的commits对象的文件路径。



## 二、Class设计

### 1. Commit	提交类

- String id：SHA-1哈希值
- String message：提交日志信息
- Date createTime：提交创建时间
- Commits parent：父提交
- Map<String, String> blobs: 提交文件的索引（key：文件路径，value：文件id）

### 2. Stage	暂存区索引

- String blobId：文件id
- String filePath：文件路径

### 3. Repository 仓储类

进行具体命令处理

### 4. Utils 工具类

封装各种文件处理方法和异常处理方法



## 三、命令难点实现

### 1. init

创建目录，创建初始提交，创建master分支，更新HEAD

### 2. merge

#### 两分支在同一链表的情况（这里的分支均指分支头部的提交）：

1. 分叉点与给定分支相同，则代表两分支属于同一链表且给定分支要么比当前分支短要么跟当前分支一致，故**直接结束**打印信息`Given branch is an ancestor of the current branch.`
2. 分叉点是当前分支，且又与给定分支不同，则两分支属于同一链表且给定分支必然比当前分支长，故直接**检出给定分支**打印信息`Current branch fast-forwarded.`

#### 两分支在不同链表时的处理：

1. ==分叉点之后给定分支修改的文件而当前分支未修改，将文件检出并暂存。==√

2. 分叉点之后当前分支修改的文件而给定分支未修改，保持原样。√

3. 任何在当前分支和给定分支中以相同方式被修改的文件（即，两个文件现在具有相同的内容或都被删除）在合并时保持不变。如果一个文件从当前分支和给定分支都被删除，但工作目录中存在同名文件，该文件将保持原样。

4. 在分叉点不存在而只在当前分支中存在的任何文件都应保持原样。√

5. ==在分叉点不存在而仅在给定分支中存在的任何文件都应被检出并暂存。==√

6. 在分支点存在的任何文件，在当前分支未经修改，且在给定分支中不存在，则应被删除（并变为未跟踪状态）。√

7. 在分支点存在的文件，在给定的分支中未修改，并且在当前分支中不存在，则应保持不存在。√

8. 当前分支和给定分支中以不同方式修改的文件存在冲突。“以不同方式修改”可以指两个文件的内容都发生了变化且与其他不同，或者一个文件的内容发生了变化而另一个文件被删除，或者该文件在分叉点时不存在，而在给定分支和当前分支中具有不同的内容。在这种情况下，用下面这种格式（将“...的内容”替换为指定文件的内容）并将结果暂存。

   ```
   <<<<<<< HEAD
   contents of file in current branch
   =======
   contents of file in given branch
   >>>>>>>
   ```






### 四、测试

在testing目录下使用git bash执行指令

```
执行单个命令
py tester.py samples/test01-init.in

执行单个命令，并获得详细输出信息
py tester.py --verbose samples/test01-init.in

执行全部命令
py tester.py samples/*.in
```

test33、test36、test43存在问题



```

py tester.py --verbose samples/test30-rm-branch.in


test01-init:√

test02-basic-checkout:√

test03-basic-log:√

test04-prev-checkout:√

test11-basic-status:√

test12-add-status:√

test13-remove-status:√

test14-add-remove-status:√

test15-remove-add-status:√

test16-empty-commit-err:√

test17-empty-commit-message-err:√

test18-nop-add:√

test19-add-missing-err:√

test20-status-after-commit:√

test21-nop-remove-err:√

test22-remove-deleted-file:√

test23-global-log:√

test24-global-log-prev:√

test25-successful-find:√

test26-successful-find-orphan:√

test27-unsuccessful-find-err:√

test28-checkout-detail:√

test29-bad-checkouts-err:√

test30-branches:√

test30-rm-branch:√

test31-duplicate-branch-err:√

test31-rm-branch-err:√

test32-file-overwrite-err:√

*test33-merge-no-conflicts:

test34-merge-conflicts:

test35-merge-rm-conflicts:

*test36-merge-err:

test36-merge-parent2:

test37-reset1:

test38-bad-resets-err:√

test39-short-uid:

test40-special-merge-cases:

test41-no-command-err:√

test42-other-err:√

*test43-criss-cross-merge:

test43-criss-cross-merge-b:

test44-bai-merge:



```

