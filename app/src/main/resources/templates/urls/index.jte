@import hexlet.code.dto.UrlsPage
@import hexlet.code.model.Url
@param UrlsPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="mx-auto max-w-3xl">
            <h1 class="mb-4 text-3xl font-semibold">Сайты</h1>
            <div class="overflow-x-auto rounded-lg border border-gray-200 bg-white">
                <table data-test="urls" class="w-full text-left text-sm">
                    <thead class="bg-gray-100 uppercase text-gray-600">
                        <tr>
                            <th class="px-4 py-2">ID</th>
                            <th class="px-4 py-2">Имя</th>
                            <th class="px-4 py-2">Дата создания</th>
                            <th class="px-4 py-2">Код ответа</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(Url url : page.getUrls())
                            <tr class="border-t border-gray-200">
                                <td class="px-4 py-2">${url.getId()}</td>
                                <td class="px-4 py-2">
                                    <a href="/urls/${url.getId()}" class="text-blue-600 hover:underline">${url.getName()}</a>
                                </td>
                                <td class="px-4 py-2">${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                                <td class="px-4 py-2"></td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </div>
    `
)