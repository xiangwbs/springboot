package com.xwbing.demo;

import java.io.File;

/**
 * 删除一个含有子项的目录
 *
 * @author xiangwb
 */
public class RecursiveFIle {
    public static void main(String[] args) {
        File dir = new File("C:\\Users\\admin\\Desktop\\123");
        deleteFile(dir);
    }

    /*
     * 将给定的file表示的文件或目录删除
     */
    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            //将该目录下的所有子项删除
            File[] subs = f.listFiles();
            for (File sub : subs) {
                deleteFile(sub);
            }
        }
        f.delete();
    }
}
