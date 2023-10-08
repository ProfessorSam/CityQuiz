package com.github.professorSam.context;

import com.github.professorSam.db.model.Answer;
import com.github.professorSam.db.model.Group;
import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;

import java.util.HashMap;
import java.util.List;

public record AdminPanelContext(
        int usercount,
        int groupcount,
        int questioncount,
        int groupsDone,
        HashMap<Quest, List<Answer>> answers,
        HashMap<Group, List<Player>> groupsAndPlayers
) {}
