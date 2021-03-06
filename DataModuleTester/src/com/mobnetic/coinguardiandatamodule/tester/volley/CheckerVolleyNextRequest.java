package com.mobnetic.coinguardiandatamodule.tester.volley;

import com.android.volley.toolbox.RequestFuture;
import com.mobnetic.coinguardian.model.CheckerInfo;

public class CheckerVolleyNextRequest extends GenericCheckerVolleyRequest<String> {
	
	public CheckerVolleyNextRequest(String url, CheckerInfo checkerInfo, RequestFuture<String> future) {
		super(url, checkerInfo, future, future);
	}

	@Override
	protected String parseNetworkResponse(String responseString) throws Exception {
		return responseString;
	}
}
