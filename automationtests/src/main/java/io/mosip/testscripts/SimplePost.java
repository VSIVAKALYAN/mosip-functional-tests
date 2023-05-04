package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
/*import org.json.simple.JSONObject;*/
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;

public class SimplePost extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(SimplePost.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean sendEsignetToken = false;
	public boolean auditLogCheck = false;
	/**
	 * get current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		sendEsignetToken = context.getCurrentXmlTest().getLocalParameters().containsKey("sendEsignetToken");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	/**
	 * Test method for OTP Generation execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException
	 * @throws AdminTestException
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
		auditLogCheck = testCaseDTO.isAuditLogCheck();
		String[] templateFields = testCaseDTO.getTemplateFields();
		
		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		if(testCaseName.contains("CreateIdSchema")) {
			inputJson = modifyIdSchemaInputJson(inputJson);
		}

		
		if(inputJson.contains("&quot;")) {
			inputJson = inputJson.replace("&quot;", "\"");
		}
		
		if (testCaseDTO.getTemplateFields() != null && templateFields.length > 0) {
			ArrayList<JSONObject> inputtestCases = AdminTestUtil.getInputTestCase(testCaseDTO);
			ArrayList<JSONObject> outputtestcase = AdminTestUtil.getOutputTestCase(testCaseDTO);
			//adding...
			List<String> languageList = new ArrayList<>();
			//languageList =Arrays.asList(System.getProperty("env.langcode").split(","));
			languageList = BaseTestCase.languageList;
			 for (int i=0; i<languageList.size(); i++) {
		        	Innerloop:
		            for (int j=i; j <languageList.size();) {
		            	response = postWithBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(),
								getJsonFromTemplate(inputtestCases.get(i).toString(), testCaseDTO.getInputTemplate()), COOKIENAME,
								testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		            	
		            	Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
								response.asString(),
								getJsonFromTemplate(outputtestcase.get(j).toString(), testCaseDTO.getOutputTemplate()));
						Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
						
						if (!OutputValidationUtil.publishOutputResult(ouputValid))
							throw new AdminTestException("Failed at output validation");
		                    break Innerloop;
		            }
		        }
		}

		else {
			String tempUrl = ApplnURI.replace("-internal", "");
			if(testCaseName.contains("ESignet_")) {
				if(testCaseName.contains("ESignet_SendBindingOtp")) {
					response = postRequestWithCookieAuthHeader(tempUrl + testCaseDTO.getEndPoint(), inputJson, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
				}
				else {
//					String tempUrl = ApplnURI.replace("-internal", "");
					response = postRequestWithCookieAuthHeaderAndXsrfToken(tempUrl + testCaseDTO.getEndPoint(),
							inputJson, COOKIENAME,
							testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
					
	//				response = postWithBodyAndCookieForAutoGeneratedIdUrlEncoded(tempUrl + testCaseDTO.getEndPoint(), inputJson,
	//						COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), idKeyName);
				}
			}
			else {
				response = postWithBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(),
						inputJson, auditLogCheck, COOKIENAME, 
						testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), sendEsignetToken);
			}
			Map<String, List<OutputValidationDto>> ouputValid = null;
			if(testCaseName.contains("_StatusCode")) {
				
				OutputValidationDto customResponse = customStatusCodeResponse(String.valueOf(response.getStatusCode()), testCaseDTO.getOutput(), testCaseName);
				
				ouputValid = new HashMap<String, List<OutputValidationDto>>();
				ouputValid.put("expected vs actual", List.of(customResponse));
			}else {
				ouputValid = OutputValidationUtil.doJsonOutputValidation(
					response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
			}
			
			System.out.println(ouputValid);
			Reporter.log(ReportUtil.getOutputValiReport(ouputValid));

			if (!OutputValidationUtil.publishOutputResult(ouputValid))
				throw new AdminTestException("Failed at output validation");
		}

	}

	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
}
