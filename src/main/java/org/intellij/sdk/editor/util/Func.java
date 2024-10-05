package org.intellij.sdk.editor.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Func {

    /**
     * 读取类路径下指定文件的内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    static public String readResourceFileContents(String fileName) {
        try (InputStream inputStream = Func.class.getClassLoader().getResourceAsStream("config/" + fileName)) {
            if (inputStream == null) {
                System.out.println("Resource not found: " + fileName);
                return "";
            }

            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return "";
        }
    }

    /**
     * 将字符串内容写入类路径下指定文件
     *
     * @param fileName 文件名
     * @param content  要写入的文件内容
     */
    static public void writeResourceFileContents(String fileName, String content) {
        try {
            // 获取类路径下的 config 目录
            String configDir = Func.class.getClassLoader().getResource("config").getPath();
            String filePath = configDir + "/" + fileName;
            System.out.println(filePath);
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                outputStream.write(bytes);
                System.out.println("File written successfully: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}