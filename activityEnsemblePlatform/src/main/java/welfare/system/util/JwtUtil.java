package welfare.system.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;

/*
* JWT加解密工具类
* */

public class JwtUtil {
    private static final long expire = 604800;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String SECRET = "kisskoishikisskoishikisskisslovelylovelysolovely";

    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static final SecureDigestAlgorithm<SecretKey,SecretKey> ALGORITHM = Jwts.SIG.HS256;

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
}
