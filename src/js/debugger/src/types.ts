
export class Parse {
    readonly tokens: Token[];
    readonly tree?: TreeNode;
    readonly queries?: string[];
    readonly nodes?: GraphNode[];
    readonly error?: string;

    static decode(json: any): Parse {
        return new Parse(
            json.tokens.map(Token.decode),
            json.question && TreeNode.decode(json.question),
            json.queries,
            json.nodes && json.nodes.map((nodeJSON: any) =>
                                             GraphNode.decode(nodeJSON)),
            json.error
        );
    }

    private constructor(
        tokens: Token[],
        tree: TreeNode,
        queries?: string[],
        nodes?: GraphNode[],
        error?: string
    ) {
        this.tokens = tokens;
        this.tree = tree;
        this.queries = queries;
        this.nodes = nodes;
        this.error = error;
    }
}

export function decodeType(type: string): string {
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
        return new Token(json.lemma, json.pennTag, json.word);
    }

    constructor(lemma: string, pennTag: string, word: string) {
        this.lemma = lemma;
        this.pennTag = pennTag;
        this.word = word;
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
                        const tokens =
                            value.map(element => Token.decode(element));
                        return [
                            new TreeLeaf(property, tokens)
                        ];
                    } else {
                        return value.map(element =>
                            TreeNode.decode(element));
                    }
                } else {
                    if (representsToken(value)) {
                        return [
                            new TreeLeaf(property, [
                                Token.decode(value)
                            ])
                        ];
                    } else {
                        return [
                            TreeNode.decode(value, property)
                        ];
                    }
                }
            })
            .reduce((a, b) => a.concat(b), []);

        const type = decodeType(json.$type);
        return new TreeNode(type, children, name);
    }

    constructor(type: string, children: Tree[], name?: string) {
        this.type = type;
        this.children = children;
        this.name = name;
    }
}

export class Item {

    readonly id: number;
    readonly name: string;

    static decode(json: any): Item {
        return new Item(json.id, json.name);
    }

    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
    }
}

export class Property {
    readonly id: number;
    readonly name: string;

    static decode(json: any): Item {
        return new Property(json.id, json.name);
    }

    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
    }
}

abstract class GraphValue {
    // shortened version of $value
    readonly _type: string;

    constructor(_type: string) {
        this._type = _type;
    }
}

export class GraphNode extends GraphValue {
    readonly label: GraphNodeLabel;
    readonly edge?: GraphEdge;
    readonly filter?: GraphFilter;
    readonly aggregates: GraphAggregateFunction[];
    readonly order?: GraphOrder;

    static decode(json: any): GraphNode {
        const _type = decodeType(json.$type);
        const label = GraphNodeLabel.decode(json.label);
        const edge = json.edge && GraphEdge.decode(json.edge);
        const filter = json.filter && GraphFilter.decode(json.filter);
        return new GraphNode(_type, label, edge, filter);
    }

    constructor(_type: string, label: GraphNodeLabel, edge?: GraphEdge, filter?: GraphFilter) {
        super(_type);
        this.label = label;
        this.edge = edge;
        this.filter = filter;
    }
}

export abstract class GraphNodeLabel extends GraphValue {

    static decode(json: any): GraphNodeLabel {
        const _type = decodeType(json.$type);
        const constructor = graphNodeLabelConstructors[_type];
        return new constructor(json, _type);
    }
}

export class GraphVarLabel extends GraphNodeLabel {
    readonly id: string;

    constructor(json: any, _type: string) {
        super(_type);
        this.id = json.id;
    }
}

export class GraphItemLabel extends GraphNodeLabel {
    readonly item: Item;

    constructor(json: any, _type: string) {
        super(_type);
        this.item = Item.decode(json.item);
    }
}

export class GraphValueLabel extends GraphNodeLabel {
    readonly value: string;

    constructor(json: any, _type: string) {
        super(_type);
        this.value = json.value;
    }
}

export class GraphNumberLabel extends GraphNodeLabel {
    readonly value: number;

    constructor(json: any, _type: string) {
        super(_type);
        this.value = json.value;
    }
}

export class GraphTemporalLabel extends GraphNodeLabel {
    readonly temporal: any;

    constructor(json: any, _type: string) {
        super(_type);
        this.temporal = json.temporal;
    }
}

interface GraphNodeLabelConstructor {
    new(json: any, _type: string): GraphNodeLabel;
}

const graphNodeLabelConstructors: {
    [type: string]: GraphNodeLabelConstructor
} = {
    'VarLabel': GraphVarLabel,
    'ItemLabel': GraphItemLabel,
    'ValueLabel': GraphValueLabel,
    'NumberLabel': GraphNumberLabel,
    'TemporalLabel': GraphTemporalLabel,
};

