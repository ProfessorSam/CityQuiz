@import com.github.professorSam.context.ErrorContext
@param ErrorContext context
<!DOCTYPE html>
<html lang="de">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>City Quiz - Fehler</title>
    <meta name="description" content="A quiz for a city">
</head>
<body>
@template.header()
<style>
    body {
        text-align: center;
        background-color: #f2f2f2;
        font-family: Arial, sans-serif;
    }
    .error-message {
        color: red;
        font-size: 36px;
        margin-top: 20vh;
    }
    .return-button {
        padding: 20px 30px;
        background-color: #ff0000;
        color: white;
        font-size: 22px;
        text-decoration: none;
        border: none;
        cursor: pointer;
    }
    .return-button:hover {
        background-color: #cc0000;
    }
</style>
<main>
    <div class="error-message">
        <b>Fehler: ${context.message()}</b>
    </div>
    <a class="return-button" href="/">Startseite</a>
</main>
</body>
@template.footer()
</html>