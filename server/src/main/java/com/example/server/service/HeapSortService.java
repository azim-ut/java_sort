package com.example.server.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.server.bean.Line;

@Service
public class HeapSortService extends SortService {
    @Override
    protected List<Line> sortList(String target, List<Line> list) {
        this.buildHeap(target, list);
        for (int i = list.size() - 1; i >= 1; i--) {
            swap(target, list, 0, i);
            heapify(target, list, i, 0);
        }
        return list;
    }

    private void buildHeap(String target, List<Line> list) {
        int n = list.size();
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(target, list, n, i);
        }
    }

    private void heapify(String target, List<Line> list, int size, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < size && list.get(left).needToReplace(list.get(largest))) {
            largest = left;
        }
        if (right < size && list.get(right).needToReplace(list.get(largest))) {
            largest = right;
        }
        if (largest != i) {
            swap(target, list, i, largest);
            heapify(target, list, size, largest);
        }
    }

    private void swap(String target, List<Line> list, int a, int b) {
        Collections.swap(list, a, b);
        addLog(target, List.of(a, b), list);
    }
}
