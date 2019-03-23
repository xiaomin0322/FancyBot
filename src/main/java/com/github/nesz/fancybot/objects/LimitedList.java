package com.github.nesz.fancybot.objects;

import java.util.ArrayList;

public class LimitedList<T> extends ArrayList<T>
{

    private final int capacity;

    public LimitedList(final int capacity)
    {
        this.capacity = capacity;
    }

    public void push(final T o)
    {
        if (size() >= capacity)
        {
            remove(0);
        }

        add(o);
    }

    public T pull()
    {
        return remove(0);
    }

    public T pullLast()
    {
        return remove(size() - 1);
    }

    public T getLast()
    {
        return get(size() - 1);
    }

    @Override
    public boolean add(final T o)
    {
        if (size() >= capacity)
        {
            remove(size() - 1);
        }

        return super.add(o);
    }

    @Override
    public T remove(final int index)
    {
        final T obj = super.remove(index);
        int id = 0;

        for (final T dat : this)
        {
            this.set(id, dat);
            id++;
        }

        return obj;
    }

    @Override
    public void add(final int index, final T element)
    {
        throw new UnsupportedOperationException("Elements may not be added to a fixed size List, use set() instead.");
    }

    @Override
    public boolean remove(final Object o)
    {
        throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    }

    @Override
    protected void removeRange(final int fromIndex, final int toIndex)
    {
        throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    }
}