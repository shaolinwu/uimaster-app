package org.shaolin.uimaster.app.data;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public final class URLData {

    public static final String BASE_URL = "https://www.vogerp.com/uimaster/";
    public static final String RESOURCE_URL = "https://www.vogerp-res.com:8082/uimaster/";
    public static final String RESOURCE_URL_1 = "https://www.vogerp-res.com:8082/uimaster";
    public static final String CHAT_URL = "https://www.vogerp.com:8090";
    public static final String CHAT_SEND_AUDIO_URL = "https://www.vogerp.com:8090/audio/add";
    public static final String CHAT_GET_AUDIO_URL = "https://www.vogerp.com:8090/audio/get";

    public static final String HOST = "www.vogerp.com";//10.0.2.2
    public static final String Origin = "https://www.vogerp.com";//10.0.2.2:8080
    public static final String FUNCTION_DETAILS_URL = Origin + "/uimaster/webflow.do?_appclient=andriod";
    public static final String AJAX_SERVICE_URL = Origin + "/uimaster/ajaxservice?_appclient=andriod";
    public static final String UPLOAD_URL = Origin + "/uimaster/uploadFile";

    //获取服务器资源文件
    public static final String GET_DOWNLOAD_RESOURCES = RESOURCE_URL + "download/";
    public static final String GET_RESOURCES_README = RESOURCE_URL + "download/readme.json";
    //获取四个tab栏的url
    public static final String GET_TAB_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.vogerp.commonmodel.page.AjaxService.mobileBottomFunctionList&_appclient=andriod";
    //点击每个tab访问web url
    public static final String MODULE_WEB_URL = BASE_URL + "https://www.vogerp.com/uimaster/webflow.do?";
    //获取验证码
    public static final String GET_VERIFICATION_CODE = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.bmdp.adminconsole.page.AjaxService.userPreLogin&_appclient=andriod";
    //注册用户url
    public static final String REGISTER_URL = BASE_URL + "webflow.do?_timestamp=97&_chunkname=org.shaolin.bmdp.adminconsole.diagram.LoginAuthentication&_nodename=Registration&_appclient=andriod";
    //登录url
    public static final String LOGIN_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.bmdp.adminconsole.page.AjaxService.userLogin&_appclient=andriod";
    //找回密码url
    public static final String FINDPWD_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.bmdp.adminconsole.page.AjaxService.findPwd&_appclient=andriod";
    //获取menuitems
    public static final String MENU_ITEMS_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.vogerp.commonmodel.page.AjaxService.functionList&_appclient=andriod";
    //获取我的items
    public static final String MINE_ITEMS_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.vogerp.commonmodel.page.AjaxService.userPageItems&_appclient=andriod";

    //登出
    public static final String LOGIN_OUT_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.bmdp.adminconsole.page.AjaxService.userLogout&_appclient=andriod";

    //
    public static final String AD_URL = BASE_URL + "ajaxservice?_ajaxUserEvent=webservice&_serviceName=org.shaolin.vogerp.campaign.page.AjaxService.getAppLuanchingAdv&_appclient=andriod";

}
