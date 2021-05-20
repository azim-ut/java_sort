package com.example.server.bean;

import java.util.Date;
import java.util.List;

public class SortLog extends Line {
    private Long tm;
    private List<Integer> index;
    private List<Line> list;

    public SortLog(List<Line> list, List<Integer> indexes) {
        this.tm = new Date().getTime();
        this.list = list;
        this.index = indexes;
    }
}
