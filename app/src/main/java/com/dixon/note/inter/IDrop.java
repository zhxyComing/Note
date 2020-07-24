package com.dixon.note.inter;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.22
 * Functional desc: 展开收起
 */
public interface IDrop {

    void drop();

    void dropOpen();

    void dropClose();

    boolean isDropOpen();

    boolean isDropClose();

    boolean isOpening();

    boolean isClosing();
}
