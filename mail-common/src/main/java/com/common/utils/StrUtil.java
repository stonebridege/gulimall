package com.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class StrUtil {

    /**
     * 判断字符串是否为纯数字
     *
     * @param str 参数
     * @return 判断结果
     */
    public static boolean isNumber(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串第一个字符是否为纯字母
     *
     * @param str 参数
     * @return 判断结果
     */
    public static boolean isLetter(String str) {
        boolean flag = true;
        char c = str.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 检查字符串是不是位全部数字
     *
     * @param str 输入字符串
     * @return 返回结果
     */
    public static boolean isAllNumber(String str) {
        boolean flag = true;
        char[] s = str.toCharArray();
        for (int i = 0; i < s.length; i++) {
            char c = str.charAt(i);
            int j = (int) c;
            if ((j >= 65 && j <= 90) || (j >= 97 && j <= 122)) {
                flag = true;
            } else {
                if (isNumber(String.valueOf(c))) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 字符转Double类型
     *
     * @param str :字符串
     * @return ：Double类型数据
     */
    public static Double strToDouble(String str) {
        try {
            if (StrUtil.isEmpty(str)) {
                return 0d;
            }
            return Double.valueOf(str);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 将日期转换成指定格式
     *
     * @param rq     ：日期
     * @param format ：日期格式
     * @return :格式化后的数据
     */
    public static String convertDateFormat(Date rq, String format) {
        if (rq == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(rq);
    }

    /**
     * 将String|Short,Integer,BigDecimal,Double,date类型值为0和null的转为空字符,用于页面显示.
     *
     * @param val ：要转化的值
     * @return
     */
    public static String convertZero(Object val) {
        if (null == val) {
            return "";
        }

        if (val instanceof String) {
            String str = (String) val;
            if (val == null || "0".equals(str) || "null".equals(str)) {
                return "";
            } else {
                return str;
            }
        } else if (val instanceof Integer) {
            Integer i = (Integer) val;
            if (i == null || i.intValue() == 0) {
                return "";
            } else {
                return "" + i;
            }
        } else if (val instanceof Short) {
            Short s = (Short) val;
            if (s == null || s.shortValue() == 0) {
                return "";
            } else {
                return "" + s;
            }
        } else if (val instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) val;

            Double dou = bd.doubleValue();
            dou = round(dou, 2);
            if (Math.abs(dou) < 0.0001) {
                return "";
            } else {
                DecimalFormat df = new DecimalFormat("#0.00");
                return df.format(bd);
            }
        } else if (val instanceof Double) {
            Double dou = (Double) val;
            dou = round(dou, 2);
            if (dou == null || Math.abs(dou.doubleValue()) < 0.0001) {
                return "";
            } else {
                DecimalFormat df = new DecimalFormat("#0.00");
                return df.format(dou);
            }
        } else if (val instanceof Date || val instanceof Timestamp) {
            DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(val);
        } else {
            return "";
        }
    }

    /**
     * double类型保留几位小数
     *
     * @param v
     * @param scale
     * @return
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 输入字符串
     * @return 返回结算结果
     */
    public static boolean isEmpty(String str) {
        boolean flag = false;
        if (str == null) {
            flag = true;
        } else if (trim(str).length() == 0) {
            flag = true;
        } else if (str.equals("null")) {
            flag = true;
        }
        return flag;
    }


    /**
     * 获取X位随机数
     *
     * @param x 位数
     * @return
     */
    public static String getRandom(int x) {
        int num = (int) Math.pow(10, x);
        int number = (int) Math.round((Math.random()) * num);
        return String.valueOf(number);
    }

    /**
     * 将Object转化为Double类型
     *
     * @param str ：字符串
     * @return
     */
    public static Double getTrueDouble(Object str) {
        if (str == null || "null".equals(str)) {
            str = "0.0";
        }
        return Double.parseDouble(str.toString());
    }


    /**
     * Utf8URL编码
     *
     * @param text
     * @return
     */
    public static String Utf8URLencode(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0 && c <= 255) {
                if (c == 32)
                    result.append("+");
                result.append(c);
            } else {
                byte[] b = new byte[0];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                } catch (Exception ex) {
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    result.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return result.toString();
    }


    /**
     * 截取字符串特定长度
     *
     * @param str      源字符串
     * @param position 截取的位置  0 从前面截取 1 从后面截取
     * @param i        截取的长度
     * @return
     */
    public static String subString(String str, String position, int i) {
        if (str.length() <= i) {
            log.error("字符串长度小于需要截取的长度！");
        } else {
            if (!StrUtil.isEmpty(position) && "1".equals(position)) {
                str = str.substring(str.length() - i, str.length());
            } else {
                str = str.substring(0, i);
            }
        }
        return str;
    }


    /**
     * object 转String
     *
     * @param obj 字符
     * @return
     */
    public static String trim(Object obj) {
        return obj == null ? "" : obj.toString().trim();
    }

    /**
     * map排序
     *
     * @param param
     * @return 返回排序后的map
     */
    public static Map<String, Object> sortMapNew(Map<String, Object> param) {
        TreeMap<String, Object> paramTreeMap = new TreeMap<>(param);
        return paramTreeMap;
    }

    /**
     * 字符串去除特殊格式 以"开头,和"以这个结尾的 符号去除
     *
     * @param str 特殊字符串
     * @return str 字符串
     */
    public static String strSpetrim(String str) {
        if (!isEmpty(str)) {
            if (str.startsWith("\"")) {
                str = str.replaceFirst("\"", "");
            }
            if (str.endsWith("\"")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }


    /**
     * json 格式去除特殊符号
     *
     * @param json json格式字符串
     * @return返回 去除头尾中括号的 字符串
     */
    public static String jsonSpeTrim(String json) {
        if (!isEmpty(json)) {
            if (json.startsWith("\\[")) {
                json = json.replaceFirst("\\[", "");
            }
            if (json.endsWith("\\]")) {
                json = json.substring(0, json.length() - 1);
            }
        }
        return json;
    }


    /**
     * 计算输入日期和当前日期的差值
     *
     * @param str       ：日期字符串
     * @param formatStr ：字符串格式
     * @return
     */
    public static int differentDays(String str, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        int ss = 0;
        try {
            Date date1 = format.parse(str);
            Date date2 = new Date();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            //获得这一天在是这一年的第多少天
            int day1 = cal1.get(Calendar.DAY_OF_YEAR);
            int day2 = cal2.get(Calendar.DAY_OF_YEAR);
            // 获取年
            int year1 = cal1.get(Calendar.YEAR);
            int year2 = cal2.get(Calendar.YEAR);
            if (year1 != year2) // 不同一年
            {
                int timeDistance = 0;
                for (int i = year1; i < year2; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                        timeDistance += 366;  //闰年
                    } else {
                        timeDistance += 365; //不是闰年
                    }
                }
                ss = timeDistance + (day2 - day1);
            } else {
                ss = day2 - day1;
                ;
            }
        } catch (java.text.ParseException e) {
            log.error(e.getMessage(), e);
        }
        return ss;
    }


    /**
     * 数字转换成字符串.
     *
     * @param ob  int或者long型数值
     * @param cnt 转换成字符串的位数,不足位前面补0.
     * @return 字符串
     * @author 殷柏成
     * @date 2012-10-12 下午05:21:14
     */
    public static String numToStrP(Object ob, int cnt) {
        StringBuilder formatStr = new StringBuilder();
        for (int i = 0; i < cnt; i++) {
            formatStr.append("0");
        }
        String tt = formatStr.toString() + String.valueOf(ob);
        int len = tt.length();
        return tt.substring(len - cnt, len);
    }

    /**
     * 特殊的数据转化  对于 0.0的情况 转字符串 直接转为 0.0 字符串
     *
     * @param val double 数据转化
     * @return
     */
    public static String convertSpeDoubleToStr(Double val) {
        if (val == null || val < 0.0) {
            return "";
        } else if (val == 0.00) {
            return "0";
        } else {
            return new DecimalFormat("0.00").format(val);
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 输入字符串
     * @return 返回结算结果
     */
    public static boolean isEmpty(Object str) {
        boolean flag = false;
        if (str == null) {
            flag = true;
        } else if ("".equals(str)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将格式为 str1,str2,str3,.....
     * 转化为  ‘str’,'str2','str3'  可查询的字符串
     * 注意查询条件中 in 后面的字符有长度限制，不能无限度拼接
     *
     * @param msg
     * @return
     */
    public static String formatStr(String msg) {
        StringBuffer str = new StringBuffer();
        if (!StrUtil.isEmpty(msg) && msg.contains(",")) {
            msg = msg.replace(",", "','");
            str.append("'");
            str.append(msg);
            str.append("'");
        }
        return str.toString();
    }


    /**
     * 将字符串对象去空格或者NULL对象变""
     *
     * @param str
     * @return
     */
    public static String trimNull(String str) {
        if (str == null || "null".equals(str)) {
            str = "";
        }
        return str.trim();
    }

    /**
     * 1,2,3 转 '1','2','3'
     *
     * @param strs 参数 1,2,3
     * @return 结果 '1','2','3'
     */
    public static String getInStr(String strs) {
        StringBuilder sb = new StringBuilder();
        if (strs != null) {
            if (strs.startsWith(",")) {
                strs = strs.substring(1);
            }
            String[] arr = strs.split(",");
            for (String str : arr) {
                if (!StrUtil.isEmpty(str))
                    sb.append(",'" + str + "'");
            }
            if (sb.length() > 0) {
                return sb.substring(1);
            }
        }
        return "";
    }

    /**
     * 数字转换为字符串 3转为 1,2,3
     *
     * @param i
     * @return
     */
    public static String intToStr(int i) {
        if (i == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int j = 1; j <= i; j++) {
            stringBuffer.append(j);
            stringBuffer.append(",");
        }
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }

    /**
     * 截取文件名的后缀
     *
     * @param fileName
     * @return:
     * @author zhouxy
     * @time 2021/06/11 09:34
     */
    public static String subSuffix(String fileName) {
        String suffix = "";
        try {
            if (fileName.contains(".")) {
                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            } else {
                //智能卷宗中 OCR扫描中文件的后缀名为_结尾
                suffix = fileName.substring(fileName.lastIndexOf("_") + 1);
            }
        } catch (Exception e) {
            log.error("[subSuffix]截取后缀错误，fileName:" + fileName, e);
        }
        return "." + suffix;
    }


}

