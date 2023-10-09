<style>
    body {
        margin: 0;
        padding: 0;
        font-family: Arial, sans-serif;
    }

    .header {
        background-color: #903CBC;
        height: 8vh;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 2rem;
        box-shadow: 0 4px 4px rgba(0, 0, 0, 1);
    }

    .header-text {
        color: white;
        font-size: 30px;
    }

    #countdown {
        color: white;
        font-size: 20px;
    }
</style>
<header>
    <div class="header">
        <div class="header-text">City Quiz</div>
        <div id="countdown">Zeit: 00:00:00</div>
    </div>
</header>
<script>
    const targetTimestamp = $unsafe{String.valueOf(com.github.professorSam.Main.getInstance().getGameEndTime().getEpochSecond())};
    function updateCountdown() {
        const currentTime = Math.floor(Date.now() / 1000);
        const timeDifference = targetTimestamp - currentTime;
        if (timeDifference <= 0) {
            document.getElementById("countdown").textContent = "Zeit abgelaufen!";
        } else {
            const hours = Math.floor(timeDifference / 3600);
            const minutes = Math.floor((timeDifference % 3600) / 60);
            const seconds = timeDifference % 60;
            document.getElementById("countdown").textContent = "Zeit: " +
                (hours < 10 ? "0" + hours : hours) +
                ":" +
                (minutes < 10 ? "0" + minutes : minutes) +
                ":" +
                (seconds < 10 ? "0" + seconds : seconds);
        }
    }
    setInterval(updateCountdown, 1000);
    updateCountdown();
</script>