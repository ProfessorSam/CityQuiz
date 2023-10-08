@import com.github.professorSam.context.QuestViewContext
@import com.github.professorSam.db.model.Player
@import com.github.professorSam.quest.AnswerQuest
@import com.github.professorSam.quest.MultipleChoiceQuest
@import com.github.professorSam.quest.PictureQuest
@param QuestViewContext context
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
    .main-container {
        font-family: Arial, sans-serif;
        background-color: #f0f0f0;
        margin: 0;
        padding: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 92vh;
    }

    .container {
        background-color: #fff;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
        max-width: 90%;
        text-align: center;
    }

    h1 {
        font-size: 24px;
        margin-bottom: 10px;
    }

    p {
        font-size: 16px;
        margin-bottom: 20px;
    }

    form {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    label {
        font-size: 18px;
        margin-bottom: 10px;
    }

    input[type="text"] {
        width: 100%;
        padding: 10px;
        margin-bottom: 20px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 16px;
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
    <div class="main-container">
        <div class="container">
            @if(context.player().nationality() == Player.Nationality.GERMAN)
                <h1>${context.quest().getTitleDE()}</h1>
                <p>${context.quest().getDescriptionDE()}</p>
                <form action="/questview" method="post" enctype="multipart/form-data">
                    @if(context.quest() instanceof AnswerQuest)
                        <label for="answer">Deine Antwort:</label>
                        <input type="text" id="answer" name="answer" required>
                        <button type="submit">Abgeben</button>
                    @elseif(context.quest() instanceof MultipleChoiceQuest quest)
                        <fieldset>
                            <legend>Wähle eine Antwort:</legend>
                            @for(String answer : quest.getChoices())
                                <label>
                                    <input type="radio" name="choice" value="${answer}">
                                    ${answer}
                                </label>
                                <br>
                            @endfor
                        </fieldset>
                        <br>
                        <button type="submit">Abgeben</button>
                    @elseif(context.quest() instanceof PictureQuest)
                        <label for="answer">Wähle ein Foto:</label>
                        <input type="file" id="picture" name="picture" accept="image/*" capture="user">
                        <br>
                        <button type="submit">Hochladen</button>
                    @endif
                </form>
            @else
                <h1>${context.quest().getTitleFR()}</h1>
                <p>${context.quest().getDescriptionFR()}</p>
                <form action="/questview" method="post">
                    @if(context.quest() instanceof AnswerQuest)
                        <label for="answer">Ta réponse:</label>
                        <input type="text" id="answer" name="answer" required>
                        <button type="submit">Donner ta réponse</button>
                    @elseif(context.quest() instanceof MultipleChoiceQuest quest)
                        <fieldset>
                            <legend>Choisis une réponse:</legend>
                            @for(String answer : quest.getChoices())
                                <label>
                                    <input type="radio" name="choice" value="${answer}">
                                    ${answer}
                                </label>
                                <br>
                            @endfor
                        </fieldset>
                        <br>
                        <button type="submit">Donner ta réponse</button>
                    @elseif(context.quest() instanceof PictureQuest)
                        <label for="answer">Choisis une photo:</label>
                        <input type="file" id="picture" name="picture" accept="image/*" capture="user">
                        <br>
                        <button type="submit">Upload</button>
                    @endif
                </form>
            @endif
        </div>
    </div>
</main>
@template.footer()
</body>
</html>