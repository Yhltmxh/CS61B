package byow.Core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 文件操作工具类
 * @author: 杨怀龙
 * @create: 2025-07-14 19:22
 **/
public class FileUtils {

    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
    }


    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }


    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }


    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }


    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }


    private static final FilenameFilter PLAIN_FILES =
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isFile();
                }
            };

    // 目录类型过滤器
    private static final FilenameFilter PLAIN_DIRS =
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            };


    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }


    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }


    /**
     * 获取子目录名集合
     * @param dir 目录
     * @return 目录名集合
     */
    static List<String> plainDirectoryIn(File dir) {
        String[] files = dir.list(PLAIN_DIRS);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }


    static List<String> plainDirectoryIn(String dir) {
        return plainDirectoryIn(new File(dir));
    }


    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }


    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }



    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException exception) {
            throw new RuntimeException("Internal error serializing", exception);
        }
    }


    /**
     * 创建目录，若创建失败打印错误信息并终止程序
     * @param file 目录
     */
    static void createDirectory(File file) {
        if (!file.exists() && !file.mkdir()) {
            throw new RuntimeException(String.format("Failed to create '%s' directory.", file.getName()));
        }
    }

    /**
     * 创建文件，若创建失败打印错误信息并终止程序
     * @param file 文件
     */
    static void createFile(File file) {
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new RuntimeException(String.format("Failed to create '%s' file.", file.getName()));
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O error during create file", e);
        }
    }


    /**
     * 复制文件
     * @param source 源文件
     * @param target 目标文件
     */
    static void copyFile(File source, File target) {
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     * @param file 文件对象
     */
    static void deleteFile(File file) {
        if (!file.delete()) {
            throw new RuntimeException(String.format("Failed to delete '%s' file.", file.getName()));
        }
    }

}
