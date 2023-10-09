<style>
    .footer {
        background-color: #333;
        color: white;
        text-align: center;
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        padding: 10px 0;
    }

    .footer a {
        color: white;
        text-decoration: none;
    }

    .footer a:hover {
        text-decoration: underline;
    }
</style>
<footer>
    <div class="footer">
        &copy; 2023 Professor Sam | <a href="https://github.com/ProfessorSam/CityQuiz" target="_blank">GitHub Repo</a>@if(System.getenv("INSTANCE_NAME") != null) | Instanz: ${System.getenv("INSTANCE_NAME")} @endif
    </div>
</footer>