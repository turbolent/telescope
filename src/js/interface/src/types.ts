
export class Parse {
    readonly queries?: string[];
    readonly error?: string;

    static decode(json: any): Parse {
        return new Parse(
            json.queries,
            json.error
        );
    }

    private constructor(
        queries?: string[],
        error?: string
    ) {
        this.queries = queries;
        this.error = error;
    }
}
