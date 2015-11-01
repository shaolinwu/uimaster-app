package org.shaolin.uimaster.app.api.remote;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.context.AppException;
import org.shaolin.uimaster.app.api.HttpClientService;
import org.shaolin.uimaster.app.bean.Report;
import org.shaolin.uimaster.app.util.StringUtils;
import org.shaolin.uimaster.app.util.TLog;

import java.io.File;
import java.io.FileNotFoundException;

public class RService {

    /**
     * 登陆
     * 
     * @param username
     * @param password
     * @param handler
     */
    public static void login(String username, String password,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("pwd", password);
        params.put("keep_login", 1);
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.bmdp.adminconsole.page.AjaxService.userLogin");

        HttpClientService.post("", params, handler);
    }

    public static void openIdLogin(String s) {

    }

    public static void getFunctionList(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("_ajaxUserEvent", org.shaolin.uimaster.page.AjaxProcessor.EVENT_WEBSERVICE);
        params.put("_serviceName", "org.shaolin.vogerp.commonmodel.page.AjaxService.functionList");

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
//        params.put("_chunkname", "org.shaolin.vogerp.commonmodel.diagram.ModularityModel");
//        params.put("_nodename", "ProductManagement");
//        params.put("_page", "org.shaolin.vogerp.productmodel.page.ProductManagement");
//        params.put("_framename", "productManager");
//        params.put("_framePrefix", "");

        HttpClientService.post(HttpClientService.FUNCTION_DETAILS_URL, "", params, handler);
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

    public static void getFriendList(int uid, int relation, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("relation", relation);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/friends_list", params, handler);
    }

    /**
     * 获取用户收藏
     * 
     * @param uid
     *            指定用户UID
     * @param type
     *            收藏类型: 0:全部收藏　1:软件　2:话题　3:博客　4:新闻　5:代码
     * @param page
     * @param handler
     */
    public static void getFavoriteList(int uid, int type, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("type", type);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/favorite_list", params, handler);
    }

    /**
     * 获取评论列表
     * 
     * @PARAM ID
     * @PARAM CATALOG
     *            1新闻 2帖子 3动弹 4动态
     * @PARAM PAGE
     * @PARAM HANDLER
     */
    public static void getCommentList(int id, int catalog, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        params.put("clientType", "android");
        HttpClientService.get("action/api/comment_list", params, handler);
    }

    public static void getBlogCommentList(int id, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/blogcomment_list", params, handler);
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

    public static void updateRelation(int uid, int hisuid, int newrelation,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("hisuid", hisuid);
        params.put("newrelation", newrelation);
        HttpClientService.post("action/api/user_updaterelation", params, handler);
    }

    public static void getMyInformation(int uid,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        HttpClientService.get("action/api/my_information", params, handler);
    }

    public static void getPostDetail(int id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("id", id);
        HttpClientService.get("action/api/post_detail", params, handler);
    }

    /**
     * 用户针对某个新闻，帖子，动弹，消息发表评论的接口，参数使用POST方式提交
     * 
     * @param catalog
     *            　　 1新闻　　2 帖子　　３　动弹　　４消息中心
     * @param id
     *            被评论的某条新闻，帖子，动弹或者某条消息的id
     * @param uid
     *            当天登陆用户的UID
     * @param content
     *            发表的评论内容
     * @param isPostToMyZone
     *            是否转发到我的空间，０不转发　　１转发到我的空间（注意该功能之对某条动弹进行评论是有效，其他情况下服务器借口可以忽略该参数）
     * @param handler
     */
    public static void publicComment(int catalog, int id, int uid,
            String content, int isPostToMyZone, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("uid", uid);
        params.put("content", content);
        params.put("isPostToMyZone", isPostToMyZone);
        HttpClientService.post("action/api/comment_pub", params, handler);
    }

    public static void replyComment(int id, int catalog, int replyid,
            int authorid, int uid, String content,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("uid", uid);
        params.put("content", content);
        params.put("replyid", replyid);
        params.put("authorid", authorid);
        HttpClientService.post("action/api/comment_reply", params, handler);
    }

    public static void deleteComment(int id, int catalog, int replyid,
            int authorid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("catalog", catalog);
        params.put("replyid", replyid);
        params.put("authorid", authorid);
        HttpClientService.post("action/api/comment_delete", params, handler);
    }

    public static void deleteBlogComment(int uid, int blogid, int replyid,
            int authorid, int owneruid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("blogid", blogid);
        params.put("replyid", replyid);
        params.put("authorid", authorid);
        params.put("owneruid", owneruid);
        HttpClientService.post("action/api/blogcomment_delete", params, handler);
    }

    /**
     * 用户添加收藏
     * 
     * @param uid
     *            用户UID
     * @param objid
     *            比如是新闻ID 或者问答ID 或者动弹ID
     * @param type
     *            1:软件 2:话题 3:博客 4:新闻 5:代码
     */
    public static void addFavorite(int uid, int objid, int type,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("objid", objid);
        params.put("type", type);
        HttpClientService.post("action/api/favorite_add", params, handler);
    }

    public static void delFavorite(int uid, int objid, int type,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("objid", objid);
        params.put("type", type);
        HttpClientService.post("action/api/favorite_delete", params, handler);
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

    public static void publicMessage(int uid, int receiver, String content,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("receiver", receiver);
        params.put("content", content);
        HttpClientService.post("action/api/message_pub", params, handler);
    }

    public static void deleteMessage(int uid, int friendid,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("friendid", friendid);
        HttpClientService.post("action/api/message_delete", params, handler);
    }

    public static void forwardMessage(int uid, String receiverName,
            String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("receiverName", receiverName);
        params.put("content", content);
        HttpClientService.post("action/api/message_pub", params, handler);
    }

    public static void getMessageList(int uid, int pageIndex,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/message_list", params, handler);
    }

    public static void updatePortrait(int uid, File portrait,
            AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("portrait", portrait);
        HttpClientService.post("action/api/portrait_update", params, handler);
    }

    public static void getNotices(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        HttpClientService.get("action/api/user_notice", params, handler);
    }

    /**
     * 清空通知消息
     * 
     * @param uid
     * @param type
     *            1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
     * @return
     * @throws AppException
     */
    public static void clearNotice(int uid, int type,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("type", type);
        HttpClientService.post("action/api/notice_clear", params, handler);
    }

    public static void singnIn(String url, AsyncHttpResponseHandler handler) {
        HttpClientService.getDirect(url, handler);
    }

    /**
     * 获取软件的动态列表
     * 
     * @param softid
     * @param handler
     */
    public static void getSoftTweetList(int softid, int page,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("project", softid);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/software_tweet_list", params, handler);
    }

    public static void checkUpdate(AsyncHttpResponseHandler handler) {
        HttpClientService.get("MobileAppVersion.xml", handler);
    }

    /**
     * 查找用户
     * 
     * @param username
     * @param handler
     */
    public static void findUser(String username,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("name", username);
        HttpClientService.get("action/api/find_user", params, handler);
    }

    /**
     * 获取活动列表
     * 
     * @param pageIndex
     * @param uid
     *            <= 0 近期活动 实际的用户ID 则获取用户参与的活动列表，需要已登陆的用户
     * @param handler
     */
    public static void getEventList(int pageIndex, int uid,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("pageIndex", pageIndex);
        params.put("uid", uid);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/event_list", params, handler);
    }

    /**
     * 获取某活动已出席的人员列表
     * 
     * @param eventId
     * @param pageIndex
     * @param handler
     */
    public static void getEventApplies(int eventId, int pageIndex,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("pageIndex", pageIndex);
        params.put("event_id", eventId);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/event_attend_user", params, handler);
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

    /**
     * 反馈意见
     * 
     * @param data
     * @param handler
     */
    public static void feedback(String data, AsyncHttpResponseHandler handler) {
        uploadLog(data, "2", handler);
    }

    /**
     * 周报评论（以后可改为全局评论）
     * 
     * @param uid
     * @param teamid
     * @param diaryId
     * @param content
     * @param handler
     */
    public static void sendComment(int uid, int teamid, int diaryId,
            String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamid", teamid);
        params.put("type", "118");
        params.put("tweetid", diaryId);
        params.put("content", content);
        HttpClientService.post("action/api/team_tweet_reply", params, handler);
    }

    /***
     * 客户端扫描二维码登陆
     * 
     * @author  2015-3-13 上午11:45:47
     * 
     * @return void
     * @param url
     * @param handler
     */
    public static void scanQrCodeLogin(String url,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String uuid = url.substring(url.lastIndexOf("=") + 1);
        params.put("uuid", uuid);
        HttpClientService.getDirect(url, handler);
    }

    /***
     * 使用第三方登陆
     * @param catalog 类别
     * @param openIdInfo 第三方的info
     * @param handler handler
     */
    public static void open_login(String catalog, String openIdInfo, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);
        HttpClientService.post("action/api/openid_login", params, handler);
    }

    /***
     * 第三方登陆账号绑定
     * @param catalog 类别（QQ、wechat）
     * @param openIdInfo 第三方info
     * @param userName 用户名
     * @param pwd 密码
     * @param handler handler
     */
    public static void bind_openid(String catalog, String openIdInfo, String userName, String pwd, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);
        params.put("username", userName);
        params.put("pwd", pwd);
        HttpClientService.post("action/api/openid_bind", params, handler);
    }

    /***
     * 使用第三方账号注册
     * @param catalog 类别（qq、wechat）
     * @param openIdInfo 第三方info
     * @param handler handler
     */
    public static void openid_reg(String catalog, String openIdInfo, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);
        HttpClientService.post("action/api/openid_reg", params, handler);
    }

}
