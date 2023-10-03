package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityBlockingQueue<SensorData> dataBuffer;
    private final Lock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize, (x1, x2) -> x1.getMeasurementTime().compareTo(x2.getMeasurementTime()));
    }

    @Override
    public void process(SensorData data) {
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
        dataBuffer.add(data);
    }

    public void flush() {
        try {
            lock.lock();
            if (dataBuffer.isEmpty()) {
                return;
            }
            var list = new ArrayList<SensorData>();
            while (!dataBuffer.isEmpty()) {
                list.add(dataBuffer.poll());
            }
            writer.writeBufferedData(list);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}