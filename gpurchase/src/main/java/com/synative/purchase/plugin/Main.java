package com.synative.purchase.plugin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {



        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook(10) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();

                try {

                    findAndHookMethod("android.app.ContextImpl", classLoader, "bindService",
                            Intent.class,
                            ServiceConnection.class, int.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    Intent intent = (Intent) param.args[0];
                                    if(intent.getAction().equals("com.android.vending.billing.InAppBillingService.BIND")){
                                        param.setResult(false);
                                    }
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
