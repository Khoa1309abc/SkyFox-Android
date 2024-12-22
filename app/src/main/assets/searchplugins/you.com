<!-- This Source Code Form is subject to the terms of the Mozilla Public
   - License, v. 2.0. If a copy of the MPL was not distributed with this
   - file, You can obtain one at http://mozilla.org/MPL/2.0/. -->

<SearchPlugin xmlns="http://www.mozilla.org/2006/browser/search/">
    <ShortName>You.com</ShortName>
    <InputEncoding>UTF-8</InputEncoding>
    <Image width="16" height="16">data:image/png;base64,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</Image>
    <Url type="application/x-suggestions+json" method="GET" template="https://search.brave.com/api/suggest">
        <Param name="q" value="{searchTerms}"/>
    </Url>
    <Url type="text/html" method="GET" template="https://you.com/search">
        <Param name="q" value="{searchTerms}"/>
    </Url>
    <SearchForm>https://you.com/</SearchForm>
</SearchPlugin>