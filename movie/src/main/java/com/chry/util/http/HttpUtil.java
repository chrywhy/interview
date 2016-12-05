package com.chry.util.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chry.interview.movie.loader.LatLngReadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class HttpUtil {
	static Logger logger = LogManager.getLogger(LatLngReadTask.class.getName());
	static HttpUtil.Proxy proxy = null;

    private static class TrustAllStrategy implements TrustStrategy {
        @Override
        public boolean isTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
            return true;
        }
    }

    static public class Proxy {
        public String host;
        public int port;
        public String user;
        public String passwd;

        public Proxy(String host, int port, String user, String passwd) {
            this.host = host;
            this.port = port;
            this.user = user;
            this.passwd = passwd;
        }
    }

    public static void loadProxy() {
        Properties props = new Properties();
        try {
        	String filepath=System.getProperty("user.dir");
            props.load(new FileInputStream(filepath + File.separator + "proxy.properties"));
            String proxyHost = props.getProperty("host", "");
            int proxyPort = Integer.parseInt(props.getProperty("port", "0"));
            String proxyUser = props.getProperty("user", "");
            String proxyPass = props.getProperty("password", "");
            if (!proxyHost.isEmpty()) {
            	proxy = new HttpUtil.Proxy(proxyHost, proxyPort, proxyUser, proxyPass);
            	logger.info("######################### PROXY: " + proxyHost + " : " + proxyPort);
            }
        } catch (Exception e) {
        	logger.warn("can not read proxy");
        }
    }
    
    public static String accessUrl(String url) {
        return accessUrl(url, false, proxy, 20000, 50000);
    }

    public static String accessUrl(String url, String proxyAddr, int port) {
        HttpUtil.Proxy proxy = new HttpUtil.Proxy(proxyAddr, port, "", "");
        return accessUrl(url, false, proxy, 20000, 50000);
    }
    
    public static String accessUrl(String url, boolean handleRedirect, Proxy proxy, int connTimeout, int readTimeout) {
        String response = null;
        try {
	        HttpParams params = new SyncBasicHttpParams();
	        ClientParamBean bean = new ClientParamBean(params);
	        bean.setHandleRedirects(handleRedirect);
	        DefaultHttpClient httpclient = new DefaultHttpClient(params);
	        HttpConnectionParams.setSoTimeout(httpclient.getParams(), connTimeout);
	        HttpConnectionParams.setSoTimeout(httpclient.getParams(), readTimeout);
	        if (proxy != null) {
	            httpclient.getCredentialsProvider().setCredentials(
	                    new AuthScope(proxy.host, proxy.port),
	                    new UsernamePasswordCredentials(proxy.user, proxy.passwd));
	            HttpHost proxyHost = new HttpHost(proxy.host, proxy.port);
	            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
	        }
	        try {
	            TrustAllStrategy trustAll;
	            trustAll = new TrustAllStrategy();
	            SSLSocketFactory socketFactory = new SSLSocketFactory(trustAll, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	            Scheme sch = new Scheme("https", 443, socketFactory);
	            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
	
	            HttpGet httpget = new HttpGet(url);
	            httpget.getRequestLine();
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            response = httpclient.execute(httpget, responseHandler);
	        } finally {
	            httpclient.getConnectionManager().shutdown();
	        }
	        return response;
        } catch (Exception e) {
        	throw new HttpAccessFailException(e);
        }
    }
}
