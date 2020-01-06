package com.php25.common.websocketsample;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.RsaAlgorithm;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.security.PrivateKey;
import java.util.Date;

/**
 * @author penghuiping
 * @date 2019/12/31 15:27
 */
@Slf4j
public class SocketJsTest {

    private static final String jwtPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC65LajTXh0dZ6mZajwsr+5XdbxdmmJKd91BJ0JFLutZyT3PiuOK8YVMQeByJ9NwD3sgcjXaTlOsOGjQUh++yFljea/nnS+kERuBXE16P26yCWMM7hIL/MiYsU4OWyRrn65cf/uNEE5GbZs59quzOsUGU/gb9sf1a3ztHk3btyqhBBIPKreo8EVTSMf2FiW92EZYtBXuBDjsFa9MHVRIaAooMufyVI7AsZwKsiBuVT60wuMtlJhYBsj+WslUSlWCsLpmJ3zJaXPawmplLONBophb3WOzOIVPUowivHWQvBshTbS+GRN4itB+Cc+ur7JLWMtkxO6UmWBxmyHw3BXWmf9AgMBAAECggEAAqp6/HhkxGfGTKbmN0DB8Mf5fd+bK31P0+xYqiW4HuTFzXCgOG2VklK0Kjp6bx0DPgHxtntJu/KzY39V+bxlVWA4/+9qFFDsz3ugwt1UAFgLgfWNIbyNqTR2qqAFl75BEHCCRKUKsiysWfuki8mP3AAqp7WjuaAL1I5O5fnKFp+upe/qlZamX0nCSBBmw8IdvsL1WsYaqi/ZByyxolnh6/1EzB9UOrts1BxItda9paR4w5f+rFbXnIkscKg02xgiiCf/VIABXMtDEiTI2wL+c4ucuXgdcyaEKWUG5bYuL9/lNe2t34pUuTk5Ufxt8+hIwxaqgvStGC96ska2CSegFQKBgQDo13I7tO4rRoyGabaU47gwTyBXgTVprbMmKw/E6e5oRMajQ3epf/Db0Jcqfo532F96FnzlqPSunj9DkwTuDvXM9WbuLL2ptQ9Zb6bk9apLFMtrUGBINp3siPDo/rav1iSKxwpupkUy7TvUcucL5oPHMDtHWDtGSeOb8vaOoA+OowKBgQDNe1irBBaoNmPtua/2/xUdVyAHEyrIUQo8EOPy7H/4kQ9HvCirV0Dk1mznGBQx2V1Lzq4MTuP86kkOFwEURPmOsn/7uc8ZEDJyTvaCXgmAelBqGJZB0CYSEZt51k875ZN2FmVjUDEtWthzd7mhNN8UEq0xopq2uGIPpD819Bi43wKBgA7eWywW5LKcwbJ/o6okVIm5M86CsL972RnR3CLt8Ux1P94DG+wWAHJ5An4zMLPUfFxWfUlJHb7c2htdkw4EpIufDCCfeMkSh9VlPNoYGfTLciX7LvrNig4lvISUZ2QZH6JBDZh9Q6P55D0vzTNEWBvrJ43pw3c8lq1JuZbn/7ZTAoGAIiWsX4cnaL7ZLmlcR7SuQpj29pYF1xI4nDDGYNlSjvE0U9x9+bNfUmgb4u+Kc5pGudFX9S2rD52zlEbYZBDuU/tv71o6g1TAvWRH7PRJqfMpp+f8GGXJ3djVOJMXycFmFwqzwKRT99CQBehQjeymYcSUyZnXEiRkD2thIcs1zIsCgYEAuN9ecHqHLhS+M+ecBOatHc6NZ3W8k0sGjhcAJGIx5DT0kKVDICv/OxiZtXoPDPB7E1NM1nsPSG+8CHXmnb7GWKN5mvBWOdwpYC4MB1DneaDB/YVRqFL3/9IyyBATYVx4M7CAx3xxU96Dq2D12+c4ZIp/5WO/SubOyK9ILmtc+4k=";

    @Test
    public void test() {
        PrivateKey privateKey = SecretKeyUtil.generatePrivateKey(RsaAlgorithm.RSA.getValue(), DigestUtil.decodeBase64(jwtPrivateKey));
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuer("www.php25.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7200 * 1000))
                .setHeaderParam("scp", Lists.newArrayList("role_user"))
                .setSubject("test_user1")
                .compact();

        log.info("jwt:{}",jwt);
    }
}
