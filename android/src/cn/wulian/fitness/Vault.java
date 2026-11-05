package cn.wulian.fitness;
import android.content.*;import android.security.keystore.*;import android.util.Base64;import javax.crypto.*;import javax.crypto.spec.GCMParameterSpec;import java.security.KeyStore;import java.nio.charset.StandardCharsets;
public class Vault {
 private static final String ALIAS="five-training-local-v1";
 private static javax.crypto.SecretKey key()throws Exception{KeyStore k=KeyStore.getInstance("AndroidKeyStore");k.load(null);if(!k.containsAlias(ALIAS)){KeyGenerator g=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");g.init(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());g.generateKey();}return (javax.crypto.SecretKey)k.getKey(ALIAS,null);}
 public static String encrypt(String s)throws Exception{Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.ENCRYPT_MODE,key());return Base64.encodeToString(c.getIV(),Base64.NO_WRAP)+":"+Base64.encodeToString(c.doFinal(s.getBytes(StandardCharsets.UTF_8)),Base64.NO_WRAP);}
 public static String decrypt(String s)throws Exception{String[] a=s.split(":");Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,Base64.decode(a[0],0)));return new String(c.doFinal(Base64.decode(a[1],0)),StandardCharsets.UTF_8);}
 public static void token(Context c,String t)throws Exception{c.getSharedPreferences("account",0).edit().putString("token",encrypt(t)).commit();}
 public static String token(Context c){try{String s=c.getSharedPreferences("account",0).getString("token","");return s.isEmpty()?"":decrypt(s);}catch(Exception e){return "";}}
}
