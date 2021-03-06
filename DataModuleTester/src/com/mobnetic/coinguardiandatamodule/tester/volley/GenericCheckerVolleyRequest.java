package com.mobnetic.coinguardiandatamodule.tester.volley;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.protocol.HTTP;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mobnetic.coinguardian.model.CheckerInfo;

public abstract class GenericCheckerVolleyRequest<T> extends Request<T> {
	
	protected final CheckerInfo checkerInfo;
	private final Listener<T> listener;
	private final Map<String, String> headers;

	public GenericCheckerVolleyRequest(String url, CheckerInfo checkerInfo, Listener<T> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		
		this.checkerInfo = checkerInfo;
		this.listener = listener;
		this.headers = new HashMap<String, String>();
		
		this.headers.put("Accept-Encoding", "gzip");
		this.headers.put(HTTP.USER_AGENT, "Bitcoin Checker (gzip)");
	}

	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers!=null ? headers : super.getHeaders();
    }
	
	@Override
	protected void deliverResponse(final T response) {
		listener.onResponse(response);
	}
	
	protected abstract T parseNetworkResponse(String responseString) throws Exception;
	
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String responseString = "";
			final String encoding = response.headers.get(HTTP.CONTENT_ENCODING);
            if(encoding!=null && encoding.contains("gzip")) {
                responseString = decodeGZip(response.data);
            } else {
                responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            }
			return Response.success(parseNetworkResponse(responseString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (CheckerErrorParsedError checkerErrorParsedError) {
			return Response.error(checkerErrorParsedError);	
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}
	
	private String decodeGZip(byte[] data) throws Exception {
        String responseString = "";

        ByteArrayInputStream bais = null;
        GZIPInputStream gzis = null;
        InputStreamReader reader = null;
        BufferedReader in = null;

        try {
            bais = new ByteArrayInputStream(data);
            gzis = new GZIPInputStream(bais);
            reader = new InputStreamReader(gzis);
            in = new BufferedReader(reader);

            String readed;
            while ((readed = in.readLine()) != null) {
                responseString += readed;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if(bais!=null)
                    bais.close();
                if(gzis!=null)
                    gzis.close();
                if(reader!=null)
                    reader.close();
                if(in!=null)
                    in.close();
            } catch (Exception e) {};
        }

        return responseString;
    }

}
