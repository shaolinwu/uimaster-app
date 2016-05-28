/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.app.fragment;

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.base.WebViewlFragment;
import org.shaolin.uimaster.app.bean.CommentList;
import org.shaolin.uimaster.app.bean.FavoriteList;
import org.shaolin.uimaster.app.ui.empty.EmptyLayout;
import org.shaolin.uimaster.app.util.StringUtils;
import org.shaolin.uimaster.app.util.UIHelper;

/**
 * One function from the server data.
 */
public class FunctionFragment extends WebViewlFragment<String> {

    @Override
    public void initData() {
        Bundle argus = this.getArguments();
        if (argus != null && argus.get("dialog") != null) {
            //showing ajax dialog
            StringBuffer sb = new StringBuffer();
            sb.append("<!DOCTYPE html><html><head><title>");
            sb.append(argus.get("title"));
            sb.append("</title>\n");
            sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
            sb.append("<meta http-equiv=\"x-ua-compatible\" content=\"ie=7\" />\n");
            sb.append("<meta name=\"viewport\" id=\"WebViewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=0.5,maximum-scale=1.0,user-scalable=1\" />\n");
            sb.append("<meta name=\"apple-mobile-web-app-title\" content=\"UIMaster\">\n");
            sb.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n");
            sb.append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\">\n");
            sb.append("<meta name=\"format-detection\" content=\"telephone=no\">\n");
            sb.append("<link rel=\"stylesheet\" href=\"file:///android_asset/uimaster/css/jquery-dataTable.css\" type=\"text/css\">\n");
            sb.append("<link rel=\"stylesheet\" href=\"file:///android_asset/uimaster/css/jquery-jstree.css\" type=\"text/css\">\n");
            sb.append("<link rel=\"stylesheet\" href=\"file:///android_asset/uimaster/css/jquery-ui.css\" type=\"text/css\">\n");
            sb.append("<link rel=\"stylesheet\" href=\"file:///android_asset/uimaster/css/jquery-mobile.css\" type=\"text/css\">\n");
            sb.append("<link rel=\"stylesheet\" href=\"file:///android_asset/uimaster/css/iumaster-mob.css\" type=\"text/css\">\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/jquery.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/jquery-ui.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/jquery-mobile.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/jquery-dataTable.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/jquery-jstree.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/uimaster.js\"></script>\n");
            sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/uimaster/js/uimaster-widget.js\"></script>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<form action=\"#\" method=\"post\" name=\"everything\" onsubmit=\"return false;\" _framePrefix=\"\">\n");
            sb.append(argus.get("data"));
            sb.append("\n");
            sb.append("</form>\n");
            sb.append("<script type=\"text/javascript\">\n");
            sb.append("var defaultname = new Object();\n");
            sb.append("defaultname.initPageJs = function(){};\n");
            sb.append("var USER_CONSTRAINT_IMG=\"/images/uimaster_constraint.gif\";\n");
            sb.append("var USER_CONSTRAINT_LEFT=false;\n");
            sb.append("var CURRENCY_GROUP_SEPARATOR=\",\";\n");
            sb.append("var CURRENCY_MONETARY_SEPARATOR=\".\";\n");
            sb.append("var CURTIME=");
            sb.append(String.valueOf(System.currentTimeMillis()));
            sb.append(";\nvar TZOFFSET=");
            sb.append(String.valueOf(Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis())));
            sb.append(";\nvar WEB_CONTEXTPATH=\"/uimaster\";\n");
            sb.append("var RESOURCE_CONTEXTPATH=\"file:///android_asset/uimaster\";\n");
            sb.append("var FRAMEWRAP=\"/uimaster\";\n");
            sb.append("var IS_SERVLETMODE=true;\n");
            sb.append("var IS_MOBILEVIEW=true;\n");
            sb.append("var MOBILE_APP_TYEP=\"andriod\";\n");
            sb.append("var AJAX_SERVICE_URL=\"\";\n");
            sb.append("getElementList();\n");
            sb.append(argus.get("js"));
            sb.append("\ndefaultname.initPageJs();\n");
            sb.append("\n</script>\n</body>\n</html>");
            executeOnLoadDataSuccess0(sb.toString());
        } else if (argus != null && argus.get("FunctionId") != null) {
            RService.getFunctionDetail( argus.getString("_chunkname"), argus.getString("_nodename"),
                    argus.getString("_page"), argus.getString("_framename"), mDetailHeandler);
        } else {
            //RService.getFunctionDetail(mDetailHeandler);
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    }

    @Override
    protected String getCacheKey() {
        return "function_" + mId;
    }

    @Override
    protected void sendRequestDataForNet() {
    }

    @Override
    protected String parseData(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected String getWebViewBody(String detail) {
        return detail;
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null)
            UIHelper.showComment(getActivity(), mId,
                    CommentList.CATALOG_NEWS);
    }

    @Override
    protected String getShareTitle() {
        return "ShareTitle";
    }

    @Override
    protected String getShareContent() {
        return StringUtils.getSubString(0, 55,
                getFilterHtmlBody(mDetail));
    }

    @Override
    protected String getShareUrl() {
        return "";
    }

}
