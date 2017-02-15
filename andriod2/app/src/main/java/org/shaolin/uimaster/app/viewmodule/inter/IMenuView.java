package org.shaolin.uimaster.app.viewmodule.inter;

import org.shaolin.uimaster.app.base.BaseView;
import org.shaolin.uimaster.app.bean.MenuItem;

import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */

public interface IMenuView extends BaseView {
    public void showMenuList(List<MenuItem> items);
}
