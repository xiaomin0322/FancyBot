package com.github.nesz.fancybot.objects.pagination;

public class Page<E>
{

    private final int maxPage;
    private int currentPage;
    private E extraData;

    public Page(final int currentPage, final int maxPage)
    {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
    }

    public Page(final int currentPage, final int maxPage, final E extra)
    {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.extraData = extra;
    }

    public E getExtra()
    {
        return extraData;
    }

    public boolean previousPage()
    {
        if (currentPage > 1)
        {
            currentPage--;
            return true;
        }
        return false;
    }

    public boolean nextPage()
    {
        if (currentPage < maxPage)
        {
            currentPage++;
            return true;
        }
        return false;
    }

    public int getMaxPage()
    {
        return maxPage;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(final int currentPage)
    {
        this.currentPage = currentPage;
    }

}