package com.asiainfo.easyops.proc.task;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.ai.appframe2.service.ServiceFactory;
import com.asiainfo.appframe.ext.exeframe.task.query.ivalues.IBOCfgTaskValue;
import com.asiainfo.crm.so.common.base.service.interfaces.ICfgTaskSV;
import com.asiainfo.crm.so.exe.tf.rboss.utils.AbstractTfForExpire;

public class PythonTask extends AbstractTfForExpire {

	private static Log logger = LogFactory.getLog(PythonTask.class);
	@Override
	public String doTaskByAbs(long taskId) throws Exception {
		String pythonPath = System.getProperty("python.path");
		String scriptPath = System.getProperty("python.script_path");
		ICfgTaskSV cfgTaskService = (ICfgTaskSV) ServiceFactory.getService(ICfgTaskSV.class);
		IBOCfgTaskValue task = cfgTaskService.getCfgTaskById(taskId);
		String scriptFileName = task.getRemarks();
		logger.info("PythonTask started, Python_path=" + pythonPath + " and Script Path=" + scriptPath);
		logger.info("Script File Name is " + scriptFileName);
		String cmdLine = pythonPath+ "python.exe " + scriptPath + "\\" + scriptFileName + " " + taskId;
		logger.info("execute cmdline=" + cmdLine);
		Process pr = Runtime.getRuntime().exec(cmdLine);

        BufferedReader in = new BufferedReader(new
                InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
           logger.info(line);
        }

		pr.waitFor();
		logger.info("Python Script is executed, return=" + pr.exitValue());
		return "success";
	}
}
