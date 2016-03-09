package shopbyar.com.arshop_customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zijiantang on 25/2/16.
 */
public class FileUtils {
    public static File[] getPhotoFolders(File baseDir) {
        createDefaultFolderIfNeeded(baseDir);
        List<File> results = new ArrayList<>();
        if (baseDir.isDirectory()) {
            File[] files = baseDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        results.add(file);
                    }
                }
            }
        }
        return results.toArray(new File[results.size()]);
    }

    public static List<String> getPhotoFolderNames(File baseDir) {
        createDefaultFolderIfNeeded(baseDir);
        File[] files = getPhotoFolders(baseDir);
        List<String> res = new ArrayList<>();
        for (File f : files) {
            res.add(f.getName());
        }
        return res;
    }

    private static void createDefaultFolderIfNeeded(File baseDir) {
        File defaultFolder = new File(baseDir, "default");
        if (!defaultFolder.exists()) {
            try {
                defaultFolder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] getPhotoURLsUnderFolder(File baseDir, String folderName) {
        try {
            File folder = new File(baseDir, folderName);
            if (folder != null && folder.isDirectory()) {
                File[] files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.toLowerCase().endsWith(".jpg");
                    }
                });
                List<String> res = new ArrayList<>();
                for (File f : files) {
                    res.add("file:///" + f.getAbsolutePath());
                }
                return res.toArray(new String[res.size()]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static boolean createFolder(File dir) {
        return dir.mkdir();
    }

    public static String readTextFile(File file) {
        if (file.exists()) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
