package com.example.server;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.server.bean.SortType;
import com.example.server.service.BubbleSortService;
import com.example.server.service.HeapSortService;
import com.example.server.service.InsertionSortService;
import com.example.server.service.QuickSortService;
import com.example.server.service.SelectionSortService;
import com.example.server.service.SortService;

@Component
public class SortServiceFactory {
    private final BubbleSortService bubbleSortService;
    private final QuickSortService quickSortService;
    private final InsertionSortService insertionSortService;
    private final HeapSortService heapSortService;
    private final SelectionSortService selectionSortService;

    private static final Map<SortType, SortService> handler = new HashMap<>();

    @Bean
    private void defineHandler() {
        handler.put(SortType.BUBBLE_SORT, bubbleSortService);
        handler.put(SortType.QUICK_SORT, quickSortService);
        handler.put(SortType.INSERTION_SORT, insertionSortService);
        handler.put(SortType.HEAP_SORT, heapSortService);
        handler.put(SortType.SELECTION_SORT, selectionSortService);
    }

    public SortServiceFactory(BubbleSortService bubbleSortService, QuickSortService quickSortService, InsertionSortService insertionSortService, HeapSortService heapSortService, SelectionSortService selectionSortService) {
        this.bubbleSortService = bubbleSortService;
        this.quickSortService = quickSortService;
        this.insertionSortService = insertionSortService;
        this.heapSortService = heapSortService;
        this.selectionSortService = selectionSortService;
    }

    public SortService get(SortType type) {
        return handler.get(type);
    }
}
