package xyz.nulldev.phouse3.concurrent;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public interface OnChangeListener<T> {
    boolean onChange(T obj);
}
