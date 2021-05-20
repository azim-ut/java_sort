package com.example.server.scoket.bean;

import com.example.server.bean.SortType;

public class IncomeMessage {
    private SortType sortType;
    private boolean play;
    private boolean stop;

    public SortType getSortType() {
        return sortType;
    }

    public boolean isPlay() {
        return play;
    }

    public boolean isStop() {
        return stop;
    }
}
