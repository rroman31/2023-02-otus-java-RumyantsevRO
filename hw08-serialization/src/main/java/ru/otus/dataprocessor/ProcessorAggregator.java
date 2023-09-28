package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        return data.stream()
                .collect(
                        Collectors.groupingBy(Measurement::getName, TreeMap::new,
                        Collectors.summingDouble(Measurement::getValue))
                );
    }
}