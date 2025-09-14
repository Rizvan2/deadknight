package org.example.deadknight.gameplay.actors.player.dialog;

public class DialogueScripts {

    // диалоги для игрока при старте игры
    public static DialogueLine[] START_GAME() {
        return new DialogueLine[]{
            new DialogueLine("Где я?..", 2.5),
            new DialogueLine("Похоже это земли гоблинов", 3.0),
            new DialogueLine("И судя по всему они мне не рады", 3.0),
            new DialogueLine("Я больше предпочитаю короткий меч", 3.0)

        };
    }

    // диалоги при убийстве гоблина
    public static DialogueLine[] FIRST_KILL() {
        return new DialogueLine[]{
            new DialogueLine("Первое поражение гоблина!", 2.0),
            new DialogueLine("Теперь можно двигаться дальше.", 2.5)
        };
    }

    // можно добавить другие сценарии
}
