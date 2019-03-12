package com.github.nesz.fancybot.objects.pagination;

import net.dv8tion.jda.api.entities.TextChannel;

public class Page<E> {

    private final int maxPage;
    private final TextChannel textChannel;
    private int currentPage;
    private E extraData;

    public Page(int currentPage, int maxPage, TextChannel textChannel) {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.textChannel = textChannel;
    }

    public Page(int currentPage, int maxPage, TextChannel textChannel, E extra) {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.textChannel = textChannel;
        this.extraData = extra;
    }

    public void setExtraData(E data) {
        extraData = data;
    }

    public E getExtra() {
        return extraData;
    }

    public boolean previousPage() {
        if (currentPage > 1) {
            currentPage--;
            return true;
        }
        return false;
    }

    public boolean nextPage() {
        if (currentPage < maxPage) {
            currentPage++;
            return true;
        }
        return false;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }
}