package org.meowcat.edxposed.manager.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;

import org.meowcat.edxposed.manager.MeowCatApplication;
import org.meowcat.edxposed.manager.R;
import org.meowcat.edxposed.manager.XposedApp;
import org.meowcat.edxposed.manager.util.ModuleUtil;

import java.util.Collection;
import java.util.List;

import static org.meowcat.edxposed.manager.adapter.AppHelper.FORCE_WHITE_LIST_MODULE;

public class ApplicationListAdapter extends AppAdapter {

    private volatile boolean isWhiteListMode;
    private List<String> checkedList;

    public ApplicationListAdapter(Context context, boolean isWhiteListMode) {
        super(context, "Application");
        this.isWhiteListMode = isWhiteListMode;
    }

//    public void setWhiteListMode(boolean isWhiteListMode) {
//        this.isWhiteListMode = isWhiteListMode;
//    }

    @Override
    public List<String> generateCheckedList() {
        if (XposedApp.getPreferences().getBoolean("hook_modules", true)) {
            Collection<ModuleUtil.InstalledModule> installedModules = ModuleUtil.getInstance().getModules().values();
            for (ModuleUtil.InstalledModule info : installedModules) {
                FORCE_WHITE_LIST_MODULE.add(info.packageName);
            }
            Log.d(MeowCatApplication.TAG, "ApplicationList -> generateCheckedList: Force add modules to list");
        }
        AppHelper.makeSurePath();
        if (isWhiteListMode) {
            checkedList = AppHelper.getWhiteList();
        } else {
            checkedList = AppHelper.getBlackList();
        }
        Log.d(MeowCatApplication.TAG, "ApplicationList -> generateCheckedList: generate done");
        return checkedList;
    }

    @Override
    protected void onCheckedChange(CompoundButton view, boolean isChecked, ApplicationInfo info) {
        boolean success = isChecked ?
                AppHelper.addPackageName(isWhiteListMode, info.packageName) :
                AppHelper.removePackageName(isWhiteListMode, info.packageName);
        if (success) {
            if (isChecked) {
                checkedList.add(info.packageName);
            } else {
                checkedList.remove(info.packageName);
            }
        } else {
            Snackbar.make(view, R.string.add_package_failed, Snackbar.LENGTH_SHORT).show();
            view.setChecked(!isChecked);
        }
    }
}