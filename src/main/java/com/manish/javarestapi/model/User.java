package com.manish.javarestapi.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private UUID id;
    private String name;
    private LocalDateTime birthDate;
}

