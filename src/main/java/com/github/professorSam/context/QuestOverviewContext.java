package com.github.professorSam.context;

import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;

import java.util.List;

public record QuestOverviewContext(Player player, List<Quest> questList) {

}
