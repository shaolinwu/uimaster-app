package org.shaolin.uimaster.app.viewmodule.inter;

import org.shaolin.uimaster.app.base.BaseView;
import org.shaolin.uimaster.app.bean.MainModuleBean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */

public interface IMainModuleView extends BaseView {
    void initMainModuleView(List<MainModuleBean> moduleBeans);
}
