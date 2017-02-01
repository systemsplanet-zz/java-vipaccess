package com.systemsplanet.vipaccess.http;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.systemsplanet.vipaccess.VipEnc;

/*-
                                        RESPONSE SECURE KEY FROM SYMANTECH

            HTTP/1.1 200 OK
            Date: Tue, 24 Jan 2017 02:18:49 GMT
            Server: Apache
            Content-Type: application/xml
            Content-Length: 1281
            Keep-Alive: timeout=15, max=20
            Connection: Keep-Alive

            <?xml version="1.0" encoding="UTF-8"?>
            <GetSharedSecretResponse RequestId="1485224328646" Version="2.0" xmlns="http://www.verisign.com/2006/08/vipservice">
              <Status>
                <ReasonCode>0000</ReasonCode>
                <StatusMessage>Success</StatusMessage>
              </Status>
              <SharedSecretDeliveryMethod>HTTPS</SharedSecretDeliveryMethod>
              <SecretContainer Version="1.0">
                <EncryptionMethod>
                  <PBESalt>8v0DNTlugG3NiwnOMJgEXBwNxCQ=</PBESalt>
                  <PBEIterationCount>50</PBEIterationCount>
                  <IV>KIFNUhhVilf1uyi5qceI3w==</IV>
                </EncryptionMethod>
                <Device>
                  <Secret type="HOTP" Id="VSMT73481358">
                    <Issuer>OU = ID Protection Center, O = Symantec</Issuer>
                    <Usage otp="true">
                      <AI type="HMAC-SHA1-TRUNC-6DIGITS"/>
                      <TimeStep>30</TimeStep>
                      <Time>0</Time>
                      <ClockDrift>4</ClockDrift>
                    </Usage>
                    <FriendlyName>OU = ID Protection Center, O = Symantec</FriendlyName>
                    <Data>
                      <Cipher>kSL6cJLuMRvLw2IeqdjMNaWrHMP628Yavlvah64dFhk=</Cipher>
                      <Digest algorithm="HMAC-SHA1">WXwRp7mJyVvdUFOfbdu6RbvcxuU=</Digest>
                    </Data>
                    <Expiry>2020-01-24T02:18:49.084Z</Expiry>
                  </Secret>
                </Device>
              </SecretContainer>
              <UTCTimestamp>1485224329</UTCTimestamp>
            </GetSharedSecretResponse>


 */

// @formatter:off
public class VipToken {
    public String status;
    public String salt64;            // \xbd"\xec\xaeU\x0f\x99\xd4\x08\xf1\x9f8\xf3\xa1h\x97t3I\xbf
    public String iteration_count;   // 50
    public int iteration_count_int;
    public String iv64;              // j\xa2\xa6\x8dh5[}\xaaQ\x02f\xef\x9c\xf1\xab
    public String expiry;            // 2020-01-21T03:00:09.283Z
    public String id;                // VSMT69744298
    public String cipher64;          // 8\x8fb\x9a~v<\x8e\xaa\xf7\x80\x15"\xf8o\xcc[\x1a\xa9dI\x9b\x8d3\xfd\x7fH\xbe\x9e\x00^\xcd
    public String digest64;          // \xbc\x8cj^f\xac\xc1\x13\x8d\x1dKZ\x18\xd9\xf7\x14\x01\xbcx}
    // Derived fields
    public byte[] salt;
    public byte[] iv;
    public byte[] cipher;
    public byte[] digest;
    // @formatter:on

    public VipToken() {
    }

    public VipToken parse(String xml) throws Exception {
        InputStream in = IOUtils.toInputStream(xml, "UTF-8");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(in);
        doc.getDocumentElement().normalize();
        status = get(doc, "StatusMessage");
        salt64 = get(doc, "PBESalt");
        iteration_count = get(doc, "PBEIterationCount");
        iv64 = get(doc, "IV");
        expiry = get(doc, "Expiry");
        id = get(doc, "Secret", "Id");
        cipher64 = get(doc, "Cipher");
        digest64 = get(doc, "Digest");
        salt = VipEnc.base64ToBytes(salt64);
        iv = VipEnc.base64ToBytes(iv64);
        cipher = VipEnc.base64ToBytes(cipher64);
        digest = VipEnc.base64ToBytes(digest64);
        iteration_count_int = Integer.parseInt(iteration_count);
        return this;
    }

    String get(Document doc, String id) {
        String result = "";
        NodeList nList = doc.getElementsByTagName(id);
        if (nList.getLength() > 0) {
            Node nNode = nList.item(0);
            result = nNode.getTextContent();
        }
        return result;
    }

    String get(Document doc, String id, String attr) {
        String result = "";
        NodeList nList = doc.getElementsByTagName(id);
        if (nList.getLength() > 0) {
            Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                result = eElement.getAttribute(attr);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
