package dev.tornaco.torscreenrec.control;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import dev.nick.library.ServiceProxy;

/**
 * Created by Nick on 2017/6/28 14:52
 */

public class FloatingControllerServiceProxy extends ServiceProxy implements FloatingController {

    private FloatingController controller;

    public FloatingControllerServiceProxy(Context context) {
        super(context, new Intent(context, FloatingControlService.class));
    }

    public void start(Context context) {
        context.startService(new Intent(context, FloatingControlService.class));
    }

    public void stop(Context context) {
        context.stopService(new Intent(context, FloatingControlService.class));
    }

    @Override
    public void show() {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                controller.show();
            }
        });
    }

    @Override
    public void hide() {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                controller.hide();
            }
        });
    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void onConnected(IBinder binder) {
        controller = (FloatingController) binder;
    }
}
