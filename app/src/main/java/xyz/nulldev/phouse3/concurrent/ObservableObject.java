package xyz.nulldev.phouse3.concurrent;

import java.util.ArrayList;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public class ObservableObject<T> extends AtomicReferenceWrapper<T> {

    ArrayList<OnChangeListener<T>> changeListeners = new ArrayList<>();
    ArrayList<OnChangeListener<T>> setListeners = new ArrayList<>();
    ArrayList<OnChangeListener<T>> removeListeners = new ArrayList<>();

    public void addChangeListener(OnChangeListener<T> changeListener) {
        changeListeners.add(changeListener);
    }

    public void addSetListener(OnChangeListener<T> setListener) {
        setListeners.add(setListener);
    }

    public void addRemoveListener(OnChangeListener<T> removeListener) {
        removeListeners.add(removeListener);
    }

    public ArrayList<OnChangeListener<T>> getChangeListeners() {
        return changeListeners;
    }

    public ArrayList<OnChangeListener<T>> getSetListeners() {
        return setListeners;
    }

    public ArrayList<OnChangeListener<T>> getRemoveListeners() {
        return removeListeners;
    }


    public void set(T object) {
        T superObj = get();
        if(superObj == null && object != null) {
            for (OnChangeListener<T> listener : setListeners) {
                if(!listener.onChange(object)) return;
            }
        } else if(superObj != null && object == null) {
            for(OnChangeListener<T> listener : removeListeners) {
                if(!listener.onChange(null)) return;
            }
        } else if(!xyz.nulldev.phouse3.util.Objects.equals(superObj, object)) {
            for (OnChangeListener<T> listener : changeListeners) {
                if(!listener.onChange(object)) return;
            }
        }
        super.set(object);
    }
}