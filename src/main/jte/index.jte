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
        .content {
            text-align: center;
            margin-top: 20vh;
        }

        .button-container {
            margin-top: 10vh;
        }

        .ui-button {
            background-color: #4CAF50;
            color: white;
            padding: 20px 40px;
            font-size: 24px;
            text-decoration: none;
            border: none;
            cursor: pointer;
        }

        .ui-button:hover {
            background-color: #45a049;
        }
    </style>
    <main>
        <div class="content">
            <h1>Willkommen</h1>
            <div class="button-container">
                <a href="/login" class="ui-button">Let's Go</a>
            </div>
        </div>
    </main>
    @template.footer()
</body>
</html>