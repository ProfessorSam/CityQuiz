@import com.github.professorSam.context.QuestOverviewContext
@import com.github.professorSam.db.model.Player
@param QuestOverviewContext context

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
        background-color: #f0f0f0;
        margin: 0;
        padding: 0;
    }

    .groupmetadata {
        background-color: #6B5CE0;
        color: #fff;
        text-align: center;
        padding: 20px 0;
    }

    .groupmetadata h1 {
        font-size: 32px;
        margin: 0;
    }

    .groupmetadata p {
        font-size: 28px;
        margin: 0;
    }

    .timeline {
        margin-top: 25px;
        margin-bottom: 50px;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .timeline a {
        text-decoration: none;
        color: rgba(0, 0, 0, 0.83);
    }

    .event {
        display: flex;
        align-items: center;
        margin: 10px;
    }

    .circle {
        width: 30px;
        height: 30px;
        background-color: #337DC6;
        color: #fff;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        font-weight: bold;
        font-size: 23px;
        flex-direction: column;
        line-height: 30px;
    }

    .line {
        height: 30px;
        border-left: 2px solid #337DC6;
        margin-left: 15px;
    }

    .text {
        margin-left: 15px;
        font-size: 19px;
    }
</style>
<main>
    <div class="groupmetadata">
        <h1>${context.player().group().name()}</h1><br>
        <p>${context.player().name()}</p>
    </div>
    <div class="timeline">
        @for(int i = 0; i < context.questList().size(); i++)
            @if(i < context.player().group().quest())
                <div class="event">
                    <div class="circle">${i + 1}</div>
                    <div class="line"></div>
                    @if(context.player().nationality() == Player.Nationality.FRENCH)
                        <div class="text"><i>${context.questList().get(i).getTitleFR()}</i></div>
                    @else
                        <div class="text"><i>${context.questList().get(i).getTitleDE()}</i></div>
                    @endif
                </div>
            @elseif(i == context.player().group().quest())
                <div class="event">
                     <div class="circle">${i + 1}</div>
                    <div class="line"></div>
                    @if(context.player().nationality() == Player.Nationality.FRENCH)
                        <div class="text"><b><a href="/questview">${context.questList().get(i).getTitleFR()}</a></b></div>
                    @else
                        <div class="text"><b><a href="/questview">${context.questList().get(i).getTitleDE()}</a></b></div>
                    @endif
                </div>
            @else
                <div class="event">
                    <div class="circle">🔒</div>
                    <div class="line"></div>
                    @if(context.player().nationality() == Player.Nationality.FRENCH)
                        <div class="text"><s>${context.questList().get(i).getTitleFR()}</s></div>
                    @else
                        <div class="text"><s>${context.questList().get(i).getTitleDE()}</s></div>
                     @endif
                </div>
            @endif
        @endfor
    </div>
</main>
<script>
    const x = ${context.player().group().quest()};
    function fetchAndRefresh() {
        fetch('/currentquest?id=$unsafe{context.player().group().id().toString()}')
            .then(response => response.json())
            .then(data => {
                const fetchedValue = data.value;
                if (fetchedValue > x) {
                    console.log('Current quest got updated to ' + fetchedValue + ' - previous: ' + x)
                    location.reload(true);
                }
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    setInterval(fetchAndRefresh, 5000);
    fetchAndRefresh();
</script>
@template.footer()
</body>
</html>