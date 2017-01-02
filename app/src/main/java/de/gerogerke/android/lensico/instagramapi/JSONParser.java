package de.gerogerke.android.lensico.instagramapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.gerogerke.android.lensico.util.StringUtil;

public class JSONParser {

	public JSONObject getJSONFromUrlByGet(String weburl){
		JSONObject jsonObject = null;

		try {
			URL url = new URL(weburl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();

			InputStream is = conn.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);

			jsonObject = new JSONObject(responseStrBuilder.toString());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public int sendDelRequest(String weburl){
        URL url = null;
        try {
            url = new URL(weburl);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("DELETE");

            return httpURLConnection.getResponseCode();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return 404;
    }

    public int sendPostRequest(String weburl){
        StringUtil.log("Sending POST request: " + weburl);
        try {
            URL url = new URL(weburl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 404;
    }

}
