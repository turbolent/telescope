import * as React from 'react';
import { forceCenter, forceCollide, forceLink, forceManyBody, forceSimulation, Simulation } from 'd3-force';
import {
    GraphComponentConjunctionNode,
    GraphComponentDirectedEdge, GraphComponentEdge, GraphComponentFilterEdge, GraphComponentLabelNode,
    GraphComponentNode, GraphComponentVarLabelNode
} from './types';
import settings from './settings';
import { HSLColor } from 'd3-color';
import './GraphComponent.css';
import { ReactNode } from 'react';

interface Props {
    nodes: GraphComponentNode[];
    links: GraphComponentEdge[];
}

interface ComponentState {
    nodes: GraphComponentNode[];
    links: GraphComponentEdge[];
    width: number;
    height: number;
}

export default class GraphComponent extends React.Component<Props, ComponentState> {

    private force: Simulation<GraphComponentNode, GraphComponentEdge>;
    private id: number;

    private static getNextId = (() => {
        let nextId = 0;
        return () => nextId++
    })();

    private static getLinkDistance(edge: GraphComponentEdge): number {
        const {getLabeled, getLong} =
            settings.edge.distance;

        const length = edge.text && edge.text.length || 0;

        if (edge instanceof GraphComponentDirectedEdge
            || edge instanceof GraphComponentFilterEdge)
        {
            return getLabeled(length);
        }

        return getLong()
    }

    private static getEdgeStroke(edge: GraphComponentEdge): HSLColor {
        const {getDirected, getFilter, getOther} =
            settings.edge.color;

        if (edge instanceof GraphComponentDirectedEdge) {
            return getDirected();
        }

        if (edge instanceof GraphComponentFilterEdge) {
            return getFilter();
        }

        return getOther()
    }

    private static getLinkOffset(edge: GraphComponentEdge): [number, number] {
        if (!(edge instanceof GraphComponentDirectedEdge)
            && !(edge instanceof GraphComponentFilterEdge))
        {
            return [0, 0]
        }

        const diffX = edge.target.x - edge.source.x;
        const diffY = edge.target.y - edge.source.y;

        const pathLength = Math.sqrt((diffX * diffX) + (diffY * diffY));
        if (!pathLength) {
            return [0, 0]
        }

        const offset = settings.node.radius
            + settings.marker.size;

        return [
            (diffX * offset) / pathLength,
            (diffY * offset) / pathLength
        ];
    }

    private static edgePath(edge: GraphComponentEdge): string {
        const [offsetX, offsetY] = GraphComponent.getLinkOffset(edge);
        return 'M' + [edge.source.x, edge.source.y].join(',')
            + 'L' + [edge.target.x - offsetX, edge.target.y - offsetY].join(',')
    }

    private static getNodeFill(node: GraphComponentNode): HSLColor {
        const {getRoot, getVariable, getLabel, getOther} =
            settings.node.backgroundColor;

        if (node.isRoot) {
            return getRoot();
        }

        if (node instanceof GraphComponentVarLabelNode) {
            return getVariable();
        }

        if (node instanceof GraphComponentLabelNode) {
            return getLabel()
        }

        return getOther()
    }

    private static getNodeStroke(node: GraphComponentNode): HSLColor {
        const {getRoot, getVariable, getLabel, getOther} =
            settings.node.stroke.color;

        if (node.isRoot) {
            return getRoot();
        }

        if (node instanceof GraphComponentVarLabelNode) {
            return getVariable();
        }

        if (node instanceof GraphComponentLabelNode) {
            return getLabel()
        }

        return getOther()
    }

    private static getNodeTextFill(node: GraphComponentNode): HSLColor {
        const {getRoot, getVariable, getConjunction, getLink, getOther} =
            settings.node.textColor;

        if (node.isRoot) {
            return getRoot();
        }

        if (node instanceof GraphComponentVarLabelNode) {
            return getVariable();
        }

        if (node instanceof GraphComponentConjunctionNode)
            return getConjunction();

        if (node.link)
            return getLink();

        return getOther()
    }

