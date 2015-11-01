package org.shaolin.uimaster.app.api.remote;

import java.io.File;
import java.io.FileNotFoundException;

import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.api.HttpClientService;


import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * team api集合类
 * 
 * @author 
 * @version 创建时间：2015年1月14日 下午3:32:18
 * 
 */
public class TeamService {

    /**
     * 获取团队项目列表
     * 
     * @param teamId
     * @param handler
     */
    public static void getTeamProjectList(int teamId,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        HttpClientService.get("action/api/team_project_list", params, handler);
    }

    /**
     * 获取team动态列表
     * 
     * @param teamId
     * @param activeId
     * @param pageIndex
     * @param handler
     */
    public static void getTeamCommentList(int teamId, int activeId,
            int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("id", activeId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 20);
        HttpClientService.get("action/api/team_reply_list_by_activeid", params,
                handler);
    }

    /**
     * 获取某项目的任务列表
     * 
     * @param uId
     *            用户id
     * @param teamId
     *            团队id
     * @param projectId
     *            项目id（当<=0或不设置时，查询非项目的任务列表）
     * @param source
     *            "Git@OSC","GitHub"(只有设置了projectid值，这里才需要设置该值)
     */
    public static void getTeamCatalogIssueList(int uId, int teamId,
            int projectId, String source, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uId);
        params.put("teamid", teamId);
        params.put("projectid", projectId);
        params.put("source", source);
        HttpClientService.get("action/api/team_project_catalog_list", params,
                handler);
    }

    /**
     * 获取指定任务列表的任务列表
     * 
     * @param teamId
     * @param projectId
     *            项目id(-1获取非项目任务列表, 0获取所有任务列表)
     * @param catalogId
     *            任务列表的的目录id
     * @param source
     *            "Team@OSC"(default),"Git@OSC","GitHub",如果指定了projectid的值，
     *            这个值就是必须的
     * @param uid
     *            如果指定该值，则获取该id用户相关的任务
     * @param state
     *            "all"(default),"opened","closed","outdate"
     * @param scope
     *            "tome"(default,指派给我的任务),"meto"(我指派的任务)
     * @param pageIndex
     * @param pageSize
     * @param handler
     */
    public static void getTeamIssueList(int teamId, int projectId,
            int catalogId, String source, int uid, String state, String scope,
            int pageIndex, int pageSize, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("projectid", projectId);
        params.put("catalogid", catalogId);
        params.put("source", source);
        params.put("uid", uid);
        params.put("state", state);
        params.put("scope", scope);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        HttpClientService.get("action/api/team_issue_list", params, handler);
    }

    /***
     * 获取团队的讨论区列表
     * 
     * @param type
     * @param teamId
     * @param uid
     * @param pageIndex
     * @param handler
     */
    public static void getTeamDiscussList(String type, int teamId, int uid,
            int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("teamid", teamId);
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/team_discuss_list", params, handler);
    }

    /***
     * 获取讨论贴详情
     * 
     * @author  2015-2-2 下午6:19:54
     * 
     * @return void
     * @param teamId
     * @param discussId
     * @param handler
     */
    public static void getTeamDiscussDetail(int teamId, int discussId,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("discussid", discussId);
        HttpClientService.get("action/api/team_discuss_detail", params, handler);
    }

    /***
     * 发表讨论贴评论
     * 
     * @author  2015-2-3 下午2:42:54
     * 
     * @return void
     * @param uid
     * @param teamId
     * @param discussId
     * @param content
     * @param handler
     */
    public static void pubTeamDiscussReply(int uid, int teamId, int discussId,
            String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamid", teamId);
        params.put("discussid", discussId);
        params.put("content", content);
        HttpClientService.post("action/api/team_discuss_reply", params, handler);
    }

    /***
     * 发表一条综合评论 动态、分享内容、周报
     * 
     * @author  2015-3-6 下午3:31:07
     * 
     * @return void
     * @param teamId
     * @param type
     *            普通动态-110，分享内容-114， 周报-118
     * @param tweetId
     * @param content
     * @param handler
     */
    public static void pubTeamTweetReply(int teamId, int type, long tweetId,
            String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("type", type);
        params.put("teamid", teamId);
        params.put("tweetid", tweetId);
        params.put("content", content);
        HttpClientService.post("action/api/team_tweet_reply", params, handler);
    }

    /***
     * 获取团队任务详情
     * 
     * @author  2015-1-27 下午7:47:17
     * 
     */
    public static void getTeamIssueDetail(int teamId, int issueId,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("issueid", issueId);
        HttpClientService.get("action/api/team_issue_detail", params, handler);
    }

    /***
     * 获取团队的周报列表
     * 
     * @param uid
     * @param teamId
     * @param year
     * @param week
     * @param pageIndex
     * @param handler
     */
    public static void getTeamDiaryList(int uid, int teamId, int year,
            int week, int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamid", teamId);
        params.put("year", year);
        params.put("week", week);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        HttpClientService.get("action/api/team_diary_list", params, handler);
    }

    /***
     * 任务、周报、讨论的回复列表
     * 
     * @author  2015-2-2 上午11:39:04
     * 
     * @return void
     * @param teamId
     * @param id
     * @param type
     *            评论列表的类型（周报diary,讨论discuss,任务issue）
     * @param pageIndex
     * @param handler
     */
    public static void getTeamReplyList(int teamId, int id, String type,
            int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("id", id);
        params.put("type", type);
        params.put("pageIndex", pageIndex);
        HttpClientService
                .get("action/api/team_reply_list_by_type", params, handler);

    }

    /***
     * 发表一个新的团队动态
     * 
     * @author  2015-3-9 下午2:46:13
     * 
     * @return void
     * @param teamId
     * @param content
     * @param img
     * @param handler
     */
    public static void pubTeamNewActive(int teamId, String content, File img,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamid", teamId);
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("msg", content);
        params.put("appid", 3);
        if (img != null) {

            try {
                params.put("img", img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        HttpClientService.post("action/api/team_tweet_pub", params, handler);
    }

}
