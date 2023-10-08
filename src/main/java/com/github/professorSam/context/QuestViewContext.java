package com.github.professorSam.context;

import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;

public record QuestViewContext(Quest quest, Player player) {}