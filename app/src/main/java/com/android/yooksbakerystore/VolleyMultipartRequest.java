package com.android.yooksbakerystore;

import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final String boundary = "volleyMultipartBoundary";
    private final String lineEnd = "\r\n";
    private Response.Listener<NetworkResponse> mListener;
    private Response.ErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    private Map<String, DataPart> mByteData;
    private Map<String, String> mParams;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add string params
            if (mParams != null && !mParams.isEmpty()) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    buildTextPart(dos, entry.getValue(), entry.getKey());
                }
            }

            // Add byte data
            if (mByteData != null && !mByteData.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                    buildDataPart(dos, entry.getValue(), entry.getKey());
                }
            }

            // End of multipart/form-data
            dos.writeBytes("--" + boundary + "--" + lineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String value, String inputName) throws IOException {
        dataOutputStream.writeBytes("--" + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + inputName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(value + lineEnd);
    }

    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataPart, String inputName) throws IOException {
        dataOutputStream.writeBytes("--" + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd);
        if (dataPart.getType() != null && !dataPart.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataPart.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        dataOutputStream.write(dataPart.getContent());
        dataOutputStream.writeBytes(lineEnd);
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    // Metode getByteData untuk menambahkan data byte ke permintaan multipart
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return mByteData;
    }

    // Metode getParams untuk menambahkan data teks ke permintaan multipart
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    // Metode setParams untuk mengatur data teks
    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    public static class DataPart {
        private String fileName;
        private File file;
        private String type;

        public DataPart(String fileName, File file) {
            this.fileName = fileName;
            this.file = file;
        }

        public DataPart(String fileName, File file, String type) {
            this.fileName = fileName;
            this.file = file;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FileInputStream inputStream = new FileInputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return outputStream.toByteArray();
        }

        public String getType() {
            return type;
        }
    }
}
