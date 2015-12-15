package xyz.nulldev.phouse3.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */

/**
 * Same thing as observable object except only executes one listener every change and pops it from a queue.
 */
public class QueuedBlockingObservableObject<T> extends AtomicReferenceWrapper<T> {

    ConcurrentLinkedQueue<OnChangeListener<T>> changeListeners = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<OnChangeListener<T>> setListeners = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<OnChangeListener<T>> removeListeners = new ConcurrentLinkedQueue<>();

    public void addChangeListener(OnChangeListener<T> changeListener) {
        changeListeners.add(changeListener);
    }

    public void addSetListener(OnChangeListener<T> setListener) {
        setListeners.add(setListener);
    }

    public boolean addSetListenerIfNotSet(OnChangeListener<T> setListener) {
        if(get() != null) {
            addSetListener(setListener);
            return true;
        } else {
            return false;
        }
    }

    public void addRemoveListener(OnChangeListener<T> removeListener) {
        removeListeners.add(removeListener);
    }

    public ConcurrentLinkedQueue<OnChangeListener<T>> getChangeListeners() {
        return changeListeners;
    }

    public ConcurrentLinkedQueue<OnChangeListener<T>> getSetListeners() {
        return setListeners;
    }

    public ConcurrentLinkedQueue<OnChangeListener<T>> getRemoveListeners() {
        return removeListeners;
    }

    public void set(T object) {
        T superObj = get();
        if(superObj == null && object != null && !setListeners.isEmpty()) {
            if(!setListeners.poll().onChange(object)) return;
        } else if(superObj != null && object == null && !changeListeners.isEmpty()) {
            if(!changeListeners.poll().onChange(null)) return;
        } else if(!xyz.nulldev.phouse3.util.Objects.equals(superObj, object) && !changeListeners.isEmpty()) {
            if(!changeListeners.poll().onChange(object)) return;
        }
        super.set(object);
    }
}
