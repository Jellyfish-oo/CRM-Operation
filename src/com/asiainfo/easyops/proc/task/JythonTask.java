package com.asiainfo.easyops.proc.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.python.core.PyFunction;
import org.python.core.PyLong;
import org.python.util.PythonInterpreter;

import com.ai.appframe2.service.ServiceFactory;
import com.asiainfo.appframe.ext.exeframe.task.query.ivalues.IBOCfgTaskValue;
import com.asiainfo.crm.so.common.base.service.interfaces.ICfgTaskSV;
import com.asiainfo.crm.so.exe.tf.rboss.utils.AbstractTfForExpire;

public class JythonTask extends AbstractTfForExpire {

	private static Log logger = LogFactory.getLog(JythonTask.class);
	@Override
	public String doTaskByAbs(long taskId) throws Exception {
		String pythonPath = System.getProperty("python.path");
		String scriptPath = System.getProperty("python.script_path");
		ICfgTaskSV cfgTaskService = (ICfgTaskSV) ServiceFactory.getService(ICfgTaskSV.class);
		IBOCfgTaskValue task = cfgTaskService.getCfgTaskById(taskId);
		String scriptFileName = task.getRemarks();
		logger.info("PythonTask started, Python_path=" + pythonPath + " and Script Path=" + scriptPath);
		logger.info("Script File Name is " + scriptFileName);
		PythonInterpreter pi = new PythonInterpreter();
		pi.execfile(scriptPath+ "\\" + scriptFileName);
		PyFunction pf = pi.get("greeting", PyFunction.class);
		pf.__call__(new PyLong(taskId));
		pi.close();
		return "success";
	}
}
