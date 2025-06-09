package gitlet;

/**
 * @description: 暂存区索引，维护暂存区文件的元数据
 * @author: Yhltmxh
 * @create: 2025-06-08 19:04
 **/
public class Stage {

    /**
     * SHA-1 哈希值
     */
    private String blobId;

    /**
     * 文件路径
     */
    private String filePath;
}
