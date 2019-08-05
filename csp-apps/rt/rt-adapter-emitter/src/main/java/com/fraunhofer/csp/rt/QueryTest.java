package com.fraunhofer.csp.rt;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class QueryTest {

	private Logger LOG = new Logger();

	public static void main_1(String[] args) {
		new QueryTest().doit();
	}

	private void doit() {
		String RT_REMOTE_USER = "RT_REMOTE_USER";
		String username = "root";
		String password = "password";

		try {
			/*
			 * curl -i -d
			 * "user=root&pass=password&query='CF.{RT_UUID}'='EFEFA6E2-D366-11E7-921C-B379DE2FD4B8'&Queue=Incidents&fields=CF.{RT_UUID}"
			 * http://rt.example.com/REST/1.0/search/ticket
			 */
			String strUrl = "http://rt.example.com/REST/1.0/search/ticket";
			LOG.debug("working with url: " + strUrl);

			HttpPost mPost = new HttpPost(strUrl);
			mPost.addHeader(RT_REMOTE_USER, username);
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("user", username));
			params.add(new BasicNameValuePair("pass", password));
			params.add(new BasicNameValuePair("query",
					"'CF.{RT_UUID}'='EFEFA6E2-D366-11E7-921C-B379DE2FD4B8'&Queue=Incidents&format=l&fields=id"));

			CloseableHttpClient cl = HttpClients.createDefault();
			String strBody = null;
			mPost.setEntity(new UrlEncodedFormEntity(params));
			CloseableHttpResponse r = cl.execute(mPost);
			LOG.debug("got response code: " + r.getStatusLine().getStatusCode());
			if (200 != r.getStatusLine().getStatusCode()) {
				LOG.warn("unexpected response status code from server: " + r.getStatusLine().getStatusCode());
				return;
			}
			HttpEntity respEntity = r.getEntity();
			strBody = EntityUtils.toString(respEntity);
			LOG.debug("got response: {}" + strBody);

			if (null == strBody || "".equals(strBody.trim())) {
				LOG.debug("got no response from rt");
				return;
			}
			/*
			 * BufferedReader br = new BufferedReader(new StringReader(strBody)); String
			 * strLine = null; boolean bFound = false; boolean bHashNext = false; String
			 * strPattern = "fsck.com-rt"; while (null != (strLine = br.readLine().trim()))
			 * { if (0 == strLine.trim().length()) continue; if
			 * (strLine.startsWith("Members:")) { LOG.debug("index of pattern: {}" +
			 * strLine.indexOf(strPattern, 0)); String strTmp =
			 * strLine.substring(strLine.indexOf(strPattern, 0)); bFound = true; bHashNext =
			 * strLine.endsWith(","); strTmp = strTmp.substring(0, strTmp.length() - 1); if
			 * (null == rv) rv = new Links(); LOG.debug("found another link: {}", strTmp);
			 * rv.addLink(strTmp); continue; } if (bFound && bHashNext) { bHashNext =
			 * strLine.endsWith(","); String strTmp = strLine; if (bHashNext) strTmp =
			 * strLine.substring(0, strLine.length() - 1).trim();
			 * LOG.debug("found another link: {}", strTmp); rv.addLink(strTmp); continue; }
			 * }
			 */
			LOG.debug("get_links DONE");

			// return endPoint.path(TICKET_PATH + ticketid + "/links").queryParam("user",
			// this.username)
			// .queryParam("pass", this.password).request(MediaType.TEXT_PLAIN_TYPE)
			// .header("RT_REMOTE_USER", this.username).get(new GenericType<Links>() {
			// });
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}
	}

	private class Logger {
		public void debug(final String msg) {
			System.out.println("[DEBUG]: " + msg);
		}

		public void error(final String msg) {
			System.out.println("[ERROR]: " + msg);
		}

		@SuppressWarnings("unused")
		public void info(final String msg) {
			System.out.println("[INFO]: " + msg);
		}

		public void warn(final String msg) {
			System.out.println("[WARN]: " + msg);
		}
	}

}