    private static getNodeTextFontWeight(node: GraphComponentNode): string | undefined {
        if (node instanceof GraphComponentLabelNode) {
            return 'bold'
        }

        return;
    }

    private static getNodeTextShadow(node: GraphComponentNode): string {
        const {normal, strong} =
            settings.textShadow;

        if (node instanceof GraphComponentLabelNode) {
            return strong;
        }

        return normal
    }

    private static getEdgeStrokeDashArray(edge: GraphComponentEdge): string | undefined {
        if (edge instanceof GraphComponentFilterEdge) {
            return settings.secondaryDashArray;
        }

        return;
    }

    private static getEdgeLabelFill(edge: GraphComponentEdge): HSLColor {
        const {getLink, getOther} = settings.edge.textColor;

        if (edge.link) {
            return getLink()
        }

        return getOther()
    }

    private getEdgePathIdentifier(index: number): string {
        return `edgepath-${this.id}-${index}`
    }

    private static getEdgeLabelTransform(edge: GraphComponentEdge, element: SVGGraphicsElement): string {
        if (edge.target.x >= edge.source.x) {
            return 'rotate(0)';
        }

        const bbox = element.getBBox();
        const rx = bbox.x + bbox.width / 2;
        const ry = bbox.y + bbox.height / 2;
        return `rotate(180 ${rx} ${ry})`
    }

    private static getNextState(props: Props): ComponentState {
        const {nodes, links} = props;
        const {baseWidth, baseHeight} = settings.size;
        const nodeCount = nodes.length;
        const linkCount = links.length;
        const width = settings.size.adjustValue(baseWidth, nodeCount, linkCount);
        const height = settings.size.adjustValue(baseHeight, nodeCount, linkCount);

        return {
            nodes,
            links,
            width,
            height
        };
    }

    private static linkify(link: string | undefined, content: ReactNode): ReactNode {
        if (!link) {
            return content;
        }

        return (
            <a href={link} target="_blank">
                {content}
            </a>
        );
    }

    constructor(props: Props) {
        super(props);

        this.id = GraphComponent.getNextId();
        this.state = GraphComponent.getNextState(props);
    }

    private getMarkerId(type: string): string {
        return `end-arrow-${this.id}-${type}`
    }

    componentDidMount() {
        this.startForceSimulation();
    }

    private startForceSimulation() {
        this.force = forceSimulation(this.state.nodes)
            .force("charge",
                   forceManyBody()
                       .strength(settings.layout.manyBodyForceStrength)
            )
            .force("link",
                   forceLink()
                       .distance(GraphComponent.getLinkDistance)
                       .links(this.state.links))
            .force("center",
                   forceCenter(
                       this.state.width / 2,
                       this.state.height / 2
                   ))
            .force('collide',
                   forceCollide(settings.layout.getCollisionRadius()));

        this.forwardForceSimulation();

        this.force.on('tick', () =>
            this.setState({
                              links: this.state.links,
                              nodes: this.state.nodes
                          }));
    }

    private forwardForceSimulation(percentage = 1) {
        const {force} = this;
        // from https://bl.ocks.org/mbostock/01ab2e85e8727d6529d20391c0fd9a16
        const n = Math.ceil(Math.log(force.alphaMin()) / Math.log(1 - force.alphaDecay())) * percentage;
        for (let i = 0; i < n; ++i) {
            force.tick();
        }
    }

    componentWillUnmount() {
        this.stopForceSimulation();
    }

    private stopForceSimulation() {
        this.force.stop();
    }

    componentWillReceiveProps(nextProps: Props) {
        if (nextProps.nodes === this.props.nodes
            && nextProps.links === this.props.links)
        {
            return
        }
        const nextState = GraphComponent.getNextState(nextProps);
        this.setState(nextState, () => {
            this.stopForceSimulation();
            this.startForceSimulation();
        });
    }

    relayout = () => {
        this.force.alpha(0.8);
        this.force.alphaDecay(0.012);
        this.force.restart();
    };

