package com.coderstory.purify.module;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.TextView;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import java.io.File;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.purify.utils.ConfigPreferences.getInstance;


public class FlymeHome extends XposedHelper implements IModule {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.meizu.flyme.launcher")) {

            Class clazz = findClass("com.meizu.flyme.launcher.u", lpparam.classLoader);
            if (clazz == null) {
                // 7.x
                clazz = findClass("com.meizu.flyme.launcher.v", lpparam.classLoader);
            }
            // 开启自定义布局
            if (getInstance().getBoolean("hide_icon_5", false)) {
                hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (param.args[0].getClass().equals(String.class)) {
                            param.args[3] = 5.0f;
                            param.args[4] = 5.0f;
                        }
                    }
                });
                // 不同布局使用不同的db
                hookAllConstructors(SQLiteOpenHelper.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam hookParam) {
                        if ("launcher.db".equals(hookParam.args[1])) {
                            Object arg = hookParam.args[0];
                            if (arg != null) {
                                String dbName = "launcher_coderStory.db";
                                XposedHelpers.setObjectField(hookParam.thisObject, "mName", dbName);

                                File file = ((Context) arg).getDatabasePath("launcher.db");
                                if (file != null && (file.exists())) {
                                    File databasePath = ((Context) arg).getDatabasePath(dbName);
                                    if (databasePath != null && (databasePath.exists())) {
                                        return;
                                    }
                                    writeFile(file, databasePath);
                                }
                            }
                        }
                    }
                });
            }

            // 隐藏图标标签
            if (getInstance().getBoolean("hide_icon_label", false)) {
                hookAllMethods(findClass("com.meizu.flyme.launcher.ShortcutIcon", lpparam.classLoader), "a", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        TextView textView = (TextView) XposedHelpers.getObjectField(param.thisObject, "c");
                        if (textView != null) {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }
}
