package com.marsiox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Redirection {

	private String domain;
	
	public String getRedirectionUrl(String urlString) {
		setDomain(urlString);
		
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);

			System.out.println("Url: " + urlString);
			System.out.println("Status: " + conn.getResponseCode());

			InputStream is = conn.getInputStream();

			String locationHeader = conn.getHeaderField("Location");
			
			System.out.println("Location: " + locationHeader);
			System.out.println("Set-Cookie: "
					+ conn.getHeaderField("Set-Cookie"));
			System.out.println("--");
			String redirectionUrl = locationHeader;
			if (redirectionUrl == null) {
				redirectionUrl = getUrlFromStream(is);
			}

			if (redirectionUrl.length() > 0) {
				/****
				 * In case url is not absolute
				 */
				Pattern pat = Pattern.compile("https?://");
				Matcher m = pat.matcher(redirectionUrl);
				if(!m.find()) {
					redirectionUrl = this.domain + redirectionUrl;
				}
				getRedirectionUrl(redirectionUrl);
			}

		} catch (MalformedURLException e) {
			System.out.println("[ERROR] Can't follow url");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return urlString;
	}

	public String getUrlFromStream(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = reader.readLine();
		Pattern htmlPat = Pattern.compile("http-equiv=\"refresh\"");
		Pattern httpPat = Pattern.compile("(https?://[^\"]+)");
		Pattern jsPat = Pattern.compile("window\\.location");
		String url = new String("");
		
		while ((line = reader.readLine()) != null) {
			Matcher m1 = htmlPat.matcher(line);
			Matcher m2 = jsPat.matcher(line);
			Matcher m3 = httpPat.matcher(line);
			if (m1.find() || m2.find()) {
				if (m3.find()) {
					System.out.println("Html redirection");
					url = m3.group(0);
					break;
				}
			}
		}

		inputStream.close();

		return url;
	}
	
	public void setDomain(String url) {
		Pattern pat = Pattern.compile("(^https?://[^/]+)");
		Matcher m = pat.matcher(url);
		if (m.find()) {
			this.domain = m.group(0);
		}
	}
}