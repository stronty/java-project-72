@import hexlet.code.dto.UrlPage
@import hexlet.code.model.UrlCheck
@import hexlet.code.util.TextTruncator
@param UrlPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="mx-auto max-w-3xl">
            <h1 class="mb-4 text-3xl font-semibold">${page.getUrl().getName()}</h1>
            <div class="overflow-x-auto rounded-lg border border-gray-200 bg-white">
                <table data-test="url" class="w-full text-left text-sm">
                    <tbody>
                        <tr class="border-b border-gray-200">
                            <td class="px-4 py-2">ID</td>
                            <td class="px-4 py-2">${page.getUrl().getId()}</td>
                        </tr>
                        <tr class="border-b border-gray-200">
                            <td class="px-4 py-2">Имя</td>
                            <td class="px-4 py-2">${page.getUrl().getName()}</td>
                        </tr>
                        <tr class="border-b border-gray-200">
                            <td class="px-4 py-2">Дата создания</td>
                            <td class="px-4 py-2">${page.getUrl().getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <form method="post" action="/urls/${page.getUrl().getId()}/checks" class="mt-4">
                <input type="submit"
                       class="cursor-pointer rounded bg-blue-600 px-8 py-2 text-lg font-medium text-white uppercase hover:bg-blue-700"
                       value="Запустить проверку">
            </form>
            <div class="mt-4 overflow-x-auto rounded-lg border border-gray-200 bg-white">
                <table data-test="checks" class="w-full text-left text-sm">
                    <thead class="bg-gray-100 uppercase text-gray-600">
                        <tr>
                            <th class="px-4 py-2">ID</th>
                            <th class="px-4 py-2">Код ответа</th>
                            <th class="px-4 py-2">h1</th>
                            <th class="px-4 py-2">title</th>
                            <th class="px-4 py-2">description</th>
                            <th class="px-4 py-2">Дата создания</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(UrlCheck check : page.getChecks())
                            <tr class="border-t border-gray-200">
                                <td class="px-4 py-2">${check.getId()}</td>
                                <td class="px-4 py-2">${check.getStatusCode()}</td>
                                <td class="px-4 py-2">${TextTruncator.truncate(check.getH1())}</td>
                                <td class="px-4 py-2">${TextTruncator.truncate(check.getTitle())}</td>
                                <td class="px-4 py-2">${TextTruncator.truncate(check.getDescription())}</td>
                                <td class="px-4 py-2">${check.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </div>
    `
)