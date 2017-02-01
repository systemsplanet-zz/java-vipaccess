package com.systemsplanet.vipaccess.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import com.systemsplanet.vipaccess.log.Log;

public class VipHttp {
    static Log LOG = new Log();


    public static String post(String host, String path, String contentType, String data) throws Exception {
        StringBuffer response = new StringBuffer();
        BufferedReader in = null;
        String urlString = "https://" + host + "/" + path;
        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Host", host);
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate");
            con.setRequestProperty("User-Agent", "python-requests/2.12.5");
            con.setRequestProperty("Content-Length", "" + data.length());
            con.setRequestProperty("Content-type", contentType);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            LOG.debug("Post response: " + responseCode);
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                LOG.debug("   " + inputLine);
                response.append(inputLine);
            }
        }
        catch (Exception e) {
            LOG.error("unable to post url:[" + urlString + "] data:[" + data + "]", e);
        }
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e) {
            }
        }
        String result = response.toString();
        return result;
    }

}
