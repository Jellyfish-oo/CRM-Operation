package com.asiainfo.easyops.proc.task;

import com.ai.appframe2.service.ServiceFactory;
import com.ai.comframe.vm.workflow.ivalues.IBOVmTaskValue;
import com.ai.comframe.vm.workflow.ivalues.IBOVmWFValue;
import com.ai.comframe.vm.workflow.service.interfaces.ITaskSV;
import com.ai.comframe.vm.workflow.service.interfaces.IVmWorkflowSV;
import com.ai.common.i18n.CrmLocaleFactory;
import com.ai.omframe.order.ivalues.IOrdCustExtValue;
import com.ai.omframe.order.ivalues.IOrdCustValue;
import com.ai.omframe.order.ivalues.IOrdOfferValue;
import com.ai.omframe.order.service.interfaces.IOrdCustExtSV;
import com.ai.omframe.util.SoServiceFactory;
import com.asiainfo.crm.so.exe.tf.rboss.utils.AbstractTfForExpire;
import com.asiainfo.crm.so.order.rboss.service.interfaces.IOrderQuerySV;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CR88WA extends AbstractTfForExpire {

	private static transient Log logger = LogFactory.getLog(CR88WA.class);

	@Override
	public String doTaskByAbs(long taskId) throws Exception {
	
		logger.info("CR88WA started");
		ITaskSV taskService = (ITaskSV) ServiceFactory.getService(ITaskSV.class);
		IVmWorkflowSV wfService = (IVmWorkflowSV) ServiceFactory.getService(IVmWorkflowSV.class);
		IBOVmWFValue wfBean;
		StringBuilder cond = new StringBuilder("");
		HashMap<String, String> param = new HashMap<String, String>();
		cond.append("label").append(" = :label ");
		cond.append(" and ").append("state").append(" = :state ");

		param.put("label", "manually send");
		param.put("state", "5");
		IBOVmTaskValue[] tasks = taskService.getVmTaskBean("RBOSS", cond.toString(), param, -1, -1);
		int[] c = { 0, 0, 0 };
		IBOVmTaskValue task;
		for (int i = 0; i < tasks.length; i++) {
			task = tasks[i];
			wfBean = wfService.getVmWorkflowBeanbyId(task.getWorkflowId());
			if (wfBean != null) {
				if ("LiveLinkContractUpAndSend".equals(wfBean.getLabel()) & wfBean.getState() == 2) {
					c = handleCreateTT(c, wfBean.getWorkflowObjectId(), wfBean.getWorkflowId(), task.getTaskId());
				}
			} else {
				continue;
			}
		}
		logger.info("CR88WA finished");
		return CrmLocaleFactory.getResource((String) "SOS99798037");
	}

	private int[] handleCreateTT(int[] c, String oId, String wfId, String taskId)
			throws NumberFormatException, Exception {
		int r = checkAgr(oId);
		if (r == 1) {
			addRemark(r, oId);
			jumpOrder(oId, wfId, taskId);
			c[0]++;
		} else if (r == 2) {
			c[1] = specialHandling(c[1], r, oId, wfId, taskId);
		} else if (r == 3) {
			int flag = isHardware(oId);
			if (flag == 1) {
				addRemark(r, oId);
				jumpOrder(oId, wfId, taskId);
				c[2]++;
			}
		}
		return c;
	}

	private int isHardware(String oId) {

		IOrderQuerySV sv = (IOrderQuerySV) ServiceFactory.getService(IOrderQuerySV.class);
		try {
			IOrdOfferValue[] orderOffers = sv.queryOrdOfferByCondition(new Long(oId).longValue(), -1,
					"OFFER_PLAN_RESOURCE", "Retail Hardware Virtual Main Offer", -1, -1);
			if (orderOffers.length > 0)
				return 1;
			else {
				for (int i = 0; i < orderOffers.length; i++) {
					if (orderOffers[i].getState() == 1) {
						return 1;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	private int specialHandling(int c, int r, String oId, String wfId, String taskId)
			throws NumberFormatException, Exception {
		IOrdCustValue orderCust = SoServiceFactory.getOrderQrySrv().getOrdCustByCustOrderId(Long.parseLong(oId));
		if (orderCust == null)
			throw new Exception("can't find customer's order :" + oId);
		long preId = orderCust.getPreCustomerOrderId();
		IOrdCustExtSV oceSV = SoServiceFactory.getOrderCustExtSV();
		IOrdCustExtValue[] orderCustExts = oceSV.getOrderCustExts(preId, "");
		int count = 0;
		for (int i = 0; i < orderCustExts.length; i++) {
			if (orderCustExts[i].getAttrCode().equals("VERIS_ORDER_COUNT")) {
				count = Integer.parseInt(orderCustExts[i].getAttrValue());
				if (count == 0) {
					addRemark(r, oId);
					jumpOrder(oId, wfId, taskId);
					c++;
				} else if (count > 0) {
					orderCustExts[i].setAttrValue("0");
					IOrdCustExtValue[] tt = new IOrdCustExtValue[] { orderCustExts[i] };
					oceSV.saveOrderCustExts(tt, "");
					c++;
				} else {
					logger.error("unexpected case for oId:" + oId);
				}
			}
		}
		return c;
	}

	private void jumpOrder(String oId, String wfId, String taskId) {

	}

	private void addRemark(int r, String oid) {
		// TODO Auto-generated method stub

	}

	public int checkAgr(String objID) {
		return 0;
	}

}
