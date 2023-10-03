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
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
            overflow: hidden;
        }

        .content {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .form-container {
            background-color: #fff;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            max-width: 400px;
        }

        h2 {
            font-size: 28px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            font-size: 18px;
            margin-bottom: 10px;
            text-align: center;
        }

        .input {
            width: 100%;
            padding: 12px;
            font-size: 18px;
            border: 1px solid #ccc;
            border-radius: 10px;
            transition: border-color 0.3s;
        }

        .input:focus {
            border-color: #4CAF50;
        }

        .language-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 10px;
            padding-left: 20px;
            padding-right: 20px;
        }

        .option {
            background-color: #e0e0e0;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 18px;
            transition: background-color 0.3s;
        }

        .group-info {
            color: rgba(141,16,0,0.85)
        }

        .option:hover {
            background-color: #ccc;
        }

        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 15px 40px;
            font-size: 18px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .btn:hover {
            background-color: #45a049;
        }
    </style>
    <main>
        <div class="content">
            <div class="form-container">
                <form action="/login" method="POST">
                    <h2>Login</h2>
                    <div class="form-group">
                        <label for="username">Dein Name:</label>
                        <input type="text" id="username" name="username" required>
                    </div>

                    <div class="form-group">
                        <label for="groupname">Name der Gruppe:</label>
                        <input type="text" id="groupname" name="groupname" required>
                        <p class="group-info">Achtung: Der Name der Gruppe muss bei allen Mitgliedern gleich sein</p>
                    </div>

                    <div class="form-group">
                        <label for="flag">Deine Sprache:</label>
                        <div class="language-options">
                            <label class="option" for="germanFlag">Deutsch
                                <input type="radio" id="germanFlag" name="language" value="german" required>
                            </label>
                            <label class="option" for="frenchFlag">Française
                                <input type="radio" id="frenchFlag" name="language" value="french" required>
                            </label>
                        </div>
                    </div>
                    <button type="submit" class="btn">Let's Go!</button>
                </form>
            </div>
        </div>
    </main>
    @template.footer()
</body>
</html>