export abstract class GraphEdgeLabel extends GraphValue {

    static decode(json: any): GraphEdgeLabel {
        const _type = decodeType(json.$type);
        const constructor = graphEdgeLabelConstructors[_type];
        return new constructor(json, _type);
    }
}

export class GraphPropertyLabel extends GraphEdgeLabel {
    readonly property: Property;

    constructor(json: any, _type: string) {
        super(_type);
        this.property = Property.decode(json.property);
    }
}

export class GraphNameLabel extends GraphEdgeLabel {

    private static instance = Object.create(GraphNameLabel.prototype);

    constructor(json: any, _type: string) {
        super(_type);
        return GraphNameLabel.instance;
    }
}

export class GraphYearLabel extends GraphEdgeLabel {
    private static instance = Object.create(GraphYearLabel.prototype);

    constructor(json: any, _type: string) {
        super(_type);
        return GraphYearLabel.instance;
    }
}

interface GraphEdgeLabelConstructor {
    new(json: any, _type: string): GraphEdgeLabel;
}

const graphEdgeLabelConstructors: {
    [type: string]: GraphEdgeLabelConstructor
} = {
    'PropertyLabel': GraphPropertyLabel,
    'NameLabel$': GraphNameLabel,
    'YearLabel$': GraphYearLabel,
};

export abstract class GraphEdge extends GraphValue {

    static decode(json: any): GraphEdge {
        const _type = decodeType(json.$type);
        const constructor = graphEdgeConstructors[_type];
        return new constructor(json, _type);
    }
}

export class GraphInEdge extends GraphEdge {
    readonly source: GraphNode;
    readonly label: GraphEdgeLabel;

    constructor(json: any, _type: string) {
        super(_type);
        this.source = GraphNode.decode(json.source);
        this.label = GraphEdgeLabel.decode(json.label);
    }
}

export class GraphOutEdge extends GraphEdge {
    readonly label: GraphEdgeLabel;
    readonly target: GraphNode;

    constructor(json: any, _type: string) {
        super(_type);
        this.label = GraphEdgeLabel.decode(json.label);
        this.target = GraphNode.decode(json.target);
    }
}

export class GraphConjunctionEdge extends GraphEdge {
    readonly edges: GraphEdge[];

    constructor(json: any, _type: string) {
        super(_type);
        this.edges = json.edges.map((innerJSON: any) =>
                                        GraphEdge.decode(innerJSON));
    }
}

export class GraphDisjunctionEdge extends GraphEdge {
    readonly edges: GraphEdge[];

    constructor(json: any, _type: string) {
        super(_type);
        this.edges = json.edges.map((innerJSON: any) =>
                                        GraphEdge.decode(innerJSON));
    }
}

interface GraphEdgeConstructor {
    new(json: any, _type: string): GraphEdge;
}

const graphEdgeConstructors: {
    [type: string]: GraphEdgeConstructor
} = {
    'InEdge': GraphInEdge,
    'OutEdge': GraphOutEdge,
    'ConjunctionEdge': GraphConjunctionEdge,
    'DisjunctionEdge': GraphDisjunctionEdge,
};

export abstract class GraphFilter extends GraphValue {

    static decode(json: any): GraphEdge {
        const _type = decodeType(json.$type);
        const constructor = filterConstructors[_type];
        return new constructor(json, _type);
    }
}

export class GraphConjunctionFilter extends GraphFilter {
    readonly filters: GraphFilter[];

    constructor(json: any, _type: string) {
        super(_type);
        this.filters = json.filters.map((innerJSON: any) =>
                                            GraphFilter.decode(innerJSON));
    }
}

export class GraphEqualsFilter extends GraphFilter {
    readonly node: GraphNode;

    constructor(json: any, _type: string) {
        super(_type);
        this.node = GraphNode.decode(json.node);
    }
}

export class GraphLessThanFilter extends GraphFilter {
    readonly node: GraphNode;

    constructor(json: any, _type: string) {
        super(_type);
        this.node = GraphNode.decode(json.node);
    }
}

export class GraphGreaterThanFilter extends GraphFilter {
    readonly node: GraphNode;

    constructor(json: any, _type: string) {
        super(_type);
        this.node = GraphNode.decode(json.node);
    }
}

interface GraphFilterConstructor {
    new(json: any, _type: string): GraphEdge;
}

const filterConstructors: {
    [type: string]: GraphFilterConstructor
} = {
    'ConjunctionFilter': GraphConjunctionFilter,
    'EqualsFilter': GraphEqualsFilter,
    'LessThanFilter': GraphLessThanFilter,
    'GreaterThanFilter': GraphGreaterThanFilter,
};

export abstract class GraphAggregateFunction extends GraphValue {
}

export abstract class GraphOrder extends GraphValue {
}
