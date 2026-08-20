@import gg.jte.Content
@import hexlet.code.dto.BasePage
@param Content content
@param BasePage page = null

<!doctype html>
<html lang="ru">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Анализатор страниц</title>
        <link href="/main.css" rel="stylesheet" />
    </head>
    <body class="flex min-h-screen flex-col bg-gray-100">
        <nav class="bg-blue-600">
            <div class="mx-auto flex max-w-3xl items-center justify-between px-4 py-3">
                <a href="/" class="text-lg font-semibold text-white">Анализатор страниц</a>
                <a href="/urls" class="text-white hover:underline">Сайты</a>
            </div>
        </nav>
        <main class="flex-1 px-4 py-8">
            @if(page != null && page.getFlash() != null)
                @if(page.getFlashType().equals("error"))
                    <div class="mb-4 rounded-lg bg-red-600 px-4 py-2 text-white" role="alert">
                        ${page.getFlash()}
                    </div>
                @else
                    <div class="mb-4 rounded-lg bg-green-600 px-4 py-2 text-white" role="alert">
                        ${page.getFlash()}
                    </div>
                @endif
            @endif
            ${content}
        </main>
    </body>
</html>
