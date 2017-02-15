package org.shaolin.uimaster.app.viewmodule.inter;

import org.shaolin.uimaster.app.base.BaseView;
import org.shaolin.uimaster.app.bean.MainModuleBean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */

public interface IMineView extends BaseView {
    public void initMineItem(List<MainModuleBean> datas);
}
