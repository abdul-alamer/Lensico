package de.gerogerke.android.lensico.instagramapi.wrapper;

/**
 * Created by Deutron on 30.03.2016.
 */
public abstract class TaskCallback implements ITaskCallback {

    @Override
    public abstract void onSuccess(Object object);

    @Override
    public void onError() {

    }

    @Override
    public void onDone() {

    }
}
