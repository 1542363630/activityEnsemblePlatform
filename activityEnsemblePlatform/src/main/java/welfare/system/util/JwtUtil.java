package welfare.system.util;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.SneakyThrows;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

/*
* JWT加解密工具类
* */

public class JwtUtil {
    //jwt加密方式
    
    private static final String CLAIM_KEY = "casID";
    
    // 十秒过期
    private static final long expire = 604800;
    
    @SuppressWarnings("SpellCheckingInspection")
    
    private static final String SECRET = "kisskoishikisskoishikisskisslovelylovelysolovely";
    
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    public static final SecureDigestAlgorithm<SecretKey,SecretKey> ALGORITHM = Jwts.SIG.HS256;

    private static final byte[] salt = "KOISHIKISHIKAWAIIKAWAIIKISSKISSLOVELY".getBytes(StandardCharsets.UTF_8);
    
    private static final int iterationCount = 114514;

    private static final Map<String, SecretKey> KEY_CACHE = new ConcurrentHashMap<>();

    //生成token
    public static String generateToken(int uid){

        Date now = new Date();
        Date expiration = new Date(now.getTime()+1000*expire);

        return Jwts.builder()
                .header().add("type","JWT")
                .and()
                .claim("uid", uid)
                .expiration(expiration)
                .signWith(KEY,ALGORITHM)
                .compact();
    }

    //解析token
    public static int getClaimsByToken(String token){
        try {
            return Integer.parseInt(Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("uid")
                    .toString());
        }catch (Exception e){
            return -1;
        }
    }

    @SneakyThrows
    private static SecretKey generateSecretKey(String key) {
        SecretKey secretKey = KEY_CACHE.get(key);
        if (secretKey == null) {
            PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), salt, iterationCount, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] secretBytes = factory.generateSecret(spec).getEncoded();
            secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
            KEY_CACHE.put(key, secretKey);
        }
        return secretKey;
    }

    public static String generate(String obj, String key) {
        SecretKey secretKey = generateSecretKey(key);
        return Jwts.builder()
                .header().add("type","JWT")
                .and()
                .claim(CLAIM_KEY, obj)
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(secretKey,ALGORITHM)
                .compact();
    }

    //解析token，得到包装的casId
    public static String getClaim(String token, String key){
        SecretKey secretKey = generateSecretKey(key);
        try {
            return (String) Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(CLAIM_KEY);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
