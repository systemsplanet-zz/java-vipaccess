package com.systemsplanet.vipaccess;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.lochbridge.oath.otp.TOTP;
import com.lochbridge.oath.otp.TOTPValidator;
import com.systemsplanet.vipaccess.VipConst;
import com.systemsplanet.vipaccess.VipEnc;
import com.systemsplanet.vipaccess.http.VipRequest;
import com.systemsplanet.vipaccess.log.Log;

public class VipEncTest {
    static Log LOG = new Log();

    @Test
    public void test_binToBase64() throws Exception {
        byte[] bytes = VipEnc.hexToBytes("c726a17812ceb9a9900d9975d47e364a7fdba8948c12963b18e21801181b6d57");
        String base64 = VipEnc.bytesToBase64(bytes);
        assertThat(base64).isEqualTo("xyaheBLOuamQDZl11H42Sn/bqJSMEpY7GOIYARgbbVc=");
    }

    @Test
    public void test_binToBase32() throws Exception {
        byte[] bytes = VipEnc.hexToBytes("3cb3f7c1b540b4ddf4cfea2e25b0129857545856");
        String base64 = VipEnc.bytesToBase32(bytes);
        assertThat(base64).isNotNull();
        assertThat(base64).isEqualTo("HSZ7PQNVIC2N35GP5IXCLMASTBLVIWCW");
    }

    @Test
    public void test_b32ToBytes() throws Exception {
        String b32 = "NV55C75WN3UMR3WXTH6BXK3RHRLCQONC";
        byte[] expectedBytes = VipEnc.hexToBytes("6d7bd17fb66ee8c8eed799fc1bab713c562839a2");
        byte[] bytes = VipEnc.base32ToBytes(b32);
        assertThat(bytes).isEqualTo(expectedBytes);
    }

    @Test
    public void test_b64ToBytes() throws Exception
    {
        String b64 = "xyaheBLOuamQDZl11H42Sn/bqJSMEpY7GOIYARgbbVc=";
        byte[] expectedBytes = VipEnc.hexToBytes("c726a17812ceb9a9900d9975d47e364a7fdba8948c12963b18e21801181b6d57");
        byte[] bytes = VipEnc.base64ToBytes(b64);
        assertThat(bytes).isEqualTo(expectedBytes);
    }

    @Test
    public void test_binToHex() {
        byte[] bytes = new byte[] { 0, 1, 2, 10, 11, 12, (byte) 255 };
        String hex = VipEnc.bytesToHex(bytes);
        assertThat(hex).isEqualTo("0001020a0b0cff");
    }

    @Test
    public void test_hexToBin() throws Exception {
        byte[] bytes = VipEnc.hexToBytes("01ff");
        assertThat(bytes[0]).isEqualTo((byte) 1);
        assertThat(bytes[1]).isEqualTo((byte) 255);
    }

    @Test
    public void test_decrypt() throws Exception {
        byte[] token_iv = VipEnc.hexToBytes("617bc21cf9c4b6904fb983a009f83778");
        byte[] token_cipher = VipEnc.hexToBytes("f2ae35784b7de3341e5809faeac828927c31e55654f759be85603915fe9ca15a");
        byte[] TOKEN_ENCRYPTION_KEY = VipEnc.hexToBytes("01ad9bc682a3aa93a9a3239a86d6ccd9");
        byte[] secretExpected = VipEnc.hexToBytes("3cb3f7c1b540b4ddf4cfea2e25b0129857545856");
        byte[] secret = VipEnc.decrypt(TOKEN_ENCRYPTION_KEY, token_iv, token_cipher);
        assertThat(secret).isEqualTo(secretExpected);
    }

    // validate the token received from Symantec
    @Test
    public void test_TOTPValidator() {
        byte[] secret = VipEnc.base32ToBytes("NV55C75WN3UMR3WXTH6BXK3RHRLCQONC");
        String otp = "809560";
        long now = 1485305376819L;
        boolean passed = TOTPValidator.window(VipConst.TIME_WINDOW).isValid(secret, VipConst.TIME_STEP_MS, VipConst.DIGITS, VipConst.ALGO, otp, now);
        assertThat(passed).isTrue();
    }

    @Test
    public void testTOTPWindowZero() {
        long now = 1485305376819L;
        byte[] secret = VipEnc.base32ToBytes("NV55C75WN3UMR3WXTH6BXK3RHRLCQONC");
        TOTP totp = TOTP.key(secret).timeStep(VipConst.TIME_STEP_MS).digits(VipConst.DIGITS).hmacSha1().build(now);
        assertThat(totp.value()).isEqualTo("964730");
    }

    // validate the token received from Symantec
    @Test
    public void testTOTPValidatorNow() {
        byte[] secret = VipEnc.base32ToBytes("NV55C75WN3UMR3WXTH6BXK3RHRLCQONC");
        TOTP totp = TOTP.key(secret).timeStep(VipConst.TIME_STEP_MS).digits(VipConst.DIGITS).hmacSha1().build();
        String otp = totp.value();
        long now = new Date().getTime();
        boolean passed = TOTPValidator.window(VipConst.TIME_WINDOW).isValid(secret, VipConst.TIME_STEP_MS, VipConst.DIGITS, VipConst.ALGO, otp, now);
        assertThat(passed).isTrue();
    }


    @Test
    public void test_crypt() throws Exception {
        byte[] expected = VipEnc.hexToBytes("e10f774d57b8aa5389b76bfb3b47884fc888fd22d696fd47a33216e82435e6c0");
        byte[] secret = VipEnc.crypt(VipEnc
                    .hexToBytes("dd0ba692c38aa3a993a3aa26968cd9c2aa2aa2cb23b7c2d2aaaf8f8fc9a0a9a1"), "14856659591485665959BOARDIDMac-25299F222BC21776Symantec"
                    .getBytes("UTF-8"));
        LOG.debug("secret=" + VipEnc.bytesToHex(secret));
        assertThat(secret).isEqualTo(expected);
    }

    // WARNING: this will fail if less than 30 seconds have passed since last run
    @Ignore
    public void test_PostTest() throws Exception {
        byte[] secret = VipEnc.hexToBytes("3cb3f7c1b540b4ddf4cfea2e25b0129857545856");
        boolean passed = new VipRequest().checkCredential("VSMT66559715", secret);
        assertThat(passed).isEqualTo(true);
    }

    @BeforeClass
    public static void init() throws Exception {
    }

    @Before
    public void setup() throws Exception {
        Log.setDEBUG(true);
    }

    @Ignore
    public void testIgnored() throws Exception {
        assert 1 == 2;
    }

}
