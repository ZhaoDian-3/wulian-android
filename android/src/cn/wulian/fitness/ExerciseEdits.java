package cn.wulian.fitness;
import org.json.JSONObject;
import static cn.wulian.fitness.Jsons.*;
/** Changes display metadata without touching completed sets or their values. */
public final class ExerciseEdits {
 private ExerciseEdits() {}
 public static void validate(String name,String phase,String note) {
  if(name.trim().isEmpty()||name.length()>100)throw new IllegalArgumentException("动作名称请输入 1～100 字");
  if(phase.trim().isEmpty()||phase.length()>30)throw new IllegalArgumentException("动作分类请输入 1～30 字");
  if(note.length()>1000)throw new IllegalArgumentException("动作注释最多 1000 字");
 }
 public static void apply(JSONObject exercise,String name,String phase,String note) {
  validate(name,phase,note);
  put(exercise,"name",name.trim());put(exercise,"phase",phase.trim());put(exercise,"note",note);
 }
}
