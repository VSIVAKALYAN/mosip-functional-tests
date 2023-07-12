package io.mosip.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.pojo.Root;


public class MockSMTPListener{
	private static Logger logger = Logger.getLogger(MockSMTPListener.class);
	
	 
	public static Map<Object, Object> emailNotificationMapS = Collections.synchronizedMap(new HashMap<Object, Object>()); 
	public static Boolean bTerminate = false;

	public void run() {
		try {
			  Properties kernelprops=ConfigManager.propsKernel;
				String a1="wss://smtp.";
				String externalurl=kernelprops.getProperty("keycloak-external-url");
			    String a2=	externalurl.substring(externalurl.indexOf(".")+1);
			    String a3="/mocksmtp/websocket"; 
				  
			WebSocket ws = HttpClient
					.newHttpClient()
					.newWebSocketBuilder()
					.buildAsync(URI.create(a1+a2+a3), new WebSocketClient())
					.join();
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}

	}

	private static class WebSocketClient implements WebSocket.Listener {
		Long count=(long) 00;
		Root root =new Root();
		public WebSocketClient() {  

		}

		@Override
		public void onOpen(WebSocket webSocket) {
			logger.info("onOpen using subprotocol " + webSocket.getSubprotocol());
			WebSocket.Listener.super.onOpen(webSocket);
		}


		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			return Listener.super.onClose(webSocket, statusCode, reason);
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			if(bTerminate) {
				logger.info(emailNotificationMapS);
				logger.info("End Closure of listner " );
				onClose(webSocket, 0, "After suite invoked closing");
			}
			try {       
				ObjectMapper om = new ObjectMapper();

				root = om.readValue(data.toString(), Root.class);
				if (!parseOtp(root.html).isEmpty() || !parseAdditionalReqId(root.html).isEmpty()) {
					emailNotificationMapS.put(root.to.value.get(0).address,root.html);
					logger.info(" After adding to emailNotificationMap key = " + root.to.value.get(0).address
							+ " data " + data + " root " + root );
				}

				else {
					logger.info(" Skip adding to emailNotificationMap key = " + root.to.value.get(0).address
							+ " data " + data + " root " + root );
				}
			} catch (JsonProcessingException | JSONException e) {

				logger.error(e.getStackTrace());
			}

			return WebSocket.Listener.super.onText(webSocket, data, last);
		}
	
		

		@Override
		public void onError(WebSocket webSocket, Throwable error) {

			logger.info("Bad day! " + webSocket.toString());
			logger.error(error.getStackTrace());
			WebSocket.Listener.super.onError(webSocket, error);
		}
	}
	
	public static String getOtp(String emailId){
		int otpExpTime = AdminTestUtil.getOtpExpTimeFromActuator();
		int otpCheckLoopCount = (otpExpTime * 1000)/AdminTestUtil.OTP_CHECK_INTERVAL;
		
		int counter = 0;
		
		String otp = "";
		if(ConfigManager.getUsePreConfiguredOtp().equalsIgnoreCase(GlobalConstants.TRUE_STRING))
		{
			return ConfigManager.getPreConfiguredOtp();
		}
		while (counter < otpCheckLoopCount) {
			if(emailNotificationMapS.get(emailId)!=null) {
				String html=(String) emailNotificationMapS.get(emailId);
				emailNotificationMapS.remove(emailId);	
				otp = parseOtp(html);
				if (otp != null && otp.length()>0) {
					logger.info("Found the OTP = " + otp);
					return otp;
				}
				else {
					logger.info("html Message = " + html + " Email = " + emailId);
				}
				
			}
			logger.info("*******Checking the email for OTP...*******");
			counter++;
			try {
				logger.info("Not received Otp yet, waiting for 10 Sec");
				Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
			} catch (InterruptedException e) {
				logger.info(e.getMessage());
				Thread.currentThread().interrupt();
			}
			
		}
		logger.info("OTP not found even after " + otpCheckLoopCount + " retries");
		return otp;
	}
	
	public static String parseOtp(String message){
		
		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
		String otp = "";
		if(message!=null) {
		    Matcher mMatcher = mPattern.matcher(message);
		    if(mMatcher.find()) {
		        otp = mMatcher.group(0);
		        otp = otp.trim();
		        logger.info("Extracted OTP: "+ otp+ " message : "+ message);
		    }else {
		    	logger.info("Failed to extract the OTP!! "+ "message : " + message);
		    	
		    }
		}
		return otp;
	}
	
	public static String parseAdditionalReqId(String message){
		
		return StringUtils.substringBetween(message, "AdditionalInfoRequestId", "-BIOMETRIC_CORRECTION-1");
		
	}
	
}