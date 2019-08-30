 public void changeOrderUser(CommonParam commparam, OrderInfo orderInfo)
  {
    PrintDebugLog.print(LOG, new Object[] { "ChangeUnifiedPayBusiness changeOrderUser -- :", orderInfo });

    if (orderInfo == null) {
      ExceptionUtil.rethrow("140000", ErrorKey.PARAM_UNIFIED_PAY_INFO, new Object[] { "" });
    }

    if (orderInfo.getSoAcctId() <= 0L) {
      ExceptionUtil.rethrow("140000", ErrorKey.PARAM_ACCT_ID, new Object[] { Long.valueOf(orderInfo.getSoAcctId()) });
    }

    if ((orderInfo.getOrderId() == null) || (orderInfo.getOrderId().isEmpty())) {
      ExceptionUtil.rethrow("140000", ErrorKey.PARAM_ORDER_ID, new Object[] { orderInfo.getOrderId() });
    }

    if (orderInfo.getResourceId() <= 0L) {
      ExceptionUtil.rethrow("140000", ErrorKey.PARAM_RESOURCE_ID, new Object[] { Long.valueOf(orderInfo.getResourceId()) });
    }
