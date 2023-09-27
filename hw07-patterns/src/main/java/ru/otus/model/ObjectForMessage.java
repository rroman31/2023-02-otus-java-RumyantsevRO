package ru.otus.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ObjectForMessage {
    private List<String> data;

    public static ObjectForMessage cloneObjectForMessage(ObjectForMessage objectForMessage)
    {
        ObjectForMessage clone = new ObjectForMessage();

        if (objectForMessage.getData() != null)
        {
            clone.setData(ImmutableList.copyOf(objectForMessage.getData()));
        }

        return clone;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
