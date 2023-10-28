@import com.github.professorSam.Main
@import com.github.professorSam.context.AdminPanelContext
@import com.github.professorSam.db.model.Answer
@import com.github.professorSam.db.model.Group
@import com.github.professorSam.db.model.Player
@import com.github.professorSam.quest.AnswerQuest
@import com.github.professorSam.quest.MultipleChoiceQuest
@import com.github.professorSam.quest.PictureQuest
@import com.github.professorSam.quest.Quest
@param AdminPanelContext context
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>City Quiz</title>
    <meta name="description" content="A quiz for a city">
</head>
<body>
@template.header()
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: rgba(233, 209, 236, 0.85);
    }
    .dashboard {
        width: 80%;
        padding: 20px;
        margin: 0 auto 60px;
    }

    .top-box {
        background-color: #6349af;
        padding: 20px;
        border-radius: 10px;
        display: flex;
        justify-content: space-between;
    }

    .middle-box {
        background-color: #f0f0f0;
        border-radius: 10px;
        padding: 20px;
        margin-top: 20px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .group {
        background-color: #ccd3ff;
        border-radius: 10px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .group h3 {
        color: #333;
    }

    .user-list {
        list-style-type: none;
        padding: 0;
    }

    .user-list li {
        margin-bottom: 5px;
    }

    .metric {
        color: #fff;
        text-align: center;
        flex: 1;
        padding: 10px;
        background-color: #563f9b;
        border-radius: 5px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .bottom-box {
        margin-top: 20px;
    }

    .quest {
        background-color: #fff;
        border-radius: 10px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .quest h3 h4 {
        color: #333;
    }

    .group-answers {
        margin-top: 5px;
    }
    .group-answers h4 {
        margin-bottom: 3px;
    }

    .group-answers img {
        width: 140px;
    }

    #timesetting {
        margin-bottom: 5px;
    }

    .download-button {
        background-color: #6B5CE0;
        color: #fff;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        font-size: 18px;
        cursor: pointer;
        text-decoration: none;
    }

    button {
        background-color: #6B5CE0;
        color: #fff;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        font-size: 18px;
        cursor: pointer;
    }

</style>
<main>
    <div class="dashboard">
        <div class="top-box">
            <div class="metric">
                <h2>Anzahl der Spieler</h2>
                <p>${context.usercount()}</p>
            </div>
            <div class="metric">
                <h2>Anzahl der Gruppen</h2>
                <p>${context.groupcount()}</p>
            </div>
            <div class="metric">
                <h2>Fragen</h2>
                <p>${context.questioncount()}</p>
            </div>
            <div class="metric">
                <h2>Fertige Gruppen</h2>
                <p>${context.groupsDone()}</p>
            </div>
        </div>

        <div class="middle-box">
            <h2>Einstellungen</h2>
            <form id="timesetting" action="/adminsettime?token=${Main.getInstance().getAdminToken()}" method="post">
                <label for="endtime"><b>Zeit festlegen:</b></label><br>
                <input type="time" id="endtime" name="endtime" required><br>
                <i>Es kann bis zu 1 Minute dauern bis Änderungen übernommen werden!</i><br>
                <button type="submit">Update</button>
            </form>
        </div>

        <div class="middle-box">
            <h2>Fotos</h2>
            @if(Main.getInstance().getAdminToken() == null)
                <a class="download-button" href="/downloadAllPhotos">
                    Alle Fotos Herunterladen
                </a>
            @else
                <a class="download-button" href="/downloadAllPhotos?token=${Main.getInstance().getAdminToken()}">
                    Alle Fotos Herunterladen
                </a>
            @endif
        </div>

        <div class="middle-box">
            <h2>Gruppen und Spieler</h2>
            @for(Group group : context.groupsAndPlayers().keySet())
                <div class="group">
                    <h3>${group.name()}</h3>
                    <hr>
                    <i>${"Aktuelle Frage: " + group.quest()}</i>
                    <ul class="user-list">
                        @for(Player player : context.groupsAndPlayers().get(group))
                            @if(player.nationality() == Player.Nationality.GERMAN)
                                <li><b>DE: </b>${player.name()}</li>
                            @else
                                <li><b>FR: </b>${player.name()}</li>
                            @endif
                        @endfor
                    </ul>
                </div>
            @endfor
        </div>

        <!-- Bottom Box: Quests -->
        <div class="bottom-box">
            <h2>Fragen</h2>
            @for(Quest quest : Main.getInstance().getQuests())
                <div class="quest">
                    <h3>${quest.getTitleDE()}</h3>
                    <h4>Frage:</h4>
                    <p>${quest.getDescriptionDE()}</p>
                    <h4>Art der Frage:</h4>
                    <p>
                        @if(quest instanceof AnswerQuest)
                            Einfache Antwort
                        @elseif(quest instanceof MultipleChoiceQuest)
                            Multiple Choice
                        @elseif(quest instanceof PictureQuest)
                            Bild
                        @endif
                    </p>
                    @if(quest instanceof AnswerQuest answerQuest)
                        <h4>Richtige Antwort:</h4> <p>${answerQuest.getRightAnswer()}</p>
                    @elseif(quest instanceof MultipleChoiceQuest multiChoiceQuest)
                        <h4>Richtige Antwort:</h4><p>${multiChoiceQuest.getRightAnswer()}</p>
                    @endif
                    @if(context.answers().get(quest) != null)
                        @for(Answer answer : context.answers().get(quest))
                            <div class="group-answers">
                                <hr>
                                <h4>${answer.player().group().name()}</h4>
                                @if(quest instanceof PictureQuest)
                                    <a href="/image?id=${answer.content()}"><img src="/image?id=${answer.content()}" alt="Foto von Gruppe ${answer.player().group().name()}"></a>
                                @else
                                    <p>Antwort: ${answer.content()}</p>
                                @endif
                            </div>
                        @endfor
                    @endif
                </div>
            @endfor
        </div>
    </div>
</main>
@template.footer()
</body>
</html>