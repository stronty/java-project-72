@import hexlet.code.dto.UrlPage
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
        </div>
    `
)