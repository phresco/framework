package com.photon.phresco.framework.param.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.photon.phresco.api.DynamicPageParameter;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.model.PerformanceDetails;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class LoadTestDetailsImpl implements DynamicPageParameter, Constants {

	@Override
	public Map<String, Object> getObjects(Map<String, Object> paramsMap) throws PhrescoException {
		Reader read = null;
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			ApplicationInfo applicationInfo = (ApplicationInfo) paramsMap.get(KEY_APP_INFO);
			String testAgainst = (String) paramsMap.get(KEY_TEST_AGAINST);
			String testResultName = (String) paramsMap.get(KEY_TEST_RESULT_NAME);
			String testResultJsonFile = testResultJsonFile(applicationInfo, testAgainst, testResultName);
			Gson gson = new Gson();
			File file = new File(testResultJsonFile);
			List<PerformanceDetails> performanceDetails = new ArrayList<PerformanceDetails>();
			PerformanceDetails performanceDetail = new PerformanceDetails();
			if (file.exists()) {
				read = new InputStreamReader(new FileInputStream(testResultJsonFile));
				performanceDetail = gson.fromJson(read, PerformanceDetails.class);
				if (performanceDetail != null) {
					performanceDetails.add(performanceDetail);
				}
			}
			resultMap.put("className", performanceDetail.getClass().getName());
			resultMap.put("valuesFromJson", performanceDetails);

			return resultMap;
		} catch(Exception e){
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}

	private String testResultJsonFile(ApplicationInfo appInfo, String testAgainst, String testResultName) throws PhrescoPomException {
		StringBuilder builder = new StringBuilder(Utility.getProjectHome());
		builder.append(appInfo.getAppDirName());
		PomProcessor processor = new PomProcessor(getPOMFile(appInfo));
		String performDir = processor.getProperty(POM_PROP_KEY_LOADTEST_DIR);
		builder.append(performDir);
		builder.append(File.separator);
		builder.append(testAgainst.toLowerCase());
		builder.append(File.separator);
		builder.append(FOLDER_JSON);
		builder.append(File.separator);
		builder.append(testResultName + DOT_JSON);

		return builder.toString();
	}

	private File getPOMFile(ApplicationInfo appInfo) {
		StringBuilder builder = new StringBuilder(Utility.getProjectHome())
		.append(appInfo.getAppDirName())
		.append(File.separatorChar)
		.append(Utility.getPomFileName(appInfo));
		return new File(builder.toString());
	}

}