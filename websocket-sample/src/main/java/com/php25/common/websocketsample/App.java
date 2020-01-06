package com.php25.common.websocketsample;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.SignAlgorithm;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Controller;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2019/12/31 15:09
 */
@Slf4j
@SpringBootApplication
@Controller
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @MessageMapping("/talk")
    @SendToUser("/queue/reply")
    public String talk(
            @Payload String msg,
            @Header("simpSessionId") String sessionId) throws Exception {
        log.info("msg:{},sessionId:{}", msg, sessionId);
        return msg;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/reply")
    public String chat(
            @Payload String msg,
            @Header("simpSessionId") String sessionId) throws Exception {
        log.info("msg:{},sessionId:{}", msg, sessionId);
        return msg;
    }

    private static final String  jwtPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuuS2o014dHWepmWo8LK/uV3W8XZpiSnfdQSdCRS7rWck9z4rjivGFTEHgcifTcA97IHI12k5TrDho0FIfvshZY3mv550vpBEbgVxNej9usgljDO4SC/zImLFODlska5+uXH/7jRBORm2bOfarszrFBlP4G/bH9Wt87R5N27cqoQQSDyq3qPBFU0jH9hYlvdhGWLQV7gQ47BWvTB1USGgKKDLn8lSOwLGcCrIgblU+tMLjLZSYWAbI/lrJVEpVgrC6Zid8yWlz2sJqZSzjQaKYW91jsziFT1KMIrx1kLwbIU20vhkTeIrQfgnPrq+yS1jLZMTulJlgcZsh8NwV1pn/QIDAQAB";


    private static final String jwtPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC65LajTXh0dZ6mZajwsr+5XdbxdmmJKd91BJ0JFLutZyT3PiuOK8YVMQeByJ9NwD3sgcjXaTlOsOGjQUh++yFljea/nnS+kERuBXE16P26yCWMM7hIL/MiYsU4OWyRrn65cf/uNEE5GbZs59quzOsUGU/gb9sf1a3ztHk3btyqhBBIPKreo8EVTSMf2FiW92EZYtBXuBDjsFa9MHVRIaAooMufyVI7AsZwKsiBuVT60wuMtlJhYBsj+WslUSlWCsLpmJ3zJaXPawmplLONBophb3WOzOIVPUowivHWQvBshTbS+GRN4itB+Cc+ur7JLWMtkxO6UmWBxmyHw3BXWmf9AgMBAAECggEAAqp6/HhkxGfGTKbmN0DB8Mf5fd+bK31P0+xYqiW4HuTFzXCgOG2VklK0Kjp6bx0DPgHxtntJu/KzY39V+bxlVWA4/+9qFFDsz3ugwt1UAFgLgfWNIbyNqTR2qqAFl75BEHCCRKUKsiysWfuki8mP3AAqp7WjuaAL1I5O5fnKFp+upe/qlZamX0nCSBBmw8IdvsL1WsYaqi/ZByyxolnh6/1EzB9UOrts1BxItda9paR4w5f+rFbXnIkscKg02xgiiCf/VIABXMtDEiTI2wL+c4ucuXgdcyaEKWUG5bYuL9/lNe2t34pUuTk5Ufxt8+hIwxaqgvStGC96ska2CSegFQKBgQDo13I7tO4rRoyGabaU47gwTyBXgTVprbMmKw/E6e5oRMajQ3epf/Db0Jcqfo532F96FnzlqPSunj9DkwTuDvXM9WbuLL2ptQ9Zb6bk9apLFMtrUGBINp3siPDo/rav1iSKxwpupkUy7TvUcucL5oPHMDtHWDtGSeOb8vaOoA+OowKBgQDNe1irBBaoNmPtua/2/xUdVyAHEyrIUQo8EOPy7H/4kQ9HvCirV0Dk1mznGBQx2V1Lzq4MTuP86kkOFwEURPmOsn/7uc8ZEDJyTvaCXgmAelBqGJZB0CYSEZt51k875ZN2FmVjUDEtWthzd7mhNN8UEq0xopq2uGIPpD819Bi43wKBgA7eWywW5LKcwbJ/o6okVIm5M86CsL972RnR3CLt8Ux1P94DG+wWAHJ5An4zMLPUfFxWfUlJHb7c2htdkw4EpIufDCCfeMkSh9VlPNoYGfTLciX7LvrNig4lvISUZ2QZH6JBDZh9Q6P55D0vzTNEWBvrJ43pw3c8lq1JuZbn/7ZTAoGAIiWsX4cnaL7ZLmlcR7SuQpj29pYF1xI4nDDGYNlSjvE0U9x9+bNfUmgb4u+Kc5pGudFX9S2rD52zlEbYZBDuU/tv71o6g1TAvWRH7PRJqfMpp+f8GGXJ3djVOJMXycFmFwqzwKRT99CQBehQjeymYcSUyZnXEiRkD2thIcs1zIsCgYEAuN9ecHqHLhS+M+ecBOatHc6NZ3W8k0sGjhcAJGIx5DT0kKVDICv/OxiZtXoPDPB7E1NM1nsPSG+8CHXmnb7GWKN5mvBWOdwpYC4MB1DneaDB/YVRqFL3/9IyyBATYVx4M7CAx3xxU96Dq2D12+c4ZIp/5WO/SubOyK9ILmtc+4k=";


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        RSAPublicKey publicKey = (RSAPublicKey) SecretKeyUtil.generatePublicKey(SignAlgorithm.SHA256withRSA.getValue(), DigestUtil.decodeBase64(jwtPublicKey));
        return s -> {
            try {
                JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
                Jws<Claims> jwt = jwtParser.parseClaimsJws(s);

                Instant issueAt = jwt.getBody().getIssuedAt().toInstant();
                Instant expiration = jwt.getBody().getExpiration().toInstant();

                Map<String, Object> headers = new HashMap<>();
                if (null != jwt.getHeader() && !jwt.getHeader().isEmpty()) {
                    jwt.getHeader().forEach((Object key, Object value) -> {
                        headers.put((String) key, value);
                    });
                }

                Map<String, Object> bodys = new HashMap<>();
                if (null != jwt.getBody() && !jwt.getBody().isEmpty()) {
                    jwt.getBody().forEach((Object key, Object value) -> {
                        bodys.put((String) key, value);
                    });
                }
                bodys.put("scp", headers.get("scp"));
                return new Jwt(s, issueAt, expiration, headers, bodys);
            }catch (Exception e) {
                throw new JwtException("jwt不合法",e);
            }
        };

    }
}
