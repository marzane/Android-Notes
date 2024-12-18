package com.marzane.bloc_de_notas.repository;

import java.util.ArrayList;

public interface ICRUD<T1, T2> {

    public abstract T2 listById(T1 id);
    public abstract ArrayList<T2> listAll();

    public abstract long insert(T2 modelo);
    public abstract int update(T2 modelo);
    public abstract int delete(T1 id);
    public abstract int deleteAll();
}
