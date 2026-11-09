package cn.wulian.fitness;
import org.json.*;
import static cn.wulian.fitness.Jsons.*;
public class ExerciseEditsTest {
 static void check(boolean ok){if(!ok)throw new AssertionError();}
 public static void main(String[] args){
  JSONObject plan=o("name","卧推","phase","正式组","note","旧注释","sets",4,"reps",12,"weight",50);
  JSONObject record=o("day",o("items",new JSONArray().put(copy(plan))),"rows",new JSONArray().put(new JSONArray().put(o("weight",55,"reps",10,"done",true))),"finished",true);
  String rows=record.optJSONArray("rows").toString();
  JSONObject item=record.optJSONObject("day").optJSONArray("items").optJSONObject(0);
  ExerciseEdits.apply(item,"哑铃推胸","适应组","肩胛下沉\n座椅第 3 档");
  check(item.optString("name").equals("哑铃推胸"));check(item.optString("note").contains("\n"));
  check(record.optJSONArray("rows").toString().equals(rows));check(record.optBoolean("finished"));
  check(item.optInt("sets")==4&&item.optInt("weight")==50&&item.optInt("reps")==12);
  check(plan.optString("name").equals("卧推")&&plan.optString("note").equals("旧注释"));
  ExerciseEdits.apply(plan,"哑铃推胸","适应组","");check(plan.optString("note").isEmpty());
  check(item.optString("note").contains("座椅"));
  for(String[] bad:new String[][]{{" ","正式组",""},{"卧推"," ",""},{"卧推","正式组",new String(new char[1001]).replace('\0','x')}}){
   String before=item.toString();boolean rejected=false;try{ExerciseEdits.apply(item,bad[0],bad[1],bad[2]);}catch(IllegalArgumentException ex){rejected=true;}
   check(rejected&&before.equals(item.toString()));
  }
  JSONObject deletion=o("day",o("items",new JSONArray().put(o("name","A")).put(o("name","B")).put(o("name","C"))),"rows",new JSONArray().put(new JSONArray().put(o("weight",10))).put(new JSONArray().put(o("weight",20))).put(new JSONArray().put(o("weight",30,"done",true))));
  JSONObject history=copy(deletion);ExerciseEdits.removeRecordItem(deletion,1);
  check(deletion.optJSONObject("day").optJSONArray("items").optJSONObject(1).optString("name").equals("C"));
  check(deletion.optJSONArray("rows").optJSONArray(1).optJSONObject(0).optInt("weight")==30);
  check(deletion.optJSONArray("rows").optJSONArray(1).optJSONObject(0).optBoolean("done"));
  check(history.optJSONArray("rows").length()==3);
  ExerciseEdits.removeRecordItem(deletion,0);String beforeDelete=deletion.toString();boolean blocked=false;
  try{ExerciseEdits.removeRecordItem(deletion,0);}catch(IllegalArgumentException ex){blocked=true;}check(blocked&&beforeDelete.equals(deletion.toString()));
  JSONObject broken=copy(history);broken.optJSONArray("rows").remove(0);String beforeBroken=broken.toString();blocked=false;
  try{ExerciseEdits.removeRecordItem(broken,1);}catch(IllegalArgumentException ex){blocked=true;}check(blocked&&beforeBroken.equals(broken.toString()));
  System.out.println("PASS: edit metadata, preserve sets/history, independent plan scope, clear notes, reject invalid input");
 }
}
