import {
    GraphConjunctionEdge, GraphDisjunctionEdge,
    GraphEdge,
    GraphEdgeLabel, GraphFilter, GraphGreaterThanFilter, GraphInEdge,
    GraphItemLabel, GraphLessThanFilter, GraphNameLabel, GraphNode, GraphNodeLabel, GraphNumberLabel, GraphOutEdge,
    GraphPropertyLabel,
    GraphTemporalLabel, GraphValueLabel, GraphVarLabel, GraphYearLabel
} from '../types';
import { Wikidata } from '../wikidata';

type GetterFunction<T, U> = (value: T, ...args: any[]) => U
type GetterEntry<T, U> = [Function, GetterFunction<T, U>]

function makeGetter<T, U>(entries: GetterEntry<T, U>[]): GetterFunction<T, U | undefined> {
    const map = new Map(entries);
    return (value: T, ...args: any[]) => {
        const type = Object.getPrototypeOf(value).constructor;
        const getter = map.get(type);
        if (!getter) {
            return;
        }
        return getter.apply(null, [value].concat(args));
    };
}

interface GraphComponentValue {}

// Nodes

export abstract class GraphComponentNode implements GraphComponentValue {
    x: number = 0;
    y: number = 0;

    readonly id: number;
    readonly text: string;
    readonly link?: string;
    readonly isRoot?: boolean;

    private static nextID = 0;

    protected constructor(text: string, link?: string, isRoot?: boolean) {
        this.id = GraphComponentNode.nextID++;
        this.text = text;
        this.link = link;
        this.isRoot = isRoot;
    }
}

export class GraphComponentLabelNode extends GraphComponentNode {

    private static readonly textGetter =
        makeGetter<GraphNodeLabel, string>(
            [
                [GraphVarLabel, (label: GraphVarLabel) => `?${label.id}`],
                [GraphItemLabel, (label: GraphItemLabel) => label.item.name],
                [GraphValueLabel, (label: GraphValueLabel) => `"${label.value}"`],
                [GraphNumberLabel, (label: GraphNumberLabel) => label.value + ''],
                [GraphTemporalLabel, (label: GraphTemporalLabel) => {
                    // TODO:
                    return label.temporal + "";
                }]
            ]
        );

    private static readonly linkGetter =
        makeGetter<GraphNodeLabel, string>([
                                               [GraphItemLabel, (label: GraphItemLabel) =>
                                                   Wikidata.getItemURL(label.item.id)]
                                           ]);

    static fromGraphNodeLabel(nodeLabel: GraphNodeLabel, isRoot = false): GraphComponentLabelNode | undefined {
        const text = GraphComponentLabelNode.textGetter(nodeLabel);
        if (text === undefined) {
            return;
        }
        const link = GraphComponentLabelNode.linkGetter(nodeLabel);

        const type = nodeLabel instanceof GraphVarLabel
            ? GraphComponentVarLabelNode
            : GraphComponentLabelNode;

        return new type(text, link, isRoot);
    }
}

export class GraphComponentVarLabelNode extends GraphComponentNode {}

export class GraphComponentConjunctionNode extends GraphComponentNode {
    constructor() {
        super('&')
    }
}

export class GraphComponentDisjunctionNode extends GraphComponentNode {
    constructor() {
        super('|')
    }
}

// Edges

export class GraphComponentEdge implements GraphComponentValue {
    readonly source: GraphComponentNode;
    readonly target: GraphComponentNode;
    readonly text?: string;
    readonly link?: string;

    constructor(source: GraphComponentNode, target: GraphComponentNode, text?: string, link?: string) {
        this.source = source;
        this.target = target;
        this.text = text;
        this.link = link;
    }
}

export class GraphComponentDirectedEdge extends GraphComponentEdge {

    private static readonly textGetter =
        makeGetter<GraphEdgeLabel, string>(
            [
                [GraphPropertyLabel, (label: GraphPropertyLabel) => label.property.name],
                [GraphNameLabel, () => 'name'],
                [GraphYearLabel, () => 'year']
            ]
        );

    private static readonly linkGetter =
        makeGetter<GraphEdgeLabel, string>(
            [
                [GraphPropertyLabel, (label: GraphPropertyLabel) =>
                    Wikidata.getPropertyURL(label.property.id)]
            ]
        );

    static fromGraphEdgeLabel(edgeLabel: GraphEdgeLabel,
                              source: GraphComponentNode,
                              target: GraphComponentNode): GraphComponentDirectedEdge | undefined {

        const text = GraphComponentDirectedEdge.textGetter(edgeLabel);
        if (text === undefined) {
            return;
        }
        const link = GraphComponentDirectedEdge.linkGetter(edgeLabel);
        return new GraphComponentDirectedEdge(source, target, text, link);
    }
}

