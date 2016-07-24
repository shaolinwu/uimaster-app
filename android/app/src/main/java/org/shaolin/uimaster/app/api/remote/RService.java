package org.shaolin.uimaster.app.api.remote;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.context.AppConfig;
import org.shaolin.uimaster.app.context.AppException;
import org.shaolin.uimaster.app.api.HttpClientService;
import org.shaolin.uimaster.app.bean.Report;
import org.shaolin.uimaster.app.util.StringUtils;
import org.shaolin.uimaster.app.util.TLog;

import java.io.File;
import java.io.FileNotFoundException;

public class RService {

    public static void getBottomFunctionList(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.bmdp.adminconsole.page.AjaxService.mobileBottomFunctionList");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    public static void getVerifyCode(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.bmdp.adminconsole.page.AjaxService.userPreLogin");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    /**
     * 登陆
     * 
     * @param username
     * @param password
     * @param handler
     */
    public static void login(String username, String password, String verifyCodeAnswer,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("pwd", password);
        params.put("verifyCode", verifyCodeAnswer);
        params.put("keep_login", 1);
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.bmdp.adminconsole.page.AjaxService.userLogin");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    public static void getFunctionList(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.vogerp.commonmodel.page.AjaxService.functionList");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    public static void getFunctionDetail(String chunkname, String nodename, String page,
                                         String framename, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_chunkname", chunkname);
        params.put("_nodename", nodename);
        params.put("_page", page);
        params.put("_framename", framename);
        params.put("_framePrefix", "");
        params.put("_r", (int)(Math.random()*10000));
        params.put("app_height", AppConfig.screenHeight);
//        params.put("_chunkname", "org.shaolin.vogerp.commonmodel.diagram.ModularityModel");
//        params.put("_nodename", "ProductManagement");
//        params.put("_page", "org.shaolin.vogerp.productmodel.page.ProductManagement");
//        params.put("_framename", "productManager");
//        params.put("_framePrefix", "");

        HttpClientService.post(AppConfig.FUNCTION_DETAILS_URL, "", params, handler);
    }

    public static void getActiveList(int uid, int catalog, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("catalog", catalog);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/active_list", params, handler);
    }

    public static void getFavoriteList(int uid, int type, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("type", type);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/favorite_list", params, handler);
    }

    public static void getUserInformation(int uid, int hisuid, String hisname,
            int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("hisuid", hisuid);
        params.put("hisname", hisname);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/user_information", params, handler);
    }

    public static void getMyInformation(int uid,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        HttpClientService.get("action/api/my_information", params, handler);
    }

    public static void getSearchList(String catalog, String content,
            int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("content", content);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/search_list", params, handler);
    }

    public static void updatePortrait(int uid, File portrait,
            AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("portrait", portrait);
        HttpClientService.post("action/api/portrait_update", params, handler);
    }

    public static void getNotifications(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.bmdp.workflow.page.AjaxService.getUserNotifications");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    /**
     * 清空通知消息
     * 
     * @param uid
     * @param type
     * @return
     * @throws AppException
     */
    public static void clearNotice(int uid, int type,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.vogerp.commonmodel.page.AjaxService.functionList");
        params.put("_r", (int)(Math.random()*10000));
        HttpClientService.post("", params, handler);
    }

    public static void checkUpdate(AsyncHttpResponseHandler handler) {
        HttpClientService.get("MobileAppVersion.xml", handler);
    }

    /**
     * 举报
     * 
     * @param report
     * @param handler
     */
    public static void report(Report report, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("obj_id", report.getReportId());
        params.put("url", report.getLinkAddress());
        params.put("obj_type", report.getReason());
        if (report.getOtherReason() != null
                && !StringUtils.isEmpty(report.getOtherReason())) {
            params.put("memo", report.getOtherReason());
        } else {
            params.put("memo", "其他原因");
        }
        TLog.log("Test", report.getReportId() + "" + report.getLinkAddress()
                + report.getReason() + report.getOtherReason());
        HttpClientService.post("action/communityManage/report", params, handler);
    }

    /**
     * 摇一摇，随机数据
     * 
     * @param handler
     */
    public static void shake(AsyncHttpResponseHandler handler) {
        shake(-1, handler);
    }

    /**
     * 摇一摇指定请求类型
     */
    public static void shake(int type, AsyncHttpResponseHandler handler) {
        String inter = "action/api/rock_rock";
        if (type > 0) {
            inter = (inter + "/?type=" + type);
        }
        HttpClientService.get(inter, handler);
    }

    private static void uploadLog(String data, String report,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "1");
        params.put("report", report);
        params.put("msg", data);
        HttpClientService.post("action/api/user_report_to_admin", params, handler);
    }

    /**
     * BUG上报
     * 
     * @param data
     * @param handler
     */
    public static void uploadLog(String data, AsyncHttpResponseHandler handler) {
        uploadLog(data, "1", handler);
    }

}
