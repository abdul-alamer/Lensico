package de.gerogerke.android.lensico.instagramapi.wrapper;

import java.io.Serializable;

/**
 * Created by Deutron on 30.03.2016.
 */
public interface ITaskUpdateCallback extends Serializable {
    void onSuccess(Object object);

    void onDone();
    void onUpdate(Object object);
    void onError();
}
