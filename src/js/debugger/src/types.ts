export class Parse {
    readonly tokens: Token[];
    readonly tree?: TreeNode;
    readonly queries?: string[];
    readonly error?: string;

    static decode(json: any): Parse {
        return new Parse(
            json.tokens.map(Token.decode),
            json.question && TreeNode.decode(json.question),
            json.queries,
            json.error
        );
    }

    private constructor(
        tokens: Token[],
        tree: TreeNode,
        queries?: string[],
        error?: string
    ) {
        this.tokens = tokens;
        this.tree = tree;
        this.queries = queries;
        this.error = error;
    }
}

function decodeType(type: string): string {
    const index = type.lastIndexOf('.');
    if (index < 0) {
        return type;
    }
    return type.substring(index + 1);
}

export class Token {
    readonly lemma: string;
    readonly pennTag: string;
    readonly word: string;

    static decode(json: any): Token {
         const token = new Token();
         return Object.assign(token, json);
    }
}

function representsToken(json: any): boolean {
    return typeof json === 'object'
        && decodeType(json.$type) === 'Token';
}

export type Tree = TreeNode | TreeLeaf;

export class TreeLeaf {
    readonly name: string;
    readonly tokens: Token[];

    constructor(name: string, tokens: Token[]) {
        this.name = name;
        this.tokens = tokens;
    }
}

export class TreeNode {
    readonly type: string;
    readonly children: Tree[];
    readonly name?: string;

    static decode(json: any, name?: string): TreeNode {
        const children = Object.keys(json)
            .filter(property => property !== '$type')
            .map((property: string): Tree[] => {
                const value = json[property];
                if (Array.isArray(value)) {
                    if (!value.length) {
                        return [];
                    }

                    if (representsToken(value[0])) {
                        const tokens = value.map(element =>
                            Token.decode(element));
                        return [
                            new TreeLeaf(property, tokens)
                        ];
                    } else {
                        return value.map(element =>
                            TreeNode.decode(element));
                    }
                }

                return [
                    TreeNode.decode(value, property)
                ];
            })
            .reduce((a, b) => a.concat(b), []);

        const type = decodeType(json.$type);
        return new TreeNode(type, children, name);
    }

    private constructor(type: string, children: Tree[], name?: string) {
        this.type = type;
        this.children = children;
        this.name = name;
    }
}