    render() {
        return (
            <div className="Graph">
                <div className="GraphBorder">
                    <button onClick={this.relayout}>🔃</button>
                    <svg
                        width={this.state.width}
                        height={this.state.height}
                    >
                        {this.renderEdges()}
                        {this.renderMarkers()}
                        {this.renderNodes()}
                        {this.renderEdgeLabels()}
                    </svg>
                </div>
            </div>
        );
    }

    private transformEdgeLabels(container: SVGGElement | null) {
        if (!container) {
            return
        }

        for (let index = 0; index < container.childNodes.length; index++) {
            const child = container.childNodes.item(index);
            if (!(child instanceof SVGGraphicsElement)) {
                continue;
            }

            const edge = this.state.links[index];
            if (!edge) {
                continue;
            }

            const transform = GraphComponent.getEdgeLabelTransform(edge, child);
            child.setAttribute('transform', transform);
        }
    }

    private renderEdgeLabels() {
        const {labelOffsetX, labelOffsetY} = settings.edge;

        return (
            <g ref={(container) => this.transformEdgeLabels(container)}>
                {
                    this.state.links.map(
                        (edge, index) => (
                            GraphComponent.linkify(edge.link,
                                <text
                                    className="GraphEdgeLabel"
                                    dy={labelOffsetY}
                                    fontWeight="bold"
                                    fill={GraphComponent.getEdgeLabelFill(edge).toString()}
                                >
                                    <textPath
                                        xlinkHref={'#' + this.getEdgePathIdentifier(index)}
                                        startOffset={labelOffsetX}
                                    >
                                        {edge.text}
                                    </textPath>
                                </text>
                            )
                        )
                    )
                }
            </g>
        );
    }

    private renderMarkers() {
        const {color, size} = settings.marker;
        return Object.keys(color).map(type => {
            const id = this.getMarkerId(type);

            const colorFactory = color[type];

            return (
                <marker
                    id={id}
                    viewBox='0 -5 10 10'
                    markerWidth={size}
                    markerHeight={size}
                    orient='auto'
                    markerUnits='userSpaceOnUse'
                >
                    <path
                        d='M0,-5L10,0L0,5'
                        fill={colorFactory()}
                    />
                </marker>
            );
        })
    }

    private renderEdges() {
        const {strokeWidth} = settings.edge;

        return this.state.links.map(
            (edge, index) => {
                const stroke = GraphComponent.getEdgeStroke(edge);
                return (
                    <path
                        id={this.getEdgePathIdentifier(index)}
                        d={GraphComponent.edgePath(edge)}
                        key={`line-${index}`}
                        stroke={stroke.toString()}
                        strokeWidth={strokeWidth}
                        strokeDasharray={GraphComponent.getEdgeStrokeDashArray(edge)}
                        markerEnd={this.getEdgeMarkerEnd(edge)}
                    />
                );
            });
    }

    private getEdgeMarkerEnd(edge: GraphComponentEdge): string | undefined {
        const isDirected = edge instanceof GraphComponentDirectedEdge;
        const isFilter = edge instanceof GraphComponentFilterEdge;
        if (!isDirected && !isFilter) {
            return;
        }

        const type = isDirected ? 'getDirected' : 'getFilter';
        const id = this.getMarkerId(type);
        return `url(#${id})`
    }

    private renderNodes() {
        const {radius, stroke} = settings.node;

        return this.state.nodes.map(
            (node, index) => {
                const transform = `translate(${node.x}, ${node.y})`;
                return (
                    <g
                        className="GraphNode"
                        transform={transform}
                    >
                        <circle
                            r={radius}
                            fill={GraphComponent.getNodeFill(node).toString()}
                            strokeWidth={stroke.width}
                            stroke={GraphComponent.getNodeStroke(node).toString()}
                            key={index}
                        />

                        {
                            GraphComponent.linkify(node.link,
                                <text
                                    fill={GraphComponent.getNodeTextFill(node).toString()}
                                    fontWeight={GraphComponent.getNodeTextFontWeight(node)}
                                    style={{textShadow: GraphComponent.getNodeTextShadow(node)}}
                                >
                                    {node.text}
                                </text>)
                        }
                    </g>
                );
            }
        );
    }
}
