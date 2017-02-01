package com.systemsplanet.vipaccess.http;

import com.lochbridge.oath.otp.TOTP;
import com.systemsplanet.vipaccess.VipConst;
import com.systemsplanet.vipaccess.VipEnc;
import com.systemsplanet.vipaccess.log.Log;

public class VipRequest {
    static Log LOG = new Log();

    public String getTestResponse(String data) throws Exception {
        LOG.debug("test request:\n" + data);
        String response = VipHttp.post(VipConst.TEST_HOST, VipConst.TEST_PATH, VipConst.MIME_FORM, data);
        LOG.debug("test response:\n" + response);
        return response;
    }

    public String send(String data) throws Exception {
        LOG.debug("provision request:\n" + data);
        String response = VipHttp.post(VipConst.PROVISION_HOST, VipConst.PROVISION_PATH, VipConst.MIME_XML, data);
        LOG.debug("provision response:\n" + response);
        return response;
    }

    // validate the token received from Symantec. 6 digits, 30 seconds, SHA1
    public boolean checkCredential(String id, byte[] secret) throws Exception {
        boolean result = false;
        VipEnc.LOG.debug("check_token:" + VipEnc.bytesToHex(secret));
        // totp(binascii.b2a_hex(secret).decode('utf-8'))
        TOTP totp = TOTP.key(secret).timeStep(VipConst.TIME_STEP_MS).digits(VipConst.DIGITS).hmacSha1().build();
        String otp = totp.value();
        String req = String.format("tokenID=%s&firstOTP=%s", id, otp);
        String reply = getTestResponse(req);
        result = reply.indexOf("Your credential is functioning properly") != -1;
        return result;
    }

}
