package xyz.nulldev.phouse3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */

/**
 * Wrapper around atomic reference to allow overriding get and set
 * @param <T>
 */
public class AtomicReferenceWrapper<T> {
    AtomicReference<T> reference = new AtomicReference<>();

    public T get() {
        return reference.get();
    }

    public void set(T newValue) {
        reference.set(newValue);
    }

    public void lazySet(T newValue) {
        reference.lazySet(newValue);
    }

    public boolean compareAndSet(T expect, T update) {
        return reference.compareAndSet(expect, update);
    }

    public boolean weakCompareAndSet(T expect, T update) {
        return reference.weakCompareAndSet(expect, update);
    }

    public T getAndSet(T newValue) {
        return reference.getAndSet(newValue);
    }

    public boolean isEmpty() {
        return reference.get() == null;
    }

    public AtomicReference<T> getReference() {
        return reference;
    }
}
