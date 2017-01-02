package de.gerogerke.android.lensico.instagramapi.wrapper;

/**
 * Created by Deutron on 30.03.2016.
 */
public abstract class TaskUpdateCallback implements ITaskUpdateCallback {

    @Override
    public abstract void onSuccess(Object object);

    @Override
    public void onUpdate(Object object) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onDone() {

    }
}
