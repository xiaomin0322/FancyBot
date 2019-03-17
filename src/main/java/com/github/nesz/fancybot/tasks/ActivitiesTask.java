package com.github.nesz.fancybot.tasks;

import com.github.nesz.fancybot.FancyBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActivitiesTask implements Runnable {

    private static final List<Pair<Activity, OnlineStatus>> ACTIVITIES = new ArrayList<Pair<Activity, OnlineStatus>>() {{
       add(new ImmutablePair<>(Activity.of(Activity.ActivityType.WATCHING, "_ * *"), OnlineStatus.DO_NOT_DISTURB));
       add(new ImmutablePair<>(Activity.of(Activity.ActivityType.WATCHING, "* _ *"), OnlineStatus.IDLE));
       add(new ImmutablePair<>(Activity.of(Activity.ActivityType.WATCHING, "* * _"), OnlineStatus.ONLINE));
    }};

    public ActivitiesTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
    }

    private int now = 0;

    @Override
    public void run() {
        if (now >= ACTIVITIES.size()) {
            now = 0;
        }
        Pair<Activity, OnlineStatus> pair = ACTIVITIES.get(now);
        FancyBot.getShardManager().setGame(pair.getLeft());
        FancyBot.getShardManager().setStatus(pair.getRight());
        now ++;
    }
}