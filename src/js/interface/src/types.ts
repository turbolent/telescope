
export class Parse {
    readonly queries?: string[]
    readonly error?: string

    static decode(json: any): Parse {
        return new Parse(
            json.queries,
            json.error
        )
    }

    private constructor(
        queries?: string[],
        error?: string
    ) {
        this.queries = queries
        this.error = error
    }
}

export class Result {
    readonly uri: string
    readonly label: string
    readonly wikipediaTitle?: string

    static decode(binding: {wikipediaTitle?: {value: string}}, variable: string): Result {
        const uri = binding[variable].value
        const label = binding[variable + 'Label'].value
        const wikipediaTitle = binding.wikipediaTitle && binding.wikipediaTitle.value
        return new Result(uri, label, wikipediaTitle)
    }

    private constructor(
        uri: string,
        label: string,
        wikipediaTitle?: string
    ) {
        this.uri = uri
        this.label = label
        this.wikipediaTitle = wikipediaTitle
    }
}

interface SPARQLQueryResults {
    readonly head: {
        readonly vars: string[]
    }
    readonly results: {
        readonly bindings: {}[]
    }
}

const LABEL_PATTERN = new RegExp('Label$')

export function decodeResults(results: SPARQLQueryResults): Result[] {
    const {head: {vars}, results: {bindings}} = results
    const variable = vars.find(name => !LABEL_PATTERN.test(name))
    if (!variable) {
        throw new Error('Missing variable in SPARQL results')
    }
    return bindings.map(binding => Result.decode(binding, variable))
}

export class WikipediaPreview {
    readonly description: string
    readonly extractHTML: string
    readonly thumbnailURL?: string

    static decode(json: any): WikipediaPreview {
        return new WikipediaPreview(
            json.description,
            json.extract_html,
            json.thumbnail && json.thumbnail.source
        )
    }

    private constructor(
        description: string,
        extractHTML: string,
        thumbnailURL?: string
    ) {
        this.description = description
        this.extractHTML = extractHTML
        this.thumbnailURL = thumbnailURL
    }
}