package cn.edu.neu.scenicspots.storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * JSON Storage Controller 提供 JSON 对象存储 以及 CRUD 基本操作接口
 *
 * @author 李林根 / 20165254 / NEU
 */
public class Storage {

    /**
     * 默认磁盘存储路径文件
     */
    private static final String DEFAULT_STORAGE_PATH = "storage.json";
    private static Storage instance;
    private JSONObject storageJSON;

    private Storage() {
        loadFromFile(); // 单例模式
    }

    public String toString(){
        return storageJSON.toString();
    }

    /**
     * @return 单例模式对象
     */
    public static Storage getStarted() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public static String readFile(String path) {
        String laststr = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr = laststr + tempString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return laststr;
    }

    public static boolean writeFile(String filePath, String fileContent) throws IOException {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(fileContent);
            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println("[Exception] 写入文件内容操作出错");
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject root() {
        return storageJSON;
    }

    /**
     * 打印当前库内容到控制台
     */
    public void varDump() {
        System.out.println(storageJSON.toString());
    }

    /**
     * 从默认文件加载库
     *
     * @return 是否加载成功
     */
    private boolean loadFromFile() {
        try {
            storageJSON = new JSONObject(readFile(DEFAULT_STORAGE_PATH));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 从文件加载库
     *
     * @param path 文件路径
     * @return 是否加载成功
     */
    public boolean loadFromFile(String path) {
        try {
            storageJSON = new JSONObject(readFile(path));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean loadFromJSON(String json) {
        try {
            storageJSON = new JSONObject(json);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 增加
     *
     * @param className 存储类型名称
     * @param obj       存储对象
     * @return 新增对象的ID，若失败，ID为-1
     */
    public int create(String className, JSONObject obj) {
        try {
            System.out.println("CREATE: \n  " + obj.toString());
            // 自动生成新ID
            int newID = storageJSON.getJSONArray(className).length() + 1;
            obj.put("id", newID);
            storageJSON.append(className, obj);
            return newID;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * 修改
     *
     * @param className 存储类型名称
     * @param id        存储对象ID
     * @param obj       新用于替换的对象
     * @return 指示是否更改成功的布尔值
     */
    public boolean update(String className, String id, JSONObject obj) {
        if(id.equals("")){
            create(className, obj);
            return true;
        }else {
            int location = searchInArray(className, id);
            if (location != -1) {
                JSONArray tempArr = storageJSON.getJSONArray(className);
                tempArr.remove(location);
                storageJSON.append(className, obj);
                return true;
            }
            return false;
        }
    }

    /**
     * 删除
     *
     * @param className 存储类型名称
     * @param id        存储对象ID
     * @return 指示是否删除成功的布尔值
     */
    public boolean delete(String className, String id) {
        int location = searchInArray(className, id);
        if (location != -1) {
            storageJSON.getJSONArray(className).remove(location);
            return true;
        }
        return false;
    }


    public boolean delete(String className, String key, String value) {
        int location = searchInArray(className, key, value);
        if (location != -1) {
            storageJSON.getJSONArray(className).remove(location);
            return true;
        }
        return false;
    }




    /**
     * 查找
     *
     * @param className     存储类型名称
     * @param id            存储对象ID
     * @param classFullName 对象化指定类完整路径
     * @return 一个携带搜索结果的指定对象，无结果则返回null
     */
    public JSONObject retrieve(String className, String id, String classFullName) {
        JSONArray temp = storageJSON.getJSONArray(className);
        int i = 0;
        for (i = 0; i < temp.length(); i++) {
            JSONObject tempObj = temp.getJSONObject(i);
            if (tempObj.optInt("id") == Integer.valueOf(id)) {
                return tempObj;
            }
        }
        return null;
    }

    /**
     * 对象存储列出对象操作
     *
     * @param className
     *            存储类型名称
     * @return 一个携带搜索结果的 JSONArray 结果集，无结果则返回null
     */

    /**
     * 通过ID 搜索指定类型的对象存储 并返回 JSONObject
     *
     * @param className
     * @param id
     * @return
     */
    public JSONObject retrieve(String className, String id) {
        JSONArray temp = storageJSON.getJSONArray(className);
        int i = 0;

        for (i = 0; i < temp.length(); i++) {
            JSONObject tempObj = temp.getJSONObject(i);
            if (tempObj.optString("id").equals(id)) {
                return tempObj;
            }
        }
        return null;
    }

    /**
     * 对象存储列出对象操作
     *
     * @param className 存储类型名称
     * @return 一个携带搜索结果的 JSONArray 结果集，无结果则返回null
     */
    public JSONArray retrieve(String className) {
        JSONArray temp = storageJSON.getJSONArray(className);
        return temp;
    }

    /**
     * 对象库中搜索指定id的对象
     *
     * @param className
     * @param id
     * @return 在数组中的索引
     */
    public int searchInArray(String className, String id) {

        JSONArray temp = storageJSON.getJSONArray(className);
        int i = 0;
        for (i = 0; i < temp.length(); i++) {
            JSONObject tempObj = temp.getJSONObject(i);
            if (tempObj.optInt("id") == Integer.valueOf(id)) {
                return i;
            }
        }
        return -1;
    }

    public int searchInArray(String className, String key, String value) {

        JSONArray temp = storageJSON.getJSONArray(className);
        int i = 0;
        for (i = 0; i < temp.length(); i++) {
            JSONObject tempObj = temp.getJSONObject(i);
            if (tempObj.optString(key).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 保存对象存储库到默认文件
     *
     * @return 成功与否
     */
    public boolean save() {
        String content = this.storageJSON.toString();
        try {
            return writeFile(DEFAULT_STORAGE_PATH, content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存对象存储库到指定路径文件
     *
     * @param path 路径
     * @return 保存成功与否
     */
    public boolean save(String path) {
        // Convert JSON to string
        String content = this.storageJSON.toString();
        try {
            return writeFile(path, content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveAndRefresh() {
        save();
        loadFromFile();
    }
}
