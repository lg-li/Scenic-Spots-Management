package cn.edu.neu.scenicspots;

import java.util.regex.Pattern;

public class Constant {
    public static String DEFAULT_RUNTIME_DIR = "static";
    public static String SCENIC_SPOT = "ScenicSpot";
    public static String SCENIC_PATH = "ScenicPath";
    public static String DEFAULT_USERNAME = "admin";
    public static String DEFAULT_PASSWORD = "123456";

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
