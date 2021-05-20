package com.example.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.example.server.bean.Line;
import com.example.server.bean.SortLog;
import com.example.server.exception.StopSortingException;


abstract public class SortService {
    private static final int LIST_CAPACITY = 100;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, BlockingDeque<SortLog>> sortLog = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Value("${wait.time}")
    private int WAIT_TIME = 100;
    private Future<?> task;

    private final static List<Line> template;
    static {
        Random random = new Random();
        template = new LinkedList<>();
        for (int i = 0; i < LIST_CAPACITY; i++) {
            template.add(new Line(random.nextInt(LIST_CAPACITY)));
        }
    }

    protected void addLog(String target, int i, List<Line> list) {
        addLog(target, List.of(i), list);
    }

    protected void addLog(String target, List<Integer> indexes, List<Line> list) {
        try {
            sortLog.get(target).addLast(new SortLog(list, indexes));
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            logger.error("Sort processing is interrupted. {}", e.getMessage());
        } catch (NullPointerException npe) {
            logger.error("Sort processing is stopped. {}", npe.getMessage());
            task.cancel(true);
            throw new StopSortingException();
        }
    }

    public SortLog pollLog(String target) {
        return sortLog.get(target).poll();
    }

    public void sort(String target) {
        if (!sortLog.containsKey(target)) {
            List<Line> list = generateList();
            sortLog.put(target, new LinkedBlockingDeque<>());
            task = executor.submit(() -> {
                sortList(target, list);
                salute(target, list);
            });
        }
    }

    private void salute(String target, List<Line> list) {
        List<Integer> indexes = new ArrayList<>();
        list.forEach(row -> {
            int ind = list.indexOf(row);
            indexes.add(ind);
            addLog(target, indexes, list);
        });
    }

    private List<Line> generateList() {
        return new LinkedList<>(template);
    }

    abstract protected List<Line> sortList(String target, List<Line> list);

    public void drop(String target) {
        sortLog.remove(target);
    }
}
