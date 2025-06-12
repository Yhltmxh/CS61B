package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 提交类，维护元数据及文件索引
 * @author: Yhltmxh
 * @create: 2025-06-08 19:04
 **/
public class Commit implements Serializable {

    /**
     * SHA-1 哈希值
     */
    private String id;

    /**
     * 提交日志信息
     */
    private String message;

    /**
     * 提交创建时间
     */
    private Date createTime;

    /**
     * 父提交id
     */
    private List<String> parents;

    /**
     * 所提交文件的索引
     */
    Map<String, String> blobs;


    public Commit(String message, Date createTime, String parent, Map<String, String> blobs) {
        this.message = message;
        this.createTime = createTime;
        this.parents = new ArrayList<>();
        this.parents.add(parent);
        this.blobs = blobs;
        this.id = Utils.sha1(message, createTime.toString(), this.parents.toString(), blobs.toString());
    }

    public Commit(String message, Date createTime, List<String> parents, Map<String, String> blobs) {
        this.message = message;
        this.createTime = createTime;
        this.parents = parents;
        this.blobs = blobs;
        this.id = Utils.sha1(message, createTime.toString(), parents.toString(), blobs.toString());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public void setBlobs(Map<String, String> blobs) {
        this.blobs = blobs;
    }

}