export abstract class GraphComponentFilterEdge extends GraphComponentEdge {
}

export class GraphComponentLessThanFilterEdge extends GraphComponentFilterEdge {
    constructor(source: GraphComponentNode, target: GraphComponentNode) {
        super(source, target, 'is less than');
    }
}

export class GraphComponentGreaterThanFilterEdge extends GraphComponentFilterEdge {
    constructor(source: GraphComponentNode, target: GraphComponentNode) {
        super(source, target, 'is greater than');
    }
}

type GraphComponentNodesAndEdges = [
    GraphComponentNode[],
    GraphComponentEdge[]
]

export function parseGraphNode(graphNode: GraphNode, isRoot = false): GraphComponentNodesAndEdges {
    const {label, edge, filter} = graphNode;

    const source = GraphComponentLabelNode.fromGraphNodeLabel(label, isRoot);
    if (!source) {
        return [[], []];
    }

    const [edgeNodes, edgeEdges]: GraphComponentNodesAndEdges =
        edge && parseGraphEdge(edge, source)
        || [[], []];

    const [filterNodes, filterEdges]: GraphComponentNodesAndEdges =
        filter && parseGraphFilter(filter, source)
        || [[], []];

    const allNodes = [source]
        .concat(edgeNodes)
        .concat(filterNodes);

    const allEdges = edgeEdges
        .concat(filterEdges);

    return [allNodes, allEdges]
}

const parseGraphFilter =
    makeGetter<GraphFilter, GraphComponentNodesAndEdges>(
        [
            [GraphLessThanFilter, (filter: GraphLessThanFilter, source: GraphComponentNode) => {
                const [nodes, edges] = parseGraphNode(filter.node);
                const edge = new GraphComponentLessThanFilterEdge(source, nodes[0]);
                return [nodes, [edge].concat(edges)]
            }],
            [GraphGreaterThanFilter, (filter: GraphGreaterThanFilter, source: GraphComponentNode) => {
                const [nodes, edges] = parseGraphNode(filter.node);
                const edge = new GraphComponentGreaterThanFilterEdge(source, nodes[0]);
                return [nodes, [edge].concat(edges)]
            }]
        ]
    );

const parseGraphEdge =
    makeGetter<GraphEdge, GraphComponentNodesAndEdges>(
        [
            [GraphConjunctionEdge, (filter: GraphConjunctionEdge, source: GraphComponentNode) => {
                const componentNode = new GraphComponentConjunctionNode();
                const componentEdge = new GraphComponentEdge(source, componentNode);
                const [componentNodes, componentEdges] = parseEdges(filter.edges, componentNode);
                return [
                    [componentNode].concat(componentNodes),
                    [componentEdge].concat(componentEdges)
                ]
            }],
            [GraphDisjunctionEdge, (value: GraphDisjunctionEdge, source: GraphComponentNode) => {
                const componentNode = new GraphComponentDisjunctionNode();
                const componentEdge = new GraphComponentEdge(source, componentNode);
                const [componentNodes, componentEdges] = parseEdges(value.edges, componentNode);
                return [
                    [componentNode].concat(componentNodes),
                    [componentEdge].concat(componentEdges)
                ]
            }],
            [GraphOutEdge, (value: GraphOutEdge, source: GraphComponentNode) => {
                const [nodes, edges] = parseGraphNode(value.target);
                const edge = GraphComponentDirectedEdge.fromGraphEdgeLabel(value.label, source, nodes[0]);
                if (!edge) {
                    return [[], []];
                }
                return [nodes, [edge].concat(edges)]
            }],
            [GraphInEdge, (value: GraphInEdge, source: GraphComponentNode) => {
                const [nodes, edges] = parseGraphNode(value.source);
                const edge = GraphComponentDirectedEdge.fromGraphEdgeLabel(value.label, nodes[0], source);
                if (!edge) {
                    return [[], []];
                }
                return [nodes, [edge].concat(edges)]
            }]
        ]);

function parseEdges(edges: GraphEdge[], source: GraphComponentNode): GraphComponentNodesAndEdges {
    return edges.reduce<GraphComponentNodesAndEdges>(
        ([currentNodes, currentEdges], edge: GraphEdge) => {
            const [nodes, edges] = parseGraphEdge(edge, source) || [[], []];
            return [
                currentNodes.concat(nodes),
                currentEdges.concat(edges)
            ]
        },
        [[], []]
    )
}
