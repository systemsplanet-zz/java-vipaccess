package com.systemsplanet.vipaccess;

import com.lochbridge.oath.otp.HmacShaAlgorithm;

public class VipConst {

    public static final String           DIST_CHANNEL     = "Symantec";

    public static final String           OTP_TYPE         = "totp";

    public static final String           APP              = "VIP Access";

    public static final String           PROVISION_HOST   = "services.vip.symantec.com";

    public static final String           PROVISION_PATH   = "prov";

    public static final String           TEST_HOST        = "idprotect.vip.symantec.com";

    public static final String           TEST_PATH        = "testtoken.v";

    public static final String           MIME_XML         = "application/xml";

    public static final String           MIME_FORM        = "application/x-www-form-urlencoded";

    public static final String           ID_TYPE          = "BOARDID";

    public static final String           CLIENT_ID_PREFIX = "Mac-";

    public static final String           MODEL_MOBILE     = "VSMT";

    public static final String           MODEL_DESKTOP    = "VSST";

    public static final String           ALGORITHM        = "HMAC-SHA1-TRUNC-6DIGITS";

    public static final String           DELIVERY         = "HTTPS";

    public static final String           OS               = "MacBookPro8,1";

    public static final String           PLATFORM         = "iMac";

    public static final String           APP_HANDLE       = "iMac010200";

    public static final String           MFG              = "Apple Inc.";

    public static final int              TIME_WINDOW      = 1;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // higher

    public static final long             TIME_STEP_MS     = 30 * 1000L;

    public static final int              DIGITS           = 6;

    public static final HmacShaAlgorithm ALGO             = HmacShaAlgorithm.HMAC_SHA_1;

    public static final String           HMAC_KEY_BYTES   = "dd0ba692c38aa3a993a3aa26968cd9c2aa2aa2cb23b7c2d2aaaf8f8fc9a0a9a1";

    public static byte[]                 HMAC_KEY         = VipEnc.hexToBytes(HMAC_KEY_BYTES);

    public static final String           AES_KEY_BYTES    = "01ad9bc682a3aa93a9a3239a86d6ccd9";                                                                                                                                                                                                                                                                                                                                 // 16

    public static byte[]                 AES_KEY          = VipEnc.hexToBytes(AES_KEY_BYTES);
}
