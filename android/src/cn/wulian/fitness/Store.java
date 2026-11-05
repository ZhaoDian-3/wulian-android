package cn.wulian.fitness;
import org.json.*;import android.content.*;import android.util.AtomicFile;import java.io.*;import java.nio.charset.StandardCharsets;import java.security.*;import java.util.*;
import static cn.wulian.fitness.Jsons.*;
public class Store {
 private JSONObject all;private AtomicFile file;public final String uid;
 Store(Context c,String user)throws Exception{uid=user;String digest=hex(MessageDigest.getInstance("SHA-256").digest(user.getBytes(StandardCharsets.UTF_8)));file=new AtomicFile(new File(c.getFilesDir(),digest+".vault"));if(file.getBaseFile().exists())all=obj(Vault.decrypt(new String(file.readFully(),StandardCharsets.UTF_8)));else all=obj();}
 static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}
 public synchronized void flush(){FileOutputStream f=null;try{byte[] bytes=Vault.encrypt(all.toString()).getBytes(StandardCharsets.UTF_8);f=file.startWrite();f.write(bytes);file.finishWrite(f);}catch(Exception e){if(f!=null)file.failWrite(f);throw new IllegalStateException("本机保存失败，请检查空间",e);}}
 public synchronized JSONObject get(String id){JSONObject r=all.optJSONObject(id);return r==null?null:copy(r);}
 public synchronized ArrayList<JSONObject> list(String kind){ArrayList<JSONObject> a=new ArrayList<>();Iterator<String> it=all.keys();while(it.hasNext()){JSONObject r=all.optJSONObject(it.next());if(kind==null||r.optString("kind").equals(kind))a.add(copy(r));}a.sort((x,y)->y.optString("id").compareTo(x.optString("id")));return a;}
 public synchronized void save(String id,String kind,JSONObject body){JSONObject old=all.optJSONObject(id);put(all,id,o("id",id,"kind",kind,"body",copy(body),"version",old==null?0:old.optInt("version"),"rev",old==null?1:old.optInt("rev")+1,"dirty",true));flush();}
 public synchronized int pending(){int n=0;for(JSONObject r:list(null))if(r.optBoolean("dirty"))n++;return n;}
 public synchronized void uploaded(JSONObject sent,JSONObject result){String id=sent.optString("id");JSONObject r=all.optJSONObject(id);if(r==null)return;put(r,"version",result.optInt("version"));if(r.optInt("rev")==sent.optInt("rev"))put(r,"dirty",false);flush();}
 public synchronized void pulled(JSONObject cloud){String id=cloud.optString("id");JSONObject local=all.optJSONObject(id);if(local==null||(!local.optBoolean("dirty")&&cloud.optInt("version")>local.optInt("version"))){put(cloud,"dirty",false);put(cloud,"rev",local==null?1:local.optInt("rev")+1);put(all,id,cloud);flush();}}
 public synchronized String conflict(JSONObject cloud,String id){JSONObject local=all.optJSONObject(id);if(local==null||cloud==null)return "";String backup=local.optString("kind")+"_copy_"+UUID.randomUUID().toString();JSONObject b=copy(local);put(b,"id",backup);put(b,"version",0);put(b,"dirty",true);if(b.optString("kind").equals("plan")){JSONObject plan=b.optJSONObject("body");put(plan,"name",plan.optString("name")+"（本机副本）");}else{JSONObject body=b.optJSONObject("body");put(body,"note",("并发修改保留的本机副本。 "+body.optString("note")).substring(0,Math.min(4000,("并发修改保留的本机副本。 "+body.optString("note")).length())));}
 put(all,backup,b);put(cloud,"dirty",false);put(cloud,"rev",local.optInt("rev")+1);put(all,id,cloud);flush();return backup;}
 public synchronized void clear(){all=obj();file.delete();}
}
