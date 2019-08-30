package com.asiainfo.easyops.proc.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.common.i18n.CrmLocaleFactory;
import com.asiainfo.crm.so.exe.tf.rboss.utils.AbstractTfForExpire;

public class Greeting extends AbstractTfForExpire {

	private static transient Log logger = LogFactory.getLog(Greeting.class);
	@Override
	public String doTaskByAbs(long taskId) throws Exception {
		logger.info("I am working !!!" + taskId);
		return CrmLocaleFactory.getResource((String) "SOS99798037");
	}

}
