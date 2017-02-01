package com.systemsplanet.vipaccess;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import com.google.common.io.BaseEncoding;
import com.systemsplanet.vipaccess.log.Log;

public class VipEnc {
    public static Log LOG = new Log();


    public static String bytesToHex(byte[] bin) {
        return Hex.encodeHexString(bin);
    }

    public static String bytesToBase64(byte[] bin) {
        return BaseEncoding.base64().encode(bin);
    }

    public static String bytesToBase32(byte[] bin) {
        return BaseEncoding.base32().encode(bin);
    }

    public static byte[] base32ToBytes(String str32) {
        return BaseEncoding.base32().decode(str32);
    }

    public static byte[] base64ToBytes(String str64) {
        return BaseEncoding.base64().decode(str64);
    }


    public static byte[] hexToBytes(String s) {
        s = s == null ? "" : s;
        byte[] result = new byte[0];
        try {
            result = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException e) {
            LOG.error("hexToBytes unable to decode:[" + s + "]", e);
        }
        return result;
    }

    public static byte[] crypt(byte[] encKey, byte[] data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(encKey, "HmacSHA256");
        hmac.init(key);
        byte[] result = hmac.doFinal(data);
        return result;
    }

    // Decrypt the One-time-password Key using the hardcoded AES key
    // AES.new(TOKEN_ENCRYPTION_KEY, AES.MODE_CBC, iv)
    public static byte[] decrypt(byte[] encKey, byte[] iv, byte[] cipher) throws Exception {
        byte[] result;
        LOG.debug("iv:" + bytesToHex(iv));
        LOG.debug("cipher:" + bytesToHex(cipher));
        Cipher c = Cipher.getInstance("AES/CBC/NoPadding"); // PKCS5Padding NoPadding
        Key key = new SecretKeySpec(encKey, "AES");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        result = c.doFinal(cipher);
        LOG.debug("padded:" + bytesToHex(result));
        int padlen = result[result.length - 1]; // PKCS7 pad len
        int len = result.length - padlen;
        if (padlen < 0 || len < 0) return result;
        byte[] out = new byte[len];
        System.arraycopy(result, 0, out, 0, len);
        LOG.debug("decrypted:" + bytesToHex(out));
        return out;
    }

}
