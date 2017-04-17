package sts;

import java.io.*;

/**
 * @author ts.Sun (2017/4/13).
 */
public class FileUtils {
    public static boolean createFile(File fileName) throws Exception {
        boolean flag = false;
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean writeTxtFile(String content, File fileName) throws Exception {
        RandomAccessFile mm = null;
        boolean flag = false;
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("GBK"));
            o.close();
//   mm=new RandomAccessFile(fileName,"rw");
//   mm.writeBytes(content);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mm != null) {
                mm.close();
            }
        }
        return flag;
    }

    public static void fileWriter(String context,File fileName) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(context);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
