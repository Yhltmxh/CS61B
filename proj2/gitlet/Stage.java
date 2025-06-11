package gitlet;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 暂存区索引，维护暂存区文件的元数据
 * @author: Yhltmxh
 * @create: 2025-06-08 19:04
 **/
public class Stage implements Serializable {

    /**
     * 添加暂存区
     */
    private Map<String, String> addStage;

    /**
     * 删除暂存区
     */
    private Map<String, String> removeStage;

    public Stage(Map<String, String> addStage, Map<String, String> removeStage) {
        this.addStage = addStage;
        this.removeStage = removeStage;
    }

    public Map<String, String> getAddStage() {
        return addStage;
    }

    public void setAddStage(Map<String, String> addStage) {
        this.addStage = addStage;
    }

    public Map<String, String> getRemoveStage() {
        return removeStage;
    }

    public void setRemoveStage(Map<String, String> removeStage) {
        this.removeStage = removeStage;
    }
}
