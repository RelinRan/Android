package com.android.json;

import android.os.Environment;

import com.android.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Relin
 * on 2018-07-12.
 * 文件写入类
 */

public class JsonWriter {

    /**
     * 包名
     */
    private String pageName = getClass().getName().replace(JsonWriter.class.getSimpleName(), "");

    /**
     * 类文件路径
     */
    private String classDir = Environment.getExternalStorageDirectory() + "/Exam/";

    /**
     * 类名称
     */
    private String className = JsonWriter.class.getSimpleName();

    /**
     * 列表类名称
     */
    private String listClassName = JsonWriter.class.getSimpleName() + "Item";

    /**
     * 作者
     */
    private String authName = JsonWriter.class.getSimpleName();

    /**
     * 列表的下标
     */
    private int listIndex = 0;

    /**
     * 作者模板
     */
    private String AUTH_TEMPLATE = "/**\n" +
            " * Created by {AUTH_NAME}\n" +
            " * on {DATE}.\n" +
            " * Generated automatically by the Exam tools,\n" +
            " * the generation process need to class names,\n" +
            " * file paths, the package name, author data set,\n" +
            " * is the name of the class must be set, or you'll \n" +
            " * generate an error, the original have this file,\n" +
            " * will not generate a new file.\n" +
            " */\n\n";

    /**
     * 字段模板
     */
    private String FIELD_TEMPLATE = "    private {FIELD_TYPE} {FIELD_NAME_S};\n\n";

    /**
     * setter and getter模板
     */
    private String CONTENT_TEMPLATE =
            "    public {FIELD_TYPE} get{FIELD_NAME_M}() {\n" +
                    "        return {FIELD_NAME_S};\n" +
                    "    }\n" +
                    "\n" +
                    "    public void set{FIELD_NAME_M}({FIELD_TYPE} {FIELD_NAME_S}) {\n" +
                    "        this.{FIELD_NAME_S} = {FIELD_NAME_S};\n" +
                    "    }";

    /**
     * 将Json字符串转换为对象写入文件
     *
     * @param jsonStr json字符串
     */
    public void writeJavaObjectClass(String jsonStr) {
        writeJavaObjectClass(className, JsonParser.parseJSONObjectString(jsonStr));
    }

    /**
     * 写入Java对象文件类
     *
     * @param className  类名称[文件名]
     * @param jsonObject JSONObject对象
     */
    private void writeJavaObjectClass(String className, JSONObject jsonObject) {
        StringBuffer sb = new StringBuffer();
        //Json解析
        Iterator<String> iterator = jsonObject.keys();
        StringBuffer fields = new StringBuffer("");
        StringBuffer setterGetter = new StringBuffer("");
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = null;
            try {
                value = jsonObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String valueStr = String.valueOf(value);
            fields.append(createField(key, valueStr));
            setterGetter.append(createSetterAndGetter(key, valueStr));
        }
        //包名
        sb.append(createPageName());
        if (fields.toString().contains("List")) {
            sb.append("import java.util.List;\n\n");
        }
        //作者
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        sb.append(AUTH_TEMPLATE.replace("{AUTH_NAME}", authName).replace("{DATE}", format.format(new Date())));
        //类名
        sb.append("public class " + className + " {\n\n");
        //字段名 + set（）和 get（）方法
        sb.append(fields.toString() + "\n" + setterGetter.toString() + "\n");
        sb.append("}\n");
        IOUtils.writeFile(className + ".java", sb.toString());
    }

    /**
     * 创建字段名
     *
     * @param jsonKey   json键
     * @param jsonValue json值
     * @return
     */
    private String createField(String jsonKey, String jsonValue) {
        String field_type;
        if (isDigital(jsonValue)) {//数字
            if (isInteger(jsonValue)) {//是否是整数
                if (jsonValue.length() > Integer.MAX_VALUE) {
                    field_type = "long";
                } else {
                    field_type = "int";
                }
            } else {
                field_type = "double";
            }
        } else {
            if (jsonValue.contains("[") && jsonValue.contains("]")) {
                listIndex++;
                listClassName = listIndex == 1 ? listClassName : (listClassName + "_" + listIndex);
                field_type = "List<" + listClassName + ">";
                JSONArray jsonArray = JsonParser.parseJSONArrayString(jsonValue);
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) jsonArray.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                writeJavaObjectClass(listClassName, jsonObject);
            } else {
                field_type = "String";
            }
        }
        return FIELD_TEMPLATE.replace("{FIELD_TYPE}", field_type).replace("{FIELD_NAME_S}", jsonKey);
    }

    /**
     * 创建set和get方式
     *
     * @param fieldName  字段名称
     * @param fieldValue 字段值
     * @return
     */
    private String createSetterAndGetter(String fieldName, String fieldValue) {
        String field_type;
        String field_name_m = (new StringBuilder()).append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1)).toString();
        if (isDigital(fieldValue)) {//数字
            if (isInteger(fieldValue)) {//是否是整数
                if (fieldValue.length() > Integer.MAX_VALUE) {
                    field_type = "long";
                } else {
                    field_type = "int";
                }
            } else {
                field_type = "double";
            }
        } else {
            if (fieldValue.contains("[") && fieldValue.contains("]")) {
                field_type = "List<" + listClassName + ">";
            } else {
                field_type = "String";
            }
        }
        return CONTENT_TEMPLATE.replace("{FIELD_TYPE}", field_type).replace("{FIELD_NAME_S}", fieldName).replace("{FIELD_NAME_M}", field_name_m) + "\n\n";
    }

    /**
     * 创建包名
     *
     * @return
     */
    private String createPageName() {
        return "package " + pageName + ";\n\n";
    }

    /**
     * 是否是整数
     *
     * @param str 判断的字符串
     * @return
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 是否是数字
     *
     * @param str 判断的字符串
     * @return
     */
    public static boolean isDigital(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getClassDir() {
        return classDir;
    }

    public void setClassDir(String classDir) {
        this.classDir = classDir;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getListClassName() {
        return listClassName;
    }

    public void setListClassName(String listClassName) {
        this.listClassName = listClassName;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

}
