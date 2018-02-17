
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

    static decode(binding: {}, variable: string): Result {
        const uri = binding[variable].value
        const label = binding[variable + 'Label'].value
        return new Result(uri, label)
    }

    private constructor(
        uri: string,
        label: string
    ) {
        this.uri = uri
        this.label = label
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
