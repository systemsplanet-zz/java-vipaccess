package com.systemsplanet.vipaccess;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Hashtable;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.lochbridge.oath.otp.keyprovisioning.OTPAuthURI;
import com.lochbridge.oath.otp.keyprovisioning.OTPAuthURIBuilder;
import com.lochbridge.oath.otp.keyprovisioning.OTPKey;
import com.lochbridge.oath.otp.keyprovisioning.OTPKey.OTPType;
import com.lochbridge.oath.otp.keyprovisioning.qrcode.QRCodeWriter;
import com.lochbridge.oath.otp.keyprovisioning.qrcode.QRCodeWriter.ErrorCorrectionLevel;
import com.systemsplanet.vipaccess.http.VipToken;
import com.systemsplanet.vipaccess.log.Log;

public class VipQrCode {
    static Log LOG = new Log();

    // qrcode.QRCode(version=1,error_correction=qrcode.constants.ERROR_CORRECT_L,box_size=10,border=4)
    public String writeQRCode(VipToken token, byte[] otp_secret) throws Exception {
        Path pathOfQRCodeImage= Files.createTempFile("qrcode", ".png", new FileAttribute[] {});

        String otp_uri = getOneTimePasswordURI(token.id, otp_secret);
        String secret32 = VipEnc.bytesToBase32(otp_secret);
        LOG.info("secret32:["+ secret32+"]")
        String label = String.format("%s:%s", u(VipConst.APP), token.id);
        OTPAuthURI uri = OTPAuthURIBuilder.fromKey(new OTPKey(secret32, OTPType.TOTP)).label(label).issuer(VipConst.DIST_CHANNEL)
                    .digits(VipConst.DIGITS).timeStep(VipConst.TIME_STEP_MS).build();
        LOG.debug("URI1: " + uri.toPlainTextUriString());
        LOG.info(otp_uri);
        LOG.info("QR Code image file: " + pathOfQRCodeImage.toFile().getAbsolutePath());
        QRCodeWriter.fromURI(uri)
        .width(300)
        .height(300)
        .errorCorrectionLevel(ErrorCorrectionLevel.H)
        .margin(4)
        .imageFormatName("PNG")
        .write(pathOfQRCodeImage);
        String qrText = getQRCodeImageRawText(pathOfQRCodeImage);
        LOG.debug("From Image: " + qrText);
        return qrText;
    }

    private String getQRCodeImageRawText(Path path) throws Exception {
        Map<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            BufferedImage bi = ImageIO.read(fis);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bi)));
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            return result.getText();
        }
    }

    // otpauth://totp/VIP%20Access:VSMT69744298?issuer=Symantec&secret=SZLPRVBVPAZ2LD3HHCGYRAU5ZOBDNTRB
    String getOneTimePasswordURI(String id, byte[] secret) throws Exception {
        return String
                    .format("otpauth://%s/%s:%s?issuer=%s&secret=%s&", u(VipConst.OTP_TYPE), u(VipConst.APP), u(id), u(VipConst.DIST_CHANNEL), VipEnc
                                .bytesToBase32(secret));
    }

    // shorthand for URL Encode
    String u(String s) throws Exception {
        return OTPAuthURI.encodeLabel(s);
    }

}
