package com.github.professorSam.db.model;

import java.util.UUID;

public record Player(UUID id, String name, String nationality, Group group){}
