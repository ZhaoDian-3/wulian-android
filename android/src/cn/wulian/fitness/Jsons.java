package cn.wulian.fitness;
import org.json.*;
public class Jsons {
 public static JSONObject obj(String s){try{return new JSONObject(s);}catch(Exception e){throw new IllegalArgumentException(e);}}
 public static JSONObject obj(){return new JSONObject();}
 public static JSONObject put(JSONObject o,String k,Object v){try{o.put(k,v);return o;}catch(Exception e){throw new IllegalArgumentException(e);}}
 public static JSONObject copy(JSONObject o){return obj(o.toString());}
 public static JSONObject o(Object... kv){JSONObject o=obj();for(int i=0;i<kv.length;i+=2)put(o,(String)kv[i],kv[i+1]);return o;}
 public static Object number(String s){if(s.trim().isEmpty())return "";double n=Double.parseDouble(s);if(!Double.isFinite(n)||n<0||n>1000)throw new IllegalArgumentException("请输入 0～1000 的数字");return n;}
}
