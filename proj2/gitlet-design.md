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



## 三、命令实现

### 1. init

创建目录，创建初始提交，创建master分支，更新HEAD

### 2. add

