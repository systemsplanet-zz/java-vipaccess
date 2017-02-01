package com.systemsplanet.vipaccess.http;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import com.systemsplanet.vipaccess.VipConst;
import com.systemsplanet.vipaccess.VipEnc;

/*-

                                        CREATE A NEW SYMANTECH SECURE KEY

            POST /prov HTTP/1.1
            Host: services.vip.symantec.com
            Connection: keep-alive
            Accept: *\/*
            Accept-Encoding: gzip, deflate
            User-Agent: python-requests/2.12.5
            Content-Length: 1133

            <?xml version="1.0" encoding="UTF-8" ?>
            <GetSharedSecret Id="1485223391" Version="2.0"
                xmlns="http://www.verisign.com/2006/08/vipservice"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <TokenModel>VSMT</TokenModel>
                <ActivationCode></ActivationCode>
                <OtpAlgorithm type="HMAC-SHA1-TRUNC-6DIGITS"/>
                <SharedSecretDeliveryMethod>HTTPS</SharedSecretDeliveryMethod>
                <DeviceId>
                    <Manufacturer>Apple Inc.</Manufacturer>
                    <SerialNo>VZ7K5MXB3A0Q</SerialNo>
                    <Model>MacBookPro11,3</Model>
                </DeviceId>
                <Extension extVersion="auth" xsi:type="vip:ProvisionInfoType"
                    xmlns:vip="http://www.verisign.com/2006/08/vipservice">
                    <AppHandle>iMac010200</AppHandle>
                    <ClientIDType>BOARDID</ClientIDType>
                    <ClientID>Mac-980DC3811ADCE61E</ClientID>
                    <DistChannel>Symantec</DistChannel>
                    <ClientInfo>
                        <os>MacBookPro11,3</os>
                        <platform>iMac</platform>
                    </ClientInfo>
                    <ClientTimestamp>1485223391</ClientTimestamp>
                    <Data>Nd5hKFb35v+ub9a0XOv1qHeJqQTg2ctbXymNhQxdWOI=</Data>
                </Extension>
            </GetSharedSecret>

 */

public class VipProvision {

    // @formatter:off
    static String REQUEST_TEMPLATE = "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                "<GetSharedSecret Id='%d' Version='2.0' xmlns='http://www.verisign.com/2006/08/vipservice' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>\n" +
                "<TokenModel>%s</TokenModel>\n" +
                "<ActivationCode></ActivationCode>\n" +
                "<OtpAlgorithm type='%s'/>\n" +
                "<SharedSecretDeliveryMethod>%s</SharedSecretDeliveryMethod>\n" +
                "<DeviceId>\n" +
                "<Manufacturer>%s</Manufacturer>\n" +
                "<SerialNo>%s</SerialNo>\n" +
                "<Model>%s</Model>\n" +
                "</DeviceId>\n" +
                "<Extension extVersion='auth' xsi:type='vip:ProvisionInfoType' xmlns:vip='http://www.verisign.com/2006/08/vipservice'>\n" +
                "<AppHandle>%s</AppHandle>\n" +
                "<ClientIDType>%s</ClientIDType>\n" +
                "<ClientID>%s</ClientID>\n" +
                "<DistChannel>%s</DistChannel>\n" +
                "<ClientInfo>\n" +
                "<os>%s</os>\n" +
                "<platform>%s</platform>\n" +
                "</ClientInfo>\n" +
                "<ClientTimestamp>%d</ClientTimestamp>\n" +
                "<Data>%s</Data>\n" +
                "</Extension>\n" +
                "</GetSharedSecret>\n";
    // @formatter:on

    static {
        REQUEST_TEMPLATE = REQUEST_TEMPLATE.replaceAll("'", "\"");
    }

    // Generate a token provisioning request.
    public static String getRequestXml(String mode) throws Exception {
        long id = new Date().getTime();
        String ts = "" + id;
        String clientId = clientId();
        StringBuilder sb = new StringBuilder(); // data_before_hmac
        sb.append(ts);
        sb.append(ts);
        sb.append(VipConst.ID_TYPE);
        sb.append(clientId);
        sb.append(VipConst.DIST_CHANNEL);
        String dataBeforeHmac = sb.toString();  // 14849676081484967608BOARDIDMac-0123456789012345Symantec
        byte[] result = VipEnc.crypt(VipConst.HMAC_KEY, dataBeforeHmac.getBytes("UTF-8"));
        // data = base64.b64encode( hmac.new(HMAC_KEY,data_before_hmac.encode('utf-8'),hashlib.sha256).digest() ).decode('utf-8')
        String data = VipEnc.bytesToBase64(result);
        // @formatter:off
        return String.format(REQUEST_TEMPLATE, id, mode, VipConst.ALGORITHM, VipConst.DELIVERY,VipConst.MFG,
                    serial(), model(), VipConst.APP_HANDLE, VipConst.ID_TYPE, clientId,
                    VipConst.DIST_CHANNEL,VipConst.OS, VipConst.PLATFORM , id, data);
        // @formatter:on
    }

    private static String serial() {
        String allowed = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int max = allowed.length();
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 12; x++) {
            sb.append(allowed.charAt(ThreadLocalRandom.current().nextInt(0, max)));
        }
        return sb.toString();
    }

    private static String model() {
        return String.format("MacBookPro%d,%d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(1, 4));
    }

    private static String clientId() {
        String allowed = "0123456789ABCDEF";
        int max = allowed.length();
        StringBuilder sb = new StringBuilder(VipConst.CLIENT_ID_PREFIX); // "Mac-"
        for (int x = 0; x < 16; x++) {
            sb.append(allowed.charAt(ThreadLocalRandom.current().nextInt(0, max)));
        }
        return sb.toString();
    }

